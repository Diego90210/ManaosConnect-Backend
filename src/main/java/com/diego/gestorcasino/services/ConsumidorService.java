package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Consumidor;
import com.diego.gestorcasino.repositories.ConsumidorRepository;
import com.diego.gestorcasino.repositories.EmpresaClienteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ConsumidorService {

    @Autowired
    private ConsumidorRepository consumidorRepository;

    @Autowired
    private EmpresaClienteRepository empresaClienteRepository; // Agrega el repositorio de Empresa

    @Value("${directorio.imagenes}")
    private String directorioImagenes;

    @PostConstruct
    public void verificarDirectorioImagenes() {
        File directorio = new File(directorioImagenes);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    // Obtener todos los empleados
    public List<Consumidor> obtenerTodosLosConsumidores() {
        return consumidorRepository.findAll();
    }

    public void guardarImagen(String cedula, MultipartFile archivo) throws IOException {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con cédula: " + cedula));

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

    // Obtener un empleado por cédula
    public Consumidor obtenerConsumidorPorCedula(String cedula) {
        return consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));
    }

    // Actualizar un empleado
    public Consumidor actualizarConsumidor(String cedula, Consumidor detallesConsumidor) {
        Consumidor consumidorExistente = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));


        // Validar si la empresa existe
        empresaClienteRepository.findByNit(detallesConsumidor.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + detallesConsumidor.getEmpresaNIT()));

        consumidorExistente.setNombre(detallesConsumidor.getNombre());
        consumidorExistente.setEmpresaNIT(detallesConsumidor.getEmpresaNIT());
        consumidorExistente.setTelefono(detallesConsumidor.getTelefono());

        return consumidorRepository.save(consumidorExistente);
    }

    // Borrar un empleado
    public void eliminarConsumidor(String cedula) {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));
        consumidorRepository.delete(consumidor);
    }

    public void eliminarImagen(String cedula) {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));

        if (consumidor.getRutaImagen() != null) {
            Path rutaImagen = Paths.get(consumidor.getRutaImagen());
            try {
                // Elimina físicamente la imagen
                if (Files.exists(rutaImagen)) {
                    Files.delete(rutaImagen);
                } else {
                    throw new RuntimeException("El archivo no existe en la ruta especificada: " + rutaImagen);
                }
                // Elimina la referencia a la imagen en la base de datos
                consumidor.setRutaImagen(null);
                consumidorRepository.save(consumidor);
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("El empleado no tiene una imagen asociada");
        }
    }


    public void modificarImagen(String cedula, MultipartFile nuevaImagen) throws IOException {
        Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));

        // Eliminar la imagen existente, si la hay
        if (consumidor.getRutaImagen() != null) {
            Path rutaImagenAnterior = Paths.get(consumidor.getRutaImagen());
            Files.deleteIfExists(rutaImagenAnterior);
        }

        // Guardar la nueva imagen
        String nombreArchivo = cedula + "_" + nuevaImagen.getOriginalFilename();
        Path rutaNuevaImagen = Paths.get("C:/imagenes_rostros/" + nombreArchivo);
        Files.write(rutaNuevaImagen, nuevaImagen.getBytes());

        // Actualizar la ruta en el empleado
        consumidor.setRutaImagen(rutaNuevaImagen.toString());
        consumidorRepository.save(consumidor);
    }

    public Consumidor anadirConsumidorConImagen(Consumidor consumidor, MultipartFile imagen) throws IOException {
        // Validar si la empresa existe
        empresaClienteRepository.findByNit(consumidor.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + consumidor.getEmpresaNIT()));

        // Validar si ya existe un empleado con la misma cédula
        if (consumidorRepository.findByCedula(consumidor.getCedula()).isPresent()) {
            throw new RuntimeException("Ya existe un empleado con la cédula: " + consumidor.getCedula());
        }

        // Guardar la imagen en el sistema de archivos
        String nombreArchivo = consumidor.getCedula() + "_" + imagen.getOriginalFilename();
        Path rutaImagen = Paths.get("C:/imagenes_rostros/" + nombreArchivo);
        Files.write(rutaImagen, imagen.getBytes());

        // Asociar la ruta de la imagen al empleado
        consumidor.setRutaImagen(rutaImagen.toString());

        // Guardar el empleado en la base de datos
        return consumidorRepository.save(consumidor);
    }

}


