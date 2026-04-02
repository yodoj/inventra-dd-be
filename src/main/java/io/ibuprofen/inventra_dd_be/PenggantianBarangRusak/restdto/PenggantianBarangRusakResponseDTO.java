package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenggantianBarangRusakResponseDTO {
    private String idPenggantian;
    private String namaBarang;
    private LocalDate waktuPenggantian;
    private Integer quantity;
    private String merk;
    private String contohBarang;
    private String status;
    private String keterangan;

    private String namaPengaju;
    private String unitPengaju;
    private String rolePengaju; 


}