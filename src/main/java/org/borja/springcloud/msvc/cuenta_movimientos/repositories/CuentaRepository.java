package org.borja.springcloud.msvc.cuenta_movimientos.repositories;

import org.borja.springcloud.msvc.cuenta_movimientos.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {

}