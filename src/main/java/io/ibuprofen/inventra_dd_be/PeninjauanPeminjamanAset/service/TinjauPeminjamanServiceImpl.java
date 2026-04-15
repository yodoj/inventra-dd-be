package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.model.TinjauPeminjaman;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.repository.TinjauPeminjamanRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TinjauPeminjamanServiceImpl implements TinjauPeminjamanService {
    private final TinjauPeminjamanRepository tinjauRepository;
    private final PeminjamanAsetRepository peminjamanRepository;
    private final AsetBarangRepository asetBarangRepository;

    @Override
    public List<TinjauPeminjamanResponseDTO> getAll(User currentUser) {
        List<TinjauPeminjaman> listTinjau;

        if (currentUser.getRole() == Role.ADMIN) {
            listTinjau = tinjauRepository.findAll();
        } else {
            listTinjau = tinjauRepository.findAll().stream()
                    .filter(t -> t.getPeminjaman().getUnitTujuan().equalsIgnoreCase(currentUser.getUnit()))
                    .collect(Collectors.toList());
        }

        return listTinjau.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public TinjauPeminjamanResponseDTO create(UUID peminjamanId, TinjauPeminjamanRequestDTO req, User currentUser) {
        PeminjamanAset peminjaman = peminjamanRepository.findById(peminjamanId)
                .orElseThrow(() -> new RuntimeException("Data Peminjaman tidak ditemukan"));

        peminjaman.setStatusPeminjaman(req.getStatusPeminjaman());
        
        if (req.getStatusPeminjaman() == PeminjamanAset.StatusPeminjaman.DISETUJUI) {
            if (peminjaman.getAset() instanceof AsetBarang) {
                AsetBarang barang = (AsetBarang) peminjaman.getAset();
                if (barang.getQtyTersedia() < peminjaman.getQty()) {
                    throw new RuntimeException("Stok barang tidak mencukupi!");
                }
                barang.setQtyTersedia(barang.getQtyTersedia() - peminjaman.getQty());
                asetBarangRepository.save(barang);
            }
        }

        TinjauPeminjaman tinjau = TinjauPeminjaman.builder()
                .peninjau(currentUser)
                .peminjaman(peminjaman)
                .rolePeninjau(currentUser.getRole())
                .statusPeminjaman(req.getStatusPeminjaman())
                .alasan(req.getAlasan())
                .build();

        return mapToDTO(tinjauRepository.save(tinjau));
    }

    private TinjauPeminjamanResponseDTO mapToDTO(TinjauPeminjaman tinjau) {
        PeminjamanAset peminjaman = tinjau.getPeminjaman();
        User peminjam = peminjaman.getPeminjam();
        AsetBarang aset = (AsetBarang) peminjaman.getAset();

        return TinjauPeminjamanResponseDTO.builder()
                .idPeninjauan(tinjau != null ? tinjau.getIdPeninjauan() : null)
                .idPeminjaman(peminjaman.getId())
                .idPeminjam(peminjam.getId())
                .namaPeminjam(peminjam.getName())
                .rolePeminjam(peminjam.getRole())
                .unitAsal(peminjam.getUnit())
                .idAset(aset.getId())
                .kodeAset(aset.getKodeAset())
                .namaAset(aset.getNamaAset())
                .merkAset(aset.getMerkAset())
                .kategoriAset(aset.getKategoriAset())
                .qty(peminjaman.getQty())
                .waktuPeminjaman(peminjaman.getWaktuPeminjaman())
                .waktuPengembalian(peminjaman.getWaktuPengembalian())
                .waktuPengajuan(peminjaman.getWaktuPengajuan())
                .tujuanPeminjaman(peminjaman.getTujuanPeminjaman())
                .unitTujuan(peminjaman.getUnitTujuan())
                .statusPeminjaman(tinjau.getStatusPeminjaman())
                .alasan(tinjau != null ? tinjau.getAlasan() : null)
                .idPeninjau(tinjau != null && tinjau.getPeninjau() != null ? tinjau.getPeninjau().getId() : null)
                .rolePeninjau(tinjau != null ? tinjau.getRolePeninjau() : null)
                .createdAt(tinjau != null ? tinjau.getCreatedAt() : null)
                .updatedAt(tinjau != null ? tinjau.getUpdatedAt() : null)
                .build();
    }
}