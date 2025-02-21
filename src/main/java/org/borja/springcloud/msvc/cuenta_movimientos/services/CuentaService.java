package org.borja.springcloud.msvc.cuenta_movimientos.services;

import jakarta.transaction.Transactional;
import org.borja.springcloud.msvc.cuenta_movimientos.dto.cuenta.CuentaRequestDto;
import org.borja.springcloud.msvc.cuenta_movimientos.dto.cuenta.CuentaResponseDto;
import org.borja.springcloud.msvc.cuenta_movimientos.event.ClienteEvent;
import org.borja.springcloud.msvc.cuenta_movimientos.models.Cuenta;
import org.borja.springcloud.msvc.cuenta_movimientos.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    privateKafkaProducerService kafkaProducerService;

    @Transactional
    public CuentaResponseDto crearCuenta(CuentaRequestDto cuentaDto) {

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuentaDto.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDto.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDto.getSaldoInicial());
        cuenta.setEstado(cuentaDto.getEstado());
        cuenta.setClienteId(cuentaDto.getClienteId());

        cuenta = cuentaRepository.save(cuenta);

        ClienteEvent event = new ClienteEvent(cuentaDto.getClienteId());
        kafkaProducerService.sendClienteEvent(event);

        return mapToResponseDto(cuenta);
    }

    private CuentaResponseDto mapToResponseDto(Cuenta cuenta) {
        return CuentaResponseDto.builder()
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .build();
    }

    // Otros métodos...
}
