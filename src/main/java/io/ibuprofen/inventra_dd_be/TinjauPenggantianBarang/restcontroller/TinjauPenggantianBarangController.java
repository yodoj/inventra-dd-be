package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.request.TinjauPenggantianBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.response.TinjauPenggantianBarangResponseDTO;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.service.TinjauPenggantianBarangService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/penggantian/tinjau")
@RequiredArgsConstructor
public class TinjauPenggantianBarangController {

  @Autowired
  private  TinjauPenggantianBarangService tinjauService;

  @GetMapping("/all")
  @PreAuthorize("hasAnyAuthority('SARPRAS','ADMIN')")
  public BaseResponseDTO<List<TinjauPenggantianBarangResponseDTO>> getAll(
    @RequestParam(name = "status_penggantian", required = false) Status statusPenggantian,
    @RequestParam(name = "search", required = false) String search
) {    
    var result = tinjauService.getAll(statusPenggantian, search);
    return BaseResponseDTO.ok(result, "Berhasil mengambil semua data tinjauan");
  }

  @GetMapping("/{penggantianId}")
  @PreAuthorize("hasAnyAuthority('SARPRAS','ADMIN')")
  public BaseResponseDTO<TinjauPenggantianBarangResponseDTO> getByPenggantianId(@PathVariable String penggantianId) {
    var result = tinjauService.getByPenggantianId(penggantianId);
    return BaseResponseDTO.ok(result, "Data peninjauan berhasil diambil");
  }

  @PostMapping("/{penggantianId}")
  @PreAuthorize("hasAnyAuthority('SARPRAS','ADMIN')")
  public BaseResponseDTO<TinjauPenggantianBarangResponseDTO> create(
      @PathVariable String penggantianId,
      @Valid @RequestBody TinjauPenggantianBarangRequestDTO request
  ) {
    var result = tinjauService.create(penggantianId, request);
    return BaseResponseDTO.ok(result, "Peninjauan berhasil dibuat");
  }

  @PutMapping("/{penggantianId}")
  @PreAuthorize("hasAnyAuthority('SARPRAS','ADMIN')")
  public BaseResponseDTO<TinjauPenggantianBarangResponseDTO> update(
      @PathVariable String penggantianId,
      @Valid @RequestBody TinjauPenggantianBarangRequestDTO request
  ) {
    var result = tinjauService.update(penggantianId, request);
    return BaseResponseDTO.ok(result, "Peninjauan berhasil diperbarui");
  }

  
  
}