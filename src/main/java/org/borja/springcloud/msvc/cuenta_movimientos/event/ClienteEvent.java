package org.borja.springcloud.msvc.cuenta_movimientos.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteEvent {
    private String clienteId;
}