package org.borja.springcloud.msvc.account.dtos.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.borja.springcloud.msvc.account.models.enums.Gender;

@Data
@Builder
public class ClientRequestDto {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    private Gender gender;

    @NotNull
    private Integer age;

    @NotBlank
    private String identification;

    @NotBlank
    private String clientId;

    private String address;
    private String phone;

    @NotBlank
    private String password;

}