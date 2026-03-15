package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restcontroller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetDetailResponse;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.PenggantianBarangRusakRequestDTO;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.PenggantianBarangRusakResponseDTO;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.service.PenggantianBarangRusakService;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.tinjauPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/penggantian")
@RequiredArgsConstructor
public class PenggantianBarangRusakController {
    @Autowired
    private  PenggantianBarangRusakService penggantianBarangRusakService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('GURU','SISWA','ADMIN')")
    public BaseResponseDTO<List<PenggantianBarangRusakResponseDTO>> getAll(
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "search", required = false) String search
    ) {    
        var result = penggantianBarangRusakService.getAll(search, status);
        return BaseResponseDTO.ok(result, "Berhasil mengambil semua data penggantian barang rusak");
    }

    @GetMapping("/{idPenggantian}")
    public BaseResponseDTO<PenggantianBarangRusakResponseDTO> getById(
            @PathVariable String idPenggantian) {

        var data = penggantianBarangRusakService.getById(idPenggantian);
        return BaseResponseDTO.ok(data, "Data pengajuan penggantian barang rusak berhasil diambil");

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('GURU', 'SISWA', 'ADMIN')")
    public BaseResponseDTO<PenggantianBarangRusakResponseDTO> createPengajuan(@Valid @ModelAttribute PenggantianBarangRusakRequestDTO request) {
        PenggantianBarangRusakResponseDTO result = penggantianBarangRusakService.createPengajuan(request, request.getContohBarang());
        return BaseResponseDTO.created(result, "Pengajuan penggantian barang rusak berhasil diajukan"); 
    }

    @PutMapping(value = "/{idPenggantian}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('GURU','SISWA', 'ADMIN')")
    public BaseResponseDTO<PenggantianBarangRusakResponseDTO> updatePengajuan(
            @PathVariable String idPenggantian,
            @Valid @ModelAttribute PenggantianBarangRusakRequestDTO request) {

        PenggantianBarangRusakResponseDTO result =
                penggantianBarangRusakService.updatePengajuan(
                        idPenggantian,
                        request,
                        request.getContohBarang()
                );

        return BaseResponseDTO.ok(result, "Pengajuan penggantian barang rusak berhasil diperbarui");
    }

    @DeleteMapping("/{idPenggantian}")
    @PreAuthorize("hasAnyAuthority('GURU','SISWA', 'ADMIN')")
    public BaseResponseDTO<Void> deletePengajuan(
            @PathVariable String idPenggantian) {

        penggantianBarangRusakService.deletePengajuan(idPenggantian);

        return BaseResponseDTO.ok(null, "Pengajuan penggantian barang rusak berhasil dihapus");
    }

}
