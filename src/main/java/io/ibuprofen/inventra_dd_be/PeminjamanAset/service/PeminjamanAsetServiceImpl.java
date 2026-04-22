package io.ibuprofen.inventra_dd_be.PeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetRuangan;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetRuanganRepository;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.request.*;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.response.PeminjamanAsetResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PeminjamanAsetServiceImpl implements PeminjamanAsetService {

    @Autowired
    private PeminjamanAsetRepository peminjamanAsetRepository;

    @Autowired
    private AsetBarangRepository asetBarangRepository;

    @Autowired
    private AsetRuanganRepository asetRuanganRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<PeminjamanAsetResponseDTO> getMyPeminjaman(UUID userId, String unitTujuan, PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable) {
        Page<PeminjamanAset> peminjamanPage = peminjamanAsetRepository.findByPeminjamIdAndUnitSendiriFiltered(userId, unitTujuan, statusPeminjaman, kategoriAset, search, pageable);
        return peminjamanPage.map(this::convertToResponseDTO);
    }

    @Override
    public Page<PeminjamanAsetResponseDTO> getMyPeminjamanLintasUnit(UUID userId, String unitTujuan, PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable) {
        Page<PeminjamanAset> peminjamanPage = peminjamanAsetRepository.findByPeminjamIdAndLintasUnitFiltered(userId, unitTujuan, statusPeminjaman, kategoriAset, search, pageable);
        return peminjamanPage.map(this::convertToResponseDTO);
    }

    @Override
    public Page<PeminjamanAsetResponseDTO> getAllPeminjaman(String unitTujuan, PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable) {
        Page<PeminjamanAset> peminjamanPage = peminjamanAsetRepository.findAllUnitSendiriFiltered(unitTujuan, statusPeminjaman, kategoriAset, search, pageable);
        return peminjamanPage.map(this::convertToResponseDTO);
    }

    @Override
    public Page<PeminjamanAsetResponseDTO> getAllPeminjamanLintasUnit(String unitTujuan, PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable) {
        Page<PeminjamanAset> peminjamanPage = peminjamanAsetRepository.findAllLintasUnitFiltered(unitTujuan, statusPeminjaman, kategoriAset, search, pageable);
        return peminjamanPage.map(this::convertToResponseDTO);
    }

    @Override
    public PeminjamanAsetResponseDTO createPeminjaman(CreatePeminjamanRequestDTO request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Aset aset = findAsetById(request.getIdAset());

        // Validation: Unit must match
        if (!user.getUnit().equals(aset.getUnit())) {
            throw new IllegalStateException("Asset does not belong to your unit");
        }

        if (request.getWaktuPeminjaman().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("Waktu peminjaman tidak boleh sebelum waktu sekarang");
        }

        validateLoanRequest(aset, request.getWaktuPeminjaman(), request.getWaktuPengembalian(), request.getQty());

        String unitTujuan = user.getUnit();

        PeminjamanAset peminjaman = PeminjamanAset.builder()
                .peminjam(user)
                .aset(aset)
                .waktuPengajuan(LocalDateTime.now())
                .waktuPeminjaman(request.getWaktuPeminjaman())
                .waktuPengembalian(request.getWaktuPengembalian())
                .qty(request.getQty())
                .tujuanPeminjaman(request.getTujuanPeminjaman())
                .statusPeminjaman(PeminjamanAset.StatusPeminjaman.DIAJUKAN)
                .unitTujuan(unitTujuan)
                .build();

        PeminjamanAset saved = peminjamanAsetRepository.save(peminjaman);
        return convertToResponseDTO(saved);
    }

    @Override
    public PeminjamanAsetResponseDTO createPeminjamanLintasUnit(CreatePeminjamanLintasUnitRequestDTO request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Aset aset = findAsetById(request.getIdAset());

        // Validation: Borrower unit must match requester's origin unit
        if (!user.getUnit().trim().equalsIgnoreCase(request.getUnitPeminjam().trim())) {
            throw new org.springframework.security.access.AccessDeniedException("Borrower unit mismatch with requested unit peminjam");
        }

        // Validation: Aset unit must match requested unitTujuan
        if (!aset.getUnit().equals(request.getUnitTujuan())) {
            throw new IllegalArgumentException("Asset unit mismatch with requested unit tujuan");
        }

        // Validation: Unit must be different between peminjam and asset owner
        if (request.getUnitPeminjam().equals(request.getUnitTujuan())) {
            throw new IllegalArgumentException("Lintas unit loan must be between different units");
        }

        if (request.getWaktuPeminjaman().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("Waktu peminjaman tidak boleh sebelum waktu sekarang");
        }

        validateLoanRequest(aset, request.getWaktuPeminjaman(), request.getWaktuPengembalian(), request.getQty());

        String unitTujuan = request.getUnitTujuan();

        PeminjamanAset peminjaman = PeminjamanAset.builder()
                .peminjam(user)
                .aset(aset)
                .waktuPengajuan(LocalDateTime.now())
                .waktuPeminjaman(request.getWaktuPeminjaman())
                .waktuPengembalian(request.getWaktuPengembalian())
                .qty(request.getQty())
                .tujuanPeminjaman(request.getTujuanPeminjaman())
                .statusPeminjaman(PeminjamanAset.StatusPeminjaman.DIAJUKAN)
                .unitTujuan(unitTujuan)
                .build();

        PeminjamanAset saved = peminjamanAsetRepository.save(peminjaman);
        return convertToResponseDTO(saved);
    }

    @Override
    public PeminjamanAsetResponseDTO getPeminjamanById(UUID id) {
        PeminjamanAset peminjaman = peminjamanAsetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Data peminjaman tidak ditemukan"));
        return convertToResponseDTO(peminjaman);
    }

    @Override
    public PeminjamanAsetResponseDTO updatePeminjaman(UUID idPeminjaman, UpdatePeminjamanRequestDTO request, UUID userId) {
        PeminjamanAset peminjaman = peminjamanAsetRepository.findById(idPeminjaman)
                .orElseThrow(() -> new NoSuchElementException("Data peminjaman tidak ditemukan"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!peminjaman.getPeminjam().getId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki izin untuk mengubah pengajuan ini");
        }

        if (peminjaman.getStatusPeminjaman() != PeminjamanAset.StatusPeminjaman.DIAJUKAN) {
            throw new IllegalStateException("Pengajuan hanya dapat diperbarui jika status masih DIAJUKAN");
        }

        Aset aset = findAsetById(request.getIdAset());

        if (!user.getUnit().trim().equalsIgnoreCase(aset.getUnit().trim()) && user.getRole() != Role.ADMIN) {
             throw new IllegalStateException("Aset bukan milik unit Anda");
        }

        if (request.getWaktuPeminjaman().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("Waktu peminjaman tidak boleh sebelum waktu sekarang");
        }

        validateLoanRequest(aset, request.getWaktuPeminjaman(), request.getWaktuPengembalian(), request.getQty());

        peminjaman.setAset(aset);
        peminjaman.setWaktuPeminjaman(request.getWaktuPeminjaman());
        peminjaman.setWaktuPengembalian(request.getWaktuPengembalian());
        peminjaman.setQty(request.getQty());
        peminjaman.setTujuanPeminjaman(request.getTujuanPeminjaman());
        peminjaman.setUnitTujuan(aset.getUnit());

        PeminjamanAset saved = peminjamanAsetRepository.save(peminjaman);
        return convertToResponseDTO(saved);
    }

    @Override
    public PeminjamanAsetResponseDTO updatePeminjamanLintasUnit(UUID idPeminjaman, UpdatePeminjamanLintasUnitRequestDTO request, UUID userId) {
        PeminjamanAset peminjaman = peminjamanAsetRepository.findById(idPeminjaman)
                .orElseThrow(() -> new NoSuchElementException("Data peminjaman tidak ditemukan"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!peminjaman.getPeminjam().getId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki izin untuk mengubah pengajuan ini");
        }

        if (peminjaman.getStatusPeminjaman() != PeminjamanAset.StatusPeminjaman.DIAJUKAN) {
            throw new IllegalStateException("Pengajuan hanya dapat diperbarui jika status masih DIAJUKAN");
        }

        Aset aset = findAsetById(request.getIdAset());

        // Validation: Borrower unit must match requester's origin unit
        if (!user.getUnit().trim().equalsIgnoreCase(request.getUnitPeminjam().trim()) && user.getRole() != Role.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Borrower unit mismatch with requested unit peminjam");
        }

        // Validation: Aset unit must match requested unitTujuan
        if (!aset.getUnit().trim().equalsIgnoreCase(request.getUnitTujuan().trim())) {
            throw new IllegalArgumentException("Asset unit mismatch with requested unit tujuan");
        }

        // Validation: Unit must be different between peminjam and asset owner
        if (request.getUnitPeminjam().trim().equalsIgnoreCase(request.getUnitTujuan().trim())) {
            throw new IllegalArgumentException("Lintas unit loan must be between different units");
        }

        if (request.getWaktuPeminjaman().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("Waktu peminjaman tidak boleh sebelum waktu sekarang");
        }

        validateLoanRequest(aset, request.getWaktuPeminjaman(), request.getWaktuPengembalian(), request.getQty());

        peminjaman.setAset(aset);
        peminjaman.setWaktuPeminjaman(request.getWaktuPeminjaman());
        peminjaman.setWaktuPengembalian(request.getWaktuPengembalian());
        peminjaman.setQty(request.getQty());
        peminjaman.setTujuanPeminjaman(request.getTujuanPeminjaman());
        peminjaman.setUnitTujuan(request.getUnitTujuan());

        PeminjamanAset saved = peminjamanAsetRepository.save(peminjaman);
        return convertToResponseDTO(saved);
    }

    @Override
    public void deletePeminjaman(UUID id, UUID userId) {
        PeminjamanAset peminjaman = peminjamanAsetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Data peminjaman tidak ditemukan"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isOwner = peminjaman.getPeminjam().getId().equals(userId);
        boolean isSuperadmin = user.getUnit().trim().equalsIgnoreCase("SUPERADMIN");
        boolean isSameUnitAdmin = user.getRole() == Role.ADMIN && user.getUnit().equalsIgnoreCase(peminjaman.getPeminjam().getUnit());

        if (!isOwner && !isSuperadmin && !isSameUnitAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki izin untuk menghapus pengajuan ini");
        }

        if (peminjaman.getStatusPeminjaman() != PeminjamanAset.StatusPeminjaman.DIAJUKAN) {
            throw new IllegalStateException("Pengajuan hanya dapat dihapus jika status masih DIAJUKAN");
        }

        peminjamanAsetRepository.delete(peminjaman);
    }

    @Override
    public void deletePeminjamanLintasUnit(UUID id, UUID userId) {
        PeminjamanAset peminjaman = peminjamanAsetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Data peminjaman tidak ditemukan"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isOwner = peminjaman.getPeminjam().getId().equals(userId);
        boolean isSuperadmin = user.getUnit().trim().equalsIgnoreCase("SUPERADMIN");

        if (!isOwner && !isSuperadmin) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki izin untuk menghapus pengajuan lintas unit ini");
        }

        if (peminjaman.getStatusPeminjaman() != PeminjamanAset.StatusPeminjaman.DIAJUKAN) {
            throw new IllegalStateException("Pengajuan hanya dapat dihapus jika status masih DIAJUKAN");
        }

        peminjamanAsetRepository.delete(peminjaman);
    }

    private Aset findAsetById(UUID id) {
        Optional<AsetBarang> barang = asetBarangRepository.findById(id);
        if (barang.isPresent()) return barang.get();
        
        Optional<AsetRuangan> ruangan = asetRuanganRepository.findById(id);
        if (ruangan.isPresent()) return ruangan.get();

        throw new NoSuchElementException("Asset not found");
    }

    private void validateLoanRequest(Aset aset, LocalDateTime start, LocalDateTime end, Integer qty) {
        if (aset.getKategoriAset() == io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.BARANG_HABIS_PAKAI) {
            throw new IllegalArgumentException("Barang habis pakai tidak dapat dipinjam");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Waktu pengembalian harus setelah waktu peminjaman");
        }

        if (aset instanceof AsetBarang) {
            AsetBarang ab = (AsetBarang) aset;
            // Physical capacity check (Total - Damaged/Repair/Destroyed)
            // We ignore current overlapping loans here to allow submission; conflicts are caught by Admin during approval.
            int physicalCapacity = ab.getQtyAset() - ab.getQtyRusak() - ab.getQtyPerbaikan() - ab.getQtyDimusnahkan();
            if (qty > physicalCapacity) {
                throw new IllegalArgumentException("Jumlah diminta (" + qty + ") melebihi kapasitas fisik tersedia (" + physicalCapacity + ")");
            }
        } else if (aset instanceof AsetRuangan) {
            // Rooms: Only check if NOT in a permanent unusable status (Repair)
            // Status SEDANG_DIPINJAM is ignored during submission to allow queueing; Admin handles overlaps.
            if (aset.getStatusAset() == StatusAset.RUSAK || aset.getStatusAset() == StatusAset.DIMUSNAHKAN) {
                throw new IllegalArgumentException("Ruangan sedang tidak dapat diajukan karena kondisi fisik");
            }
            if (qty > 1) {
                throw new IllegalArgumentException("Ruangan hanya dapat dipinjam dengan kuantitas 1");
            }
        }
    }

    private PeminjamanAsetResponseDTO convertToResponseDTO(PeminjamanAset peminjaman) {
        String merkAset = null;
        Aset aset = peminjaman.getAset();
        
        if (aset.getKategoriAset() == io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.BARANG_HABIS_PAKAI || 
            aset.getKategoriAset() == io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.BARANG_TIDAK_HABIS_PAKAI) {
            
            if (aset instanceof AsetBarang) {
                merkAset = ((AsetBarang) aset).getMerkAset();
            } else {
                // Handle Hibernate proxies
                Optional<AsetBarang> ab = asetBarangRepository.findById(aset.getId());
                if (ab.isPresent()) {
                    merkAset = ab.get().getMerkAset();
                }
            }
        }

        return PeminjamanAsetResponseDTO.builder()
                .idPeminjaman(peminjaman.getId())
                .waktuPengajuan(peminjaman.getWaktuPengajuan())
                .waktuPeminjaman(peminjaman.getWaktuPeminjaman())
                .waktuPengembalian(peminjaman.getWaktuPengembalian())
                .qty(peminjaman.getQty())
                .tujuanPeminjaman(peminjaman.getTujuanPeminjaman())
                .statusPeminjaman(peminjaman.getStatusPeminjaman())
                .idPeminjam(peminjaman.getPeminjam().getId())
                .namaPeminjam(peminjaman.getPeminjam().getName())
                .unitPeminjam(peminjaman.getPeminjam().getUnit())
                .rolePeminjam(peminjaman.getPeminjam().getRole())
                .idAset(peminjaman.getAset().getId())
                .kodeAset(peminjaman.getAset().getKodeAset())
                .namaAset(peminjaman.getAset().getNamaAset())
                .merkAset(merkAset)
                .kategoriAset(peminjaman.getAset().getKategoriAset())
                .unitTujuan(peminjaman.getUnitTujuan())
                .build();
    }
}
