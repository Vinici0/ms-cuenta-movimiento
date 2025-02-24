package org.borja.springcloud.msvc.account.validadors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovementValidator {

    public Boolean isValidBalance(double newBalance) {
        if (newBalance < 0) {
            return false;
        }
        return true;
    }
}