package org.borja.springcloud.msvc.account.dtos.account;

import lombok.Builder;
import lombok.Data;
import org.borja.springcloud.msvc.account.models.enums.AccountType;

@Data
@Builder
public class AccountResponseDto {
    private String accountNumber;
    private AccountType accountType;
    private Double initialBalance;
    private Long clientId;
}