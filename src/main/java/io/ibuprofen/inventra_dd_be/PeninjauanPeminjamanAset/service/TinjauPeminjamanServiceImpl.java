package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetRuangan;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetRuanganRepository;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.model.TinjauPeminjaman;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.repository.TinjauPeminjamanRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TinjauPeminjamanServiceImpl implements TinjauPeminjamanService {
    @Autowired
    private TinjauPeminjamanRepository tinjauRepository;

    @Autowired
    private PeminjamanAsetRepository peminjamanRepository;

    @Autowired
    private AsetBarangRepository asetBarangRepository;

    @Autowired
    private AsetRuanganRepository asetRuanganRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<TinjauPeminjamanResponseDTO> getAll() {
        UserDetailsImpl userDetails = getCurrentUser();
        List<PeminjamanAset> daftarPeminjaman;

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            daftarPeminjaman = peminjamanRepository.findAll();
        } else {
            daftarPeminjaman = peminjamanRepository.findAll().stream()
                    .filter(peminjaman -> peminjaman.getUnitTujuan() != null && 
                           peminjaman.getUnitTujuan().equalsIgnoreCase(userDetails.getUnit()))
                    .collect(Collectors.toList());
        }

        return daftarPeminjaman.stream()
                .map(peminjaman -> {
                    TinjauPeminjaman tinjau = tinjauRepository.findByPeminjaman_Id(peminjaman.getId()).orElse(null);
                    return mapToResponseDTO(peminjaman, tinjau);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TinjauPeminjamanResponseDTO create(UUID peminjamanId, TinjauPeminjamanRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        
        PeminjamanAset peminjaman = peminjamanRepository.findById(peminjamanId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Data Peminjaman tidak ditemukan"));

        if (peminjaman.getStatusPeminjaman() != PeminjamanAset.StatusPeminjaman.DIAJUKAN) {
            throw new IllegalStateException("Peninjauan hanya dapat dilakukan pada pengajuan dengan status DIAJUKAN");
        }

        if (request.getStatusPeminjaman() == PeminjamanAset.StatusPeminjaman.DIAJUKAN) {
            throw new IllegalArgumentException("Peninjau hanya boleh memilih status DISETUJUI atau DITOLAK");
        }

        User peninjau = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("User peninjau tidak ditemukan"));

        peminjaman.setStatusPeminjaman(request.getStatusPeminjaman());
        
        if (request.getStatusPeminjaman() == PeminjamanAset.StatusPeminjaman.DISETUJUI) {
            eksekusiPeminjamanAset(peminjaman);
        }

        TinjauPeminjaman peninjauanBaru = TinjauPeminjaman.builder()
                .peninjau(peninjau)
                .peminjaman(peminjaman)
                .rolePeninjau(peninjau.getRole())
                .statusPeminjaman(request.getStatusPeminjaman())
                .alasan(request.getAlasan())
                .build();

        peminjamanRepository.save(peminjaman);
        TinjauPeminjaman savedTinjau = tinjauRepository.save(peninjauanBaru);
        return mapToResponseDTO(peminjaman, savedTinjau);
    }

    @Override
    public TinjauPeminjamanResponseDTO getPeninjauanById(UUID idPeminjaman) {
        PeminjamanAset peminjaman = peminjamanRepository.findById(idPeminjaman)
                .orElseThrow(() -> new java.util.NoSuchElementException("Data peminjaman dengan ID tersebut tidak ditemukan"));

        TinjauPeminjaman tinjau = tinjauRepository.findByPeminjaman_Id(idPeminjaman).orElse(null);

        UserDetailsImpl userDetails = getCurrentUser();
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        
        if (!isAdmin && !peminjaman.getUnitTujuan().equalsIgnoreCase(userDetails.getUnit())) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki akses untuk melihat detail unit lain");
        }

        return mapToResponseDTO(peminjaman, tinjau);
    }

    private void eksekusiPeminjamanAset(PeminjamanAset peminjaman) {
        UUID asetId = peminjaman.getAset().getId();

        Optional<AsetBarang> barangOpt = asetBarangRepository.findById(asetId);
        if (barangOpt.isPresent()) {
            AsetBarang barang = barangOpt.get();

            int qtyBaru = barang.getQtyTersedia() - peminjaman.getQty();
            if (qtyBaru < 0) {
                throw new IllegalStateException("Stok tidak mencukupi untuk disetujui");
            }
            barang.setQtyTersedia(qtyBaru);
            barang.setQtyDipinjam(barang.getQtyDipinjam() + peminjaman.getQty());

            asetBarangRepository.save(barang);
            return;
        }

        AsetRuangan ruangan = asetRuanganRepository.findById(asetId)
                .orElseThrow(() -> new IllegalStateException("Aset tidak ditemukan: " + asetId));

        ruangan.setStatusAset(StatusAset.SEDANG_DIPINJAM);
        asetRuanganRepository.save(ruangan);
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal(); 
    }

    private TinjauPeminjamanResponseDTO mapToResponseDTO(PeminjamanAset peminjaman, TinjauPeminjaman tinjau) {
        User peminjam = peminjaman.getPeminjam();
        Aset aset = peminjaman.getAset();

        TinjauPeminjamanResponseDTO.TinjauPeminjamanResponseDTOBuilder builder = TinjauPeminjamanResponseDTO.builder()
                .idPeminjaman(peminjaman.getId())
                .waktuPengajuan(peminjaman.getWaktuPengajuan())
                .waktuPeminjaman(peminjaman.getWaktuPeminjaman())
                .waktuPengembalian(peminjaman.getWaktuPengembalian())
                .qty(peminjaman.getQty())
                .tujuanPeminjaman(peminjaman.getTujuanPeminjaman())
                .unitTujuan(peminjaman.getUnitTujuan())
                .statusPeminjaman(peminjaman.getStatusPeminjaman())
                .idPeminjam(peminjam.getId())
                .namaPeminjam(peminjam.getName())
                .rolePeminjam(peminjam.getRole())
                .unitAsal(peminjam.getUnit())
                .idAset(aset.getId())
                .kodeAset(aset.getKodeAset())
                .namaAset(aset.getNamaAset())
                .kategoriAset(aset.getKategoriAset());

        if (aset instanceof AsetBarang) {
            builder.merkAset(((AsetBarang) aset).getMerkAset());
        }

        if (tinjau != null) {
            builder.idPeninjauan(tinjau.getIdPeninjauan())
                   .alasan(tinjau.getAlasan())
                   .idPeninjau(tinjau.getPeninjau().getId())
                   .rolePeninjau(tinjau.getRolePeninjau())
                   .createdAt(tinjau.getCreatedAt())
                   .updatedAt(tinjau.getUpdatedAt());
        }

        return builder.build();
    }
}