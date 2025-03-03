package org.borja.springcloud.msvc.account.application.movement.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MovementRequestDto {

    @NotNull(message = "El número de cuenta es obligatorio")
    private String accountNumber;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private String movementType;

    @NotNull(message = "El monto es obligatorio")
    private BigDecimal amount;
}