package io.ibuprofen.inventra_dd_be.Profile.restdto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequestDTO {
    
    @JsonProperty("current_password")
    @NotBlank(message = "Password saat ini tidak boleh kosong")
    private String currentPassword;

    @JsonProperty("new_password")
    @NotBlank(message = "Password baru tidak boleh kosong")
    private String newPassword;

    @JsonProperty("confirm_password")
    @NotBlank(message = "Konfirmasi password tidak boleh kosong")
    private String confirmPassword;
}
