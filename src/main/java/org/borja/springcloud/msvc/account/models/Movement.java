package org.borja.springcloud.msvc.account.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("movements")
public class Movement {
    @Id
    private Long id;

    private LocalDate date;
    private String movementType;
    private Double amount;
    private Double balance;

    @MappedCollection(idColumn = "account_id")
    private Account account;
}