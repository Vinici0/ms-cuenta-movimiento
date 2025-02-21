package org.borja.springcloud.msvc.cuenta_movimientos.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteRequestDto {

    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    private String genero;       // Ej. "Masculino", "Femenino", etc.

    @NotNull
    private Integer edad;

    @NotBlank
    private String identificacion;

    private String direccion;
    private String telefono;

    @NotBlank
    private String clienteId;

    @NotBlank
    private String contrasena;

    @NotNull
    private Boolean estado;
}