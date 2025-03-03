package org.borja.springcloud.msvc.account.application.account.dtos;

import lombok.Builder;
import lombok.Data;
import org.borja.springcloud.msvc.account.domain.enums.AccountType;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponseDto {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private Long clientId;
}