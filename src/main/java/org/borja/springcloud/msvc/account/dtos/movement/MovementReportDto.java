package org.borja.springcloud.msvc.account.dtos.movement;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovementReportDto {
    private String fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipo;
    private Double saldoInicial;
    private Boolean estado;
    private Double movimiento;
    private Double saldoDisponible;
}