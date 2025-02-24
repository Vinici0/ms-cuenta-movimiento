package org.borja.springcloud.msvc.account.dtos.movement;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovementRequestDto {

    @NotNull(message = "El número de cuenta es obligatorio")
    private String accountNumber;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private String movementType;

    @NotNull(message = "El monto es obligatorio")
    private Double amount;
}