package org.borja.springcloud.msvc.cuenta_movimientos.repositories;

import org.borja.springcloud.msvc.cuenta_movimientos.models.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

}