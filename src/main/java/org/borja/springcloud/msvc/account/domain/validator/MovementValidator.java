package org.borja.springcloud.msvc.account.domain.validator;

import org.borja.springcloud.msvc.account.exceptions.InsufficientBalanceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MovementValidator {
    public void validateBalance(BigDecimal currentBalance, BigDecimal amount, BigDecimal newBalance) {
        if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Current: %.2f, Requested: %.2f", currentBalance, amount)
            );
        }
    }
}