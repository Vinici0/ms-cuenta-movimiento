package org.borja.springcloud.msvc.account.dtos.account;

import org.borja.springcloud.msvc.account.models.enums.AccountType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRequestDto {

    @NotNull
    private AccountType accountType;

    @NotNull
    private Double initialBalance;

    private Boolean status;

    @Positive
    private Long clientId;
}