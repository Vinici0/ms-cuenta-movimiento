package org.borja.springcloud.msvc.cuenta_movimientos.dto.cliente;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponseDto {

    private Long id;
    private String nombre;
    private String genero;
    private Integer edad;
    private String identificacion;
    private String direccion;
    private String telefono;
    private String clienteId;
    private Boolean estado;
}