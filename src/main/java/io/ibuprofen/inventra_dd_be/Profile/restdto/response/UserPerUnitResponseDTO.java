package io.ibuprofen.inventra_dd_be.Profile.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPerUnitResponseDTO {
    
    private UUID id;
    
    private String email;
    
    @JsonProperty("nama_lengkap")
    private String name;
    
    @JsonProperty("nomor_telepon")
    private String phoneNumber;
    
    private String role;
    
    private String unit;
    
    private String nisn;
    
    private String kelas;
}
