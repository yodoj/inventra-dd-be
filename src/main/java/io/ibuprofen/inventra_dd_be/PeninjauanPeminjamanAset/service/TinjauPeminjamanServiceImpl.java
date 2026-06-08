package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetRuangan;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetRuanganRepository;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.model.TinjauPeminjaman;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.repository.TinjauPeminjamanRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
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
    public List<TinjauPeminjamanResponseDTO> getAll(
            StatusPeminjaman statusPeminjaman, 
            String unitTujuan, 
            LocalDate tanggalPeminjaman, 
            LocalDate tanggalPengembalian,
            String kategoriAset,
            String search) {
        
        UserDetailsImpl userDetails = getCurrentUser();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (kategoriAset != null && !kategoriAset.isEmpty()) {
            List<String> validKategori = List.of("BARANG", "RUANGAN");
            if (!validKategori.contains(kategoriAset.toUpperCase())) {
                throw new IllegalArgumentException("Kategori '" + kategoriAset + "' tidak valid. Gunakan: BARANG atau RUANG");
            }
        }

        if (unitTujuan != null && !unitTujuan.isEmpty()) {
            List<String> legalUnits = List.of("KB-TK", "SD", "SMP", "SMA", "SUPERADMIN");
            if (!legalUnits.contains(unitTujuan.toUpperCase())) {
                throw new IllegalArgumentException("Unit '" + unitTujuan + "' tidak valid. Pilih unit yang benar");
            }
        }

        if (!isAdmin && unitTujuan != null && !unitTujuan.isEmpty()) {
            if (!unitTujuan.equalsIgnoreCase(userDetails.getUnit())) {
                throw new org.springframework.security.access.AccessDeniedException(
                    "Anda tidak memiliki akses untuk meninjau unit ini. Tinjau sesuai unit Anda" 
                );
            }
        } 

        String unitTerpilih = isAdmin ? unitTujuan : userDetails.getUnit();
        Specification<PeminjamanAset> kriteria = PeminjamanAsetSpecification.withFilters(
            statusPeminjaman, 
            unitTerpilih, 
            tanggalPeminjaman, 
            tanggalPengembalian,
            kategoriAset,
            search
        );

        return peminjamanRepository.findAll(kriteria).stream()
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

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isAdmin && !peminjaman.getUnitTujuan().equalsIgnoreCase(userDetails.getUnit())) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki akses untuk meninjau peminjaman unit lain");
        }

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
            validateAvailabilityForPeriod(peminjaman);
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

    @Override
    @Transactional
    public TinjauPeminjamanResponseDTO update(UUID idPeminjaman, TinjauPeminjamanRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        
        PeminjamanAset peminjaman = peminjamanRepository.findById(idPeminjaman)
                .orElseThrow(() -> new NoSuchElementException("Data Peminjaman tidak ditemukan"));

        if (request.getStatusPeminjaman() == StatusPeminjaman.DIAJUKAN) {
            throw new IllegalArgumentException("Peninjau hanya boleh memilih status DISETUJUI atau DITOLAK");
        }
        
        TinjauPeminjaman tinjauLama = tinjauRepository.findByPeminjaman_Id(idPeminjaman)
                .orElseThrow(() -> new IllegalStateException("Data peninjauan belum ada"));

        if (java.time.LocalDateTime.now().isAfter(peminjaman.getWaktuPeminjaman())) {
            throw new IllegalStateException("Peninjauan tidak dapat diubah karena waktu peminjaman sudah dimulai atau terlewati.");
        }

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isAdmin && !peminjaman.getUnitTujuan().equalsIgnoreCase(userDetails.getUnit())) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki akses untuk mengubah peninjauan unit lain");
        }

        StatusPeminjaman statusLama = peminjaman.getStatusPeminjaman();
        StatusPeminjaman statusBaru = request.getStatusPeminjaman();

        if (statusLama != statusBaru) {
            if (statusLama == StatusPeminjaman.DITOLAK && statusBaru == StatusPeminjaman.DISETUJUI) {
                validateAvailabilityForPeriod(peminjaman);
            }
        }

        User peninjauBaru = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new IllegalStateException("User peninjau tidak ditemukan"));
        peminjaman.setStatusPeminjaman(statusBaru); 
        tinjauLama.setPeninjau(peninjauBaru);
        tinjauLama.setRolePeninjau(peninjauBaru.getRole());
        tinjauLama.setStatusPeminjaman(statusBaru);
        tinjauLama.setAlasan(request.getAlasan()); 

        peminjamanRepository.save(peminjaman); 
        TinjauPeminjaman savedTinjau = tinjauRepository.save(tinjauLama); 

        return mapToResponseDTO(peminjaman, savedTinjau);
    }

    private void validateAvailabilityForPeriod(PeminjamanAset peminjaman) {
        UUID asetId = peminjaman.getAset().getId();
        int requestedQty = peminjaman.getQty();
        
        Integer overlapQty = peminjamanRepository.countOverlappingLoansExcludeId(
            asetId, 
            peminjaman.getId(),
            peminjaman.getWaktuPeminjaman(), 
            peminjaman.getWaktuPengembalian()
        );
        if (overlapQty == null) overlapQty = 0;

        Optional<AsetBarang> barangOpt = asetBarangRepository.findById(asetId);
        if (barangOpt.isPresent()) {
            AsetBarang barang = barangOpt.get();
            int totalPhysicalCapacity = barang.getQtyAset() - barang.getQtyRusak() - barang.getQtyPerbaikan() - barang.getQtyDimusnahkan();
            int currentAvailable = totalPhysicalCapacity - overlapQty;

            if (currentAvailable < requestedQty) {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm");
                String wktMulai = peminjaman.getWaktuPeminjaman().format(formatter);
                String wktSelesai = peminjaman.getWaktuPengembalian().format(formatter);
                throw new IllegalStateException("Stok tidak mencukupi untuk rentang waktu " 
                    + wktMulai + " s/d " + wktSelesai);
            }
        } else {
            if (overlapQty > 0) {
                throw new IllegalStateException("Ruangan sudah diajukan peminjamannya oleh orang lain untuk rentang waktu tersebut.");
            }
        }
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
        } else {
            Optional<AsetBarang> asetBarang = asetBarangRepository.findById(aset.getId());
            if (asetBarang.isPresent()) {
                builder.merkAset(asetBarang.get().getMerkAset());
            }
        }

        if (tinjau != null) {
            builder.idPeninjauan(tinjau.getIdPeninjauan())
                   .alasan(tinjau.getAlasan())
                   .idPeninjau(tinjau.getPeninjau().getId())
                   .rolePeninjau(tinjau.getRolePeninjau())
                   .namaPeninjau(tinjau.getPeninjau().getName())
                   .createdAt(tinjau.getCreatedAt())
                   .updatedAt(tinjau.getUpdatedAt());
        }

        return builder.build();
    }
}