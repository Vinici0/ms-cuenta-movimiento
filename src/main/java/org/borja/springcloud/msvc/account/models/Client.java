package org.borja.springcloud.msvc.account.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Client {
    private Long id;
    private String clientId;
    private String name;
}
