package org.borja.springcloud.msvc.account.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.borja.springcloud.msvc.account.models.enums.AccountType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.security.SecureRandom;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("accounts")
public class Account {
    @Id
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private Double initialBalance;
    private Boolean status = true;
    private Long clientId;

    public void generateAccountNumber() {
        this.accountNumber = generateRandomAccountNumber();
    }

    private String generateRandomAccountNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }
}