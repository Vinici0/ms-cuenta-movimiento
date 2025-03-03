package org.borja.springcloud.msvc.account.infrastructure.persistences.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("movements")
public class MovementEntity {
    @Id
    private Long id;
    private LocalDate date;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    @Column("account_id")
    private Long accountId;
}