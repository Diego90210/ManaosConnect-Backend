package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.EmpresaCliente;
import com.diego.gestorcasino.repositories.EmpresaClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaClienteService {

    @Autowired
    private EmpresaClienteRepository empresaClienteRepository;

    // MÉTODOS ESTANDARIZADOS (nombres consistentes)
    public EmpresaCliente guardar(EmpresaCliente empresaCliente) {
        if (empresaClienteRepository.findByNit(empresaCliente.getNit()).isPresent()) {
            throw new RuntimeException("Ya existe una empresa con el NIT: " + empresaCliente.getNit());
        }
        return empresaClienteRepository.save(empresaCliente);
    }

    public EmpresaCliente actualizar(String nit, EmpresaCliente detallesEmpresaCliente) {
        EmpresaCliente empresaExistente = empresaClienteRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));

        empresaExistente.setNombre(detallesEmpresaCliente.getNombre());
        empresaExistente.setDireccion(detallesEmpresaCliente.getDireccion());
        empresaExistente.setTelefono(detallesEmpresaCliente.getTelefono());
        empresaExistente.setContacto(detallesEmpresaCliente.getContacto());

        return empresaClienteRepository.save(empresaExistente);
    }

    public void eliminar(String nit) {
        EmpresaCliente empresa = empresaClienteRepository.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));
        empresaClienteRepository.delete(empresa);
    }

    public List<EmpresaCliente> listarTodas() {
        return empresaClienteRepository.findAll();
    }

    public Optional<EmpresaCliente> buscarPorNit(String nit) {
        return empresaClienteRepository.findByNit(nit);
    }

    // MÉTODOS EXISTENTES (mantener compatibilidad)
    public List<EmpresaCliente> obtenerTodasLasEmpresas() {
        return listarTodas();
    }

    public EmpresaCliente obtenerEmpresaPorNit(String nit) {
        return buscarPorNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nit));
    }

    public EmpresaCliente anadirEmpresa(EmpresaCliente empresaCliente) {
        return guardar(empresaCliente);
    }

    public EmpresaCliente actualizarEmpresa(String nit, EmpresaCliente detallesEmpresaCliente) {
        return actualizar(nit, detallesEmpresaCliente);
    }

    public void eliminarEmpresa(String nit) {
        eliminar(nit);
    }
}