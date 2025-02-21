package org.borja.springcloud.msvc.cuenta_movimientos.dto.movimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovimientoRequestDto {

    @NotBlank
    private String tipoMovimiento;

    @NotNull
    private Double valor;

    @NotBlank
    private String numeroCuenta;
}