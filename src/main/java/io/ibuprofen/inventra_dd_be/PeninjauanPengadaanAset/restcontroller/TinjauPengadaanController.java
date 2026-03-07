package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restcontroller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request.tinjauPengadaanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.tinjauPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service.TinjauPengadaanService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pengadaan/tinjau")
@RequiredArgsConstructor
public class TinjauPengadaanController {

  @Autowired
  private  TinjauPengadaanService tinjauService;

  @GetMapping("/all")
  @PreAuthorize("hasAnyAuthority('KEPSEK','YAYASAN','ADMIN')")
  public BaseResponseDTO<List<tinjauPengadaanResponseDTO>> getAll() {
    var result = tinjauService.getAll();
    return BaseResponseDTO.ok(result, "Berhasil mengambil semua data tinjauan");
  }

  @GetMapping("/{pengadaanId}")
  @PreAuthorize("hasAnyAuthority('KEPSEK','YAYASAN','ADMIN')")
  public BaseResponseDTO<tinjauPengadaanResponseDTO> getByPengadaanId(@PathVariable UUID pengadaanId) {
    var result = tinjauService.getByPengadaanId(pengadaanId);
    return BaseResponseDTO.ok(result, "Data peninjauan berhasil diambil");
  }

  @PostMapping("/{pengadaanId}")
  @PreAuthorize("hasAnyAuthority('KEPSEK','YAYASAN','ADMIN')")
  public BaseResponseDTO<tinjauPengadaanResponseDTO> create(
      @PathVariable UUID pengadaanId,
      @Valid @RequestBody tinjauPengadaanRequestDTO request
  ) {
    var result = tinjauService.create(pengadaanId, request);
    return BaseResponseDTO.ok(result, "Peninjauan berhasil dibuat");
  }

  @PutMapping("/update/{pengadaanId}")
  @PreAuthorize("hasAnyAuthority('KEPSEK','YAYASAN','ADMIN')")
  public BaseResponseDTO<tinjauPengadaanResponseDTO> update(
      @PathVariable UUID pengadaanId,
      @Valid @RequestBody tinjauPengadaanRequestDTO request
  ) {
    var result = tinjauService.update(pengadaanId, request);
    return BaseResponseDTO.ok(result, "Peninjauan berhasil diperbarui");
  }

  @PostMapping(value = "/bukti/{pengadaanId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyAuthority('YAYASAN','ADMIN')")
  public BaseResponseDTO<tinjauPengadaanResponseDTO> beli(
          @PathVariable UUID pengadaanId,
          @RequestParam("harga") Long harga,
          @RequestParam("buktiPembelian") MultipartFile file) {
      
      var result = tinjauService.beli(pengadaanId, harga, file);
      
      return BaseResponseDTO.ok(result, "Pembelian berhasil diproses dan aset telah dicatat");
  }

  
}