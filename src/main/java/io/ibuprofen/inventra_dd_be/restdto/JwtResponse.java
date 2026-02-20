package io.ibuprofen.inventra_dd_be.restdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String name;
    private String email;
    private String role;
    private String unit;
}
