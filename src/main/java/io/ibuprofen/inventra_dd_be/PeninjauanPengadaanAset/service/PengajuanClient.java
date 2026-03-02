package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service;

import java.util.List;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.PengajuanSummary;


public interface PengajuanClient {
  PengajuanSummary getById(Long pengajuanId);
  List<PengajuanSummary> getAll();
}
