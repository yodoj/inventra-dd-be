package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class tinjauPengadaanResponseDTO {
    private Long id;
    private Long idPengadaan;

    private String namaAset;
    private String linkGambar;
    private String kategori;
    private String merk;
    private int qty;
    private int estimasiHarga;
    private LocalDateTime waktuPengadaan;

    private Status status;
    private String alasan;

    private LocalDateTime kepsekFirstReviewedAt;
    private LocalDateTime yayasanFirstReviewedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private UUID userId;
}
