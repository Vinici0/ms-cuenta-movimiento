package org.borja.springcloud.msvc.account.dtos.movement;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MovementResponseDto {
    private Long id;
    private LocalDate date;
    private String movementType;
    private Double amount;
    private Double balance;
    private String accountNumber;
}