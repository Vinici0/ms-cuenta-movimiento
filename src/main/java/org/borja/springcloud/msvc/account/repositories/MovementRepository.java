package org.borja.springcloud.msvc.account.repositories;

import org.borja.springcloud.msvc.account.models.Movement;
import org.borja.springcloud.msvc.account.repositories.interfaces.MovementReportProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface MovementRepository extends ReactiveCrudRepository<Movement, Long> {

    @Query("""
            SELECT
                TO_CHAR(m.date, 'DD/MM/YYYY') AS fecha,
                p.name AS cliente,
                a.account_number AS numeroCuenta,
                a.account_type AS tipo,
                a.initial_balance AS saldoInicial,
                a.status AS estado,
                m.amount AS movimiento,
                m.balance AS saldoDisponible
            FROM movements m
                INNER JOIN accounts a ON m.account_id = a.id
                INNER JOIN clients c ON a.client_id = c.id
                INNER JOIN personas p ON c.id = p.id
            WHERE m.date BETWEEN :startDate AND :endDate AND c.id = :clientId
            ORDER BY m.id DESC
            """)
    Flux<MovementReportProjection> findAllInRangeNative(
            LocalDate startDate,
            LocalDate endDate,
            Long clientId
    );
}