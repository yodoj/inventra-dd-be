package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePengadaanAsetRequestDTO {

    @NotBlank(message = "Nama aset tidak boleh kosong") 
    private String namaAset;

    @NotNull(message = "Kategori aset tidak boleh kosong") 
    private KategoriAset kategoriAset;

    @NotBlank(message = "Merk tidak boleh kosong")
    private String merk;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas harus lebih besar dari 0") 
    private Integer qty;

    // Estimasi harga per unit dalam satuan Rupiah
    @NotNull(message = "Estimasi harga tidak boleh kosong")
    @Min(value = 1, message = "Estimasi harga harus lebih besar dari 0") 
    private Long estimasiHarga;

    // Tanggal rencana pengadaan, format: yyyy-MM-dd dan harus di masa depan
    @NotNull(message = "Waktu pengadaan tidak boleh kosong")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Future(message = "Tanggal pengadaan tidak boleh hari ini atau lampau")
    private LocalDate waktuPengadaan;
    
    @NotBlank(message = "Link gambar tidak boleh kosong") 
    @URL(message = "Format link gambar tidak valid (harus http/https)")
    private String linkGambar;

    private String unit; 
}