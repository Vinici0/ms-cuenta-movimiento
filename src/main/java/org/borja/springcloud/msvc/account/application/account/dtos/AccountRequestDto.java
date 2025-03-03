package org.borja.springcloud.msvc.account.application.account.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.borja.springcloud.msvc.account.domain.enums.AccountType;

import java.math.BigDecimal;

@Data
@Builder
public class AccountRequestDto {

    @NotNull
    private AccountType accountType;

    @NotNull
    private BigDecimal initialBalance;

    private Boolean status;

    @Positive
    private Long clientId;
}