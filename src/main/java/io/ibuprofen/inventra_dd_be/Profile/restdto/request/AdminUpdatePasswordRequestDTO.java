package io.ibuprofen.inventra_dd_be.Profile.restdto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdatePasswordRequestDTO {

    @NotBlank(message = "Password baru tidak boleh kosong")
    @JsonProperty("new_password")
    private String newPassword;

    @NotBlank(message = "Konfirmasi password tidak boleh kosong")
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
