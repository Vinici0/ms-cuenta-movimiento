package org.borja.springcloud.msvc.account.application.movement.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MovementResponseDto {
    private Long id;
    private LocalDate date;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String accountNumber;
}