package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Consumidor;
import com.diego.gestorcasino.repositories.ConsumidorRepository;
import com.diego.gestorcasino.repositories.EmpresaClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ConsumidorService {

    @Autowired
    private ConsumidorRepository consumidorRepository;

    @Autowired
    private EmpresaClienteRepository empresaClienteRepository;

    @Value("${directorio.imagenes}")
    private String directorioImagenes;

    @PostConstruct
    public void verificarDirectorioImagenes() {
        File directorio = new File(directorioImagenes);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    // MÉTODOS ESTANDARIZADOS (nombres consistentes)
    @Deprecated(since = "1.0", forRemoval = true)
    public Consumidor guardar(Consumidor consumidor) {
        // Validar si la empresa existe
        empresaClienteRepository.findByNit(consumidor.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + consumidor.getEmpresaNIT()));

        // Validar si ya existe un consumidor con la misma cédula
        if (consumidorRepository.findByCedula(consumidor.getCedula()).isPresent()) {
            throw new RuntimeException("Ya existe un consumidor con la cédula: " + consumidor.getCedula());
        }

        return consumidorRepository.save(consumidor);
    }

    public Consumidor actualizar(String cedula, Consumidor detallesConsumidor) {
        Consumidor consumidorExistente = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedula));

        // Validar si la empresa existe
        empresaClienteRepository.findByNit(detallesConsumidor.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + detallesConsumidor.getEmpresaNIT()));

        consumidorExistente.setNombre(detallesConsumidor.getNombre());
        consumidorExistente.setEmpresaNIT(detallesConsumidor.getEmpresaNIT());
        consumidorExistente.setTelefono(detallesConsumidor.getTelefono());

        return consumidorRepository.save(consumidorExistente);
    }

    public void eliminar(String cedula) {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedula));
        
        // Eliminar imagen asociada si existe
        if (consumidor.getRutaImagen() != null) {
            try {
                eliminarImagenFisica(consumidor.getRutaImagen());
            } catch (Exception e) {
                // Log warning pero no fallar la eliminación
                System.out.println("Warning: No se pudo eliminar la imagen: " + e.getMessage());
            }
        }
        
        consumidorRepository.delete(consumidor);
    }

    public List<Consumidor> listarTodos() {
        return consumidorRepository.findAll();
    }

    public Optional<Consumidor> buscarPorCedula(String cedula) {
        return consumidorRepository.findByCedula(cedula);
    }

    // MÉTODO ESPECÍFICO PARA CAJEROS
    public List<Consumidor> listarPorEmpresa(String nit) {
        return consumidorRepository.findAll().stream()
                .filter(c -> nit.equals(c.getEmpresaNIT()))
                .toList();
    }

    // MÉTODOS EXISTENTES (mantener compatibilidad)
    public List<Consumidor> obtenerTodosLosConsumidores() {
        return listarTodos();
    }

    public Consumidor obtenerConsumidorPorCedula(String cedula) {
        return buscarPorCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedula));
    }

    public Consumidor actualizarConsumidor(String cedula, Consumidor detallesConsumidor) {
        return actualizar(cedula, detallesConsumidor);
    }

    public void eliminarConsumidor(String cedula) {
        eliminar(cedula);
    }

    // MÉTODOS DE GESTIÓN DE IMÁGENES
    public void guardarImagen(String cedula, MultipartFile archivo) throws IOException {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Consumidor no encontrado con cédula: " + cedula));

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new RuntimeException("El archivo debe ser una imagen");
        }

        String nombreArchivo = cedula + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = Paths.get(directorioImagenes, nombreArchivo);
        Files.write(rutaArchivo, archivo.getBytes());

        consumidor.setRutaImagen(rutaArchivo.toAbsolutePath().toString());
        consumidorRepository.save(consumidor);
    }

    public void eliminarImagen(String cedula) {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedula));

        if (consumidor.getRutaImagen() != null) {
            eliminarImagenFisica(consumidor.getRutaImagen());
            consumidor.setRutaImagen(null);
            consumidorRepository.save(consumidor);
        } else {
            throw new RuntimeException("El consumidor no tiene una imagen asociada");
        }
    }

    public void modificarImagen(String cedula, MultipartFile nuevaImagen) throws IOException {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedula));

        // Eliminar la imagen existente, si la hay
        if (consumidor.getRutaImagen() != null) {
            Path rutaImagenAnterior = Paths.get(consumidor.getRutaImagen());
            Files.deleteIfExists(rutaImagenAnterior);
        }

        // Guardar la nueva imagen
        String nombreArchivo = cedula + "_" + nuevaImagen.getOriginalFilename();
        Path rutaNuevaImagen = Paths.get(directorioImagenes, nombreArchivo);
        Files.write(rutaNuevaImagen, nuevaImagen.getBytes());

        // Actualizar la ruta en el consumidor
        consumidor.setRutaImagen(rutaNuevaImagen.toString());
        consumidorRepository.save(consumidor);
    }

    public Consumidor anadirConsumidorConImagen(Consumidor consumidor, MultipartFile imagen) throws IOException {
        // Validar que la imagen sea obligatoria
        if (imagen == null || imagen.isEmpty()) {
            throw new RuntimeException("La imagen es obligatoria para crear un consumidor");
        }
        
        // Guardar el consumidor primero
        Consumidor nuevoConsumidor = guardar(consumidor);

        // Luego guardar la imagen
        guardarImagen(consumidor.getCedula(), imagen);

        return nuevoConsumidor;
    }

    private void eliminarImagenFisica(String rutaImagen) {
        try {
            Path ruta = Paths.get(rutaImagen);
            if (Files.exists(ruta)) {
                Files.delete(ruta);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    public Consumidor guardarConsumidorConImagen(Consumidor consumidor, MultipartFile imagen) throws IOException {
        empresaClienteRepository.findByNit(consumidor.getEmpresaNIT());

        // Validar si ya existe consumidor
        if (consumidorRepository.findByCedula(consumidor.getCedula()).isPresent()) {
            throw new RuntimeException("Ya existe un consumidor con la cédula: " + consumidor.getCedula());
        }

        if (imagen != null && !imagen.isEmpty()) {
            String contentType = imagen.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                throw new RuntimeException("El archivo debe ser una imagen");
            }

            String nombreArchivo = consumidor.getCedula() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Paths.get("C:/imagenes_consumidores", nombreArchivo);
            Files.createDirectories(rutaArchivo.getParent()); // Asegura carpeta
            Files.write(rutaArchivo, imagen.getBytes());

            consumidor.setRutaImagen(rutaArchivo.toAbsolutePath().toString());
        }

        return consumidorRepository.save(consumidor);
    }

    public Consumidor registrarConsumidor(String cedula, String nombre, String telefono, String empresaNIT, MultipartFile imagen) throws IOException {
        // Validar que exista la empresa
        if (!empresaClienteRepository.findByNit(empresaNIT).isPresent()) {
            throw new RuntimeException("No se encontró la empresa con NIT: " + empresaNIT);
        }

        Consumidor consumidor = new Consumidor();
        consumidor.setCedula(cedula);
        consumidor.setNombre(nombre);
        consumidor.setTelefono(telefono);
        consumidor.setEmpresaNIT(empresaNIT);

        return guardarConsumidorConImagen(consumidor, imagen);
    }


}