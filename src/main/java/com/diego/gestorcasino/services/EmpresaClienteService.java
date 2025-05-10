package com.diego.gestorcasino.services;

import com.diego.gestorcasino.repositories.EmpresaClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaClienteService {

    @Autowired
    private EmpresaClienteRepository empresaClienteRepository;

    // Obtener todas las empresas
    public List<com.diego.gestorcasino.models.EmpresaCliente> obtenerTodasLasEmpresas() {
        return empresaClienteRepository.findAll();
    }

    // Obtener una empresa por NIT
    public com.diego.gestorcasino.models.EmpresaCliente obtenerEmpresaPorNit(String nit) {
        return empresaClienteRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));
    }

    // anadir una nueva empresa
    public com.diego.gestorcasino.models.EmpresaCliente anadirEmpresa(com.diego.gestorcasino.models.EmpresaCliente empresaCliente) {
        if (this.empresaClienteRepository.findByNit(empresaCliente.getNit()).isPresent()){
            throw new RuntimeException("Ya existe una empresa con el NIT: " + empresaCliente.getNit());
        }
        return this.empresaClienteRepository.save(empresaCliente);
    }

    // Actualizar una empresa existente
    public com.diego.gestorcasino.models.EmpresaCliente actualizarEmpresa(String nit, com.diego.gestorcasino.models.EmpresaCliente detallesEmpresaCliente) {
        com.diego.gestorcasino.models.EmpresaCliente empresaClienteExistente = empresaClienteRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));

        empresaClienteExistente.setNombre(detallesEmpresaCliente.getNombre());
        empresaClienteExistente.setDireccion(detallesEmpresaCliente.getDireccion());
        empresaClienteExistente.setTelefono(detallesEmpresaCliente.getTelefono());
        empresaClienteExistente.setContacto(detallesEmpresaCliente.getContacto());
        // anadir cualquier otro campo que se desee actualizar

        return empresaClienteRepository.save(empresaClienteExistente);
    }

    // Eliminar una empresa
    public void eliminarEmpresa(String nit) {
        com.diego.gestorcasino.models.EmpresaCliente empresaCliente = this.empresaClienteRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));
        this.empresaClienteRepository.delete(empresaCliente);
    }
}

