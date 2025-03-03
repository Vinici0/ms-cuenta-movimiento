package org.borja.springcloud.msvc.account.infrastructure.persistences.entities;

import lombok.*;
import org.borja.springcloud.msvc.account.domain.enums.AccountType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("accounts")
public class AccountEntity {
    @Id
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private Boolean status;
    private Long clientId;
}