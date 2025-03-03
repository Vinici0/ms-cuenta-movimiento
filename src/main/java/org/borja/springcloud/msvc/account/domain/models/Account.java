package org.borja.springcloud.msvc.account.domain.models;

import lombok.*;
import org.borja.springcloud.msvc.account.domain.enums.AccountType;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private Boolean status = true;
    private Long clientId;

    public void generateAccountNumber() {
        this.accountNumber = generateRandomAccountNumber();
    }

    private String generateRandomAccountNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }
}