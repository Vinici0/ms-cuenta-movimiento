package org.borja.springcloud.msvc.account.domain.models;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movement {
    private Long id;
    private LocalDate date;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    private Long accountId;
}