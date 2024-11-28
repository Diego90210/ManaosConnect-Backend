package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Empleado;
import com.diego.gestorcasino.repositories.EmpleadoRepository;
import com.diego.gestorcasino.repositories.EmpresaRepository;
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
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EmpresaRepository empresaRepository; // Agrega el repositorio de Empresa

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
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll();
    }

    public void guardarImagen(String cedula, MultipartFile archivo) throws IOException {
        Empleado empleado = empleadoRepository.findByCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con cédula: " + cedula));

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new RuntimeException("El archivo debe ser una imagen");
        }

        String nombreArchivo = cedula + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = Paths.get(directorioImagenes, nombreArchivo);
        Files.write(rutaArchivo, archivo.getBytes());

        empleado.setRutaImagen(rutaArchivo.toAbsolutePath().toString());
        empleadoRepository.save(empleado);
    }

    // Obtener un empleado por cédula
    public Empleado obtenerEmpleadoPorCedula(String cedula) {
        return empleadoRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));
    }

    // anadir un nuevo empleado
    public Empleado anadirEmpleado(Empleado empleado) {
        // Validar si la empresa existe por NIT
        empresaRepository.findByNit(empleado.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + empleado.getEmpresaNIT()));

        if (empleadoRepository.findByCedula(empleado.getCedula()).isPresent()) {
            throw new RuntimeException("Ya existe un empleado con la cédula: " + empleado.getCedula());
        }

        return empleadoRepository.save(empleado);
    }

    // Actualizar un empleado
    public Empleado actualizarEmpleado(String cedula, Empleado detallesEmpleado) {
        Empleado empleadoExistente = empleadoRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));


        // Validar si la empresa existe
        empresaRepository.findByNit(detallesEmpleado.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + detallesEmpleado.getEmpresaNIT()));

        empleadoExistente.setNombre(detallesEmpleado.getNombre());
        empleadoExistente.setEmpresaNIT(detallesEmpleado.getEmpresaNIT());
        empleadoExistente.setTelefono(detallesEmpleado.getTelefono());

        return empleadoRepository.save(empleadoExistente);
    }

    // Borrar un empleado
    public void eliminarEmpleado(String cedula) {
        Empleado empleado = empleadoRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));
        empleadoRepository.delete(empleado);
    }

    public void eliminarImagen(String cedula) {
        Empleado empleado = empleadoRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));

        if (empleado.getRutaImagen() != null) {
            Path rutaImagen = Paths.get(empleado.getRutaImagen());
            try {
                Files.deleteIfExists(rutaImagen);
                empleado.setRutaImagen(null); // Limpiar la ruta en la base de datos
                empleadoRepository.save(empleado);
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("El empleado no tiene una imagen asociada");
        }
    }

    public void modificarImagen(String cedula, MultipartFile nuevaImagen) throws IOException {
        Empleado empleado = empleadoRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));

        // Eliminar la imagen existente, si la hay
        if (empleado.getRutaImagen() != null) {
            Path rutaImagenAnterior = Paths.get(empleado.getRutaImagen());
            Files.deleteIfExists(rutaImagenAnterior);
        }

        // Guardar la nueva imagen
        String nombreArchivo = cedula + "_" + nuevaImagen.getOriginalFilename();
        Path rutaNuevaImagen = Paths.get("C:/imagenes_rostros/" + nombreArchivo);
        Files.write(rutaNuevaImagen, nuevaImagen.getBytes());

        // Actualizar la ruta en el empleado
        empleado.setRutaImagen(rutaNuevaImagen.toString());
        empleadoRepository.save(empleado);
    }


}


