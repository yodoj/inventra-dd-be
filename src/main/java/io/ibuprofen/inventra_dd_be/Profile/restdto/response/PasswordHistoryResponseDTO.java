package io.ibuprofen.inventra_dd_be.Profile.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordHistoryResponseDTO {
    @JsonProperty("changed_at")
    private LocalDateTime changedAt;

    @JsonProperty("changed_by_full_name")
    private String changedByFullName;

    @JsonProperty("changed_by_role")
    private String changedByRole;
}
