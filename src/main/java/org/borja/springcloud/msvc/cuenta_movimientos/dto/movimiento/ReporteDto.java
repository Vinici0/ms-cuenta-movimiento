package org.borja.springcloud.msvc.cuenta_movimientos.dto.movimiento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteDto {
    private Long clienteId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CuentaDto> cuentas;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CuentaDto {
        private String numeroCuenta;
        private Double saldoActual;
        private List<MovimientoDto> movimientos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovimientoDto {
        private LocalDate fecha;
        private String tipoMovimiento;
        private Double valor;
        private Double saldo;
    }
}