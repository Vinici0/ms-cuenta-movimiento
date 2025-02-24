package org.borja.springcloud.msvc.account.dtos.movement;

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
public class ReportDto {
    private Long clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountDto> accounts;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountDto {
        private String accountNumber;
        private Double currentBalance;
        private List<MovementDto> movements;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovementDto {
        private LocalDate date;
        private String movementType;
        private Double value;
        private Double balance;
    }
}