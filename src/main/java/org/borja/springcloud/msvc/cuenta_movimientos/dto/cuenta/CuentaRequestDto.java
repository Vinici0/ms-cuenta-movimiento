package org.borja.springcloud.msvc.cuenta_movimientos.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.borja.springcloud.msvc.cuenta_movimientos.models.enums.TipoCuenta;

@Data
@Builder
public class CuentaRequestDto {

    @NotBlank
    private String numeroCuenta;

    @NotNull
    private TipoCuenta tipoCuenta;

    @NotNull
    private Double saldoInicial;

    @NotNull
    private Boolean estado;

    @Positive
    private String clienteId;
}