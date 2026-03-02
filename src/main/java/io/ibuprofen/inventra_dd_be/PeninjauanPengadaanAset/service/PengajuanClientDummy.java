package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.PengajuanSummary;

@Service
public class PengajuanClientDummy implements PengajuanClient {

  private final List<PengajuanSummary> seedData = List.of(
    new PengajuanSummary(
        1L,
        "Kertas",
        "https://dummyimage.com/600x400",
        "ATK",
        "Sidu",
        2,
        50000,
        LocalDateTime.now(),
        Status.DIAJUKAN
    ),
    new PengajuanSummary(
        2L,
        "Microphone",
        "https://dummyimage.com/600x400",
        "Elektronik",
        "JBL",
        1,
        750000,
        LocalDateTime.now(),
        Status.DISETUJUI_KEPSEK  
    ),
    new PengajuanSummary(
        3L,
        "Printer",
        "https://dummyimage.com/600x400",
        "Elektronik",
        "Canon",
        1,
        2500000,
        LocalDateTime.now(),
        Status.DIAJUKAN
    )
  );

  @Override
  public List<PengajuanSummary> getAll() {
    return seedData;
  }

  @Override
  public PengajuanSummary getById(Long pengadaanId) {
    return seedData.stream()
        .filter(p -> p.getId().equals(pengadaanId))
        .findFirst()
        .orElseThrow(() ->
            new IllegalArgumentException("Pengadaan tidak ditemukan (dummy)."));
  }
}