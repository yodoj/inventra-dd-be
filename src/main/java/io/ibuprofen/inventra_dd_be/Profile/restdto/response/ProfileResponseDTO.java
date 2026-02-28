package io.ibuprofen.inventra_dd_be.Profile.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String role;
    private String unit;
    private String phoneNumber;
    private String password;
    private String nisn;
    private String kelas;
}
