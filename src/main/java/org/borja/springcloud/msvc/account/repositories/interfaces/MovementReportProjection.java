package org.borja.springcloud.msvc.account.repositories.interfaces;

public interface MovementReportProjection {
    String getFecha();
    String getCliente();
    String getNumeroCuenta();
    String getTipo();
    Double getSaldoInicial();
    Boolean getEstado();
    Double getMovimiento();
    Double getSaldoDisponible();
}
