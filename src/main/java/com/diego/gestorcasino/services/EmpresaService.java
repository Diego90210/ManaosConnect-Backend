package com.diego.gestorcasino.services;

import com.diego.gestorcasino.repositories.EmpresaRepository;
import com.diego.gestorcasino.models.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    // Obtener todas las empresas
    public List<Empresa> obtenerTodasLasEmpresas() {
        return empresaRepository.findAll();
    }

    // Obtener una empresa por NIT
    public Empresa obtenerEmpresaPorNit(String nit) {
        return empresaRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));
    }

    // anadir una nueva empresa
    public Empresa anadirEmpresa(Empresa empresa) {
        if (empresaRepository.findByNit(empresa.getNit()).isPresent()){
            throw new RuntimeException("Ya existe una empresa con el NIT: " + empresa.getNit());
        }
        return empresaRepository.save(empresa);
    }

    // Actualizar una empresa existente
    public Empresa actualizarEmpresa(String nit, Empresa detallesEmpresa) {
        Empresa empresaExistente = empresaRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));

        empresaExistente.setNombre(detallesEmpresa.getNombre());
        empresaExistente.setDireccion(detallesEmpresa.getDireccion());
        empresaExistente.setTelefono(detallesEmpresa.getTelefono());
        empresaExistente.setContacto(detallesEmpresa.getContacto());
        // anadir cualquier otro campo que se desee actualizar

        return empresaRepository.save(empresaExistente);
    }

    // Eliminar una empresa
    public void eliminarEmpresa(String nit) {
        Empresa empresa = empresaRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));
        empresaRepository.delete(empresa);
    }
}

