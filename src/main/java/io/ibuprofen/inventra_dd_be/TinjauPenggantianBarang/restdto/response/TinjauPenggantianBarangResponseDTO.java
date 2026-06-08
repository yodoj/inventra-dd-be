package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.Status;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinjauPenggantianBarangResponseDTO {
    private UUID id;
    private String idPenggantian;

    private String namaAset;
    private String linkGambar;
    private String merk;
    private int qty;
    private LocalDate waktuPenggantian;
    private String namaPengaju;
    private Status statusPenggantian;
    private String unitPengaju;
    private Role rolePengaju;

    private String alasan;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private UUID userId;
    private String reviewerRole;
    private String namaReviewer;
}
