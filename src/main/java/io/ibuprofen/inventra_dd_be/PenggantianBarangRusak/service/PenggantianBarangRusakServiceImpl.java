package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.service;

import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.PenggantianBarangRusakRequestDTO;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.PenggantianBarangRusakResponseDTO;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.UpdatePenggantianBarangRusakRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository.TinjauPengadaanRepository;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.repository.TinjauPenggantianBarangRepository;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.repository.PenggantianBarangRusakRepo;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PenggantianBarangRusakServiceImpl implements PenggantianBarangRusakService {

    private final PenggantianBarangRusakRepo repoPenggantian;
    private final UserRepository userRepository;
    @Autowired
    private TinjauPenggantianBarangRepository tinjauRepo;

    @Override
    public List<PenggantianBarangRusakResponseDTO> getAll(String search, String status) {
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();
        String unitUser = currentUser.getUnit(); 

        List<PenggantianBarangRusak> data = repoPenggantian.findByUserId(currentUser.getId());
            return data.stream()

            .filter(p -> {
                        if (search == null || search.isBlank()) return true;

                        String s = search.toLowerCase();

                        return p.getIdPenggantian().toLowerCase().contains(s)
                                || p.getNamaBarang().toLowerCase().contains(s)
                                || p.getMerk().toLowerCase().contains(s);
                    })

                    .filter(p -> {
                        if (status == null || status.isBlank()) return true;

                        return p.getStatus().equalsIgnoreCase(status);
                    }).map(p -> PenggantianBarangRusakResponseDTO.builder()
                        .idPenggantian(p.getIdPenggantian())
                        .namaBarang(p.getNamaBarang())
                        .waktuPenggantian(p.getWaktuPenggantian())
                        .quantity(p.getQuantity())
                        .namaPengaju(p.getNamaPengaju())
                        .unitPengaju(p.getUnitPengaju())
                        .rolePengaju(p.getRolePengaju().name())
                        .merk(p.getMerk())
                        .contohBarang(p.getContohBarang())
                        .status(p.getStatus())
                        .alasan(p.getReviewPengajuan())
                        .keterangan(p.getKeterangan())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public PenggantianBarangRusakResponseDTO getById(String idPenggantian) {

        User currentUser = getCurrentUserEntity();

        PenggantianBarangRusak penggantian = repoPenggantian
                .findByIdPenggantian(idPenggantian)
                .orElseThrow(() -> new IllegalStateException("Pengajuan tidak ditemukan"));

        if (!penggantian.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Anda tidak memiliki akses ke pengajuan ini");
        }

        return mapToResponse(penggantian);
    }

    @Override
    public PenggantianBarangRusakResponseDTO createPengajuan(PenggantianBarangRusakRequestDTO request, MultipartFile file) {

        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();


        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Kuantitas harus lebih dari 0");
        }

        LocalDate waktuPenggantian = request.getWaktuPenggantian(); 
        LocalDate today = LocalDate.now();
        if (!waktuPenggantian.isAfter(today)) {
            throw new IllegalArgumentException("Waktu penggantian tidak boleh hari ini atau sebelumnya.");
        }
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("User tidak ditemukan"));

        String fileName = null;
        if (file != null && !file.isEmpty()) {
            fileName = saveFileToLocal(file); 
        }
        PenggantianBarangRusak penggantian = new PenggantianBarangRusak();

        penggantian.setNamaBarang(request.getNamaBarang());
        penggantian.setMerk(request.getMerk());
        penggantian.setQuantity(request.getQuantity());
        penggantian.setWaktuPenggantian(request.getWaktuPenggantian());
        penggantian.setContohBarang(fileName);
        if (role == Role.ADMIN) {
            if (request.getUnitPengaju() == null || request.getUnitPengaju().isEmpty()) {
                throw new IllegalArgumentException("Admin wajib menentukan unit untuk pengadaan ini.");
            }
            penggantian.setUnitPengaju(request.getUnitPengaju());
        } else {
            if (request.getUnitPengaju() != null && !request.getUnitPengaju().isEmpty() && !request.getUnitPengaju().equals(user.getUnit())) {
                throw new IllegalArgumentException("Anda hanya bisa membuat pengajuan untuk unit Anda sendiri: " + user.getUnit());
            }
            penggantian.setUnitPengaju(user.getUnit());
        }
        penggantian.setKeterangan(request.getKeterangan());

        penggantian.setUser(user);
        penggantian.setNamaPengaju(user.getName());
        penggantian.setRolePengaju(user.getRole());

        penggantian.setStatus(Status.DIAJUKAN.name());
        penggantian.setReviewPengajuan(null);

        PenggantianBarangRusak saved = repoPenggantian.save(penggantian);

        return mapToResponse(saved);
    }

    @Override
    public PenggantianBarangRusakResponseDTO updatePengajuan(String idPenggantian, UpdatePenggantianBarangRusakRequestDTO request, MultipartFile file) {
        User currentUser = getCurrentUserEntity(); 

        PenggantianBarangRusak penggantian = repoPenggantian.findByIdPenggantian(idPenggantian)
                .orElseThrow(() -> new IllegalStateException("Pengajuan tidak ditemukan"));

        if (!penggantian.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Anda hanya dapat mengupdate pengajuan milik Anda sendiri");
        }

        if (!penggantian.getStatus().equals(Status.DIAJUKAN.name())) {
            throw new IllegalStateException("Pengajuan hanya bisa diupdate jika status DIAJUKAN");
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Kuantitas harus lebih dari 0");
        }

        if (!request.getWaktuPenggantian().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Waktu penggantian tidak boleh hari ini atau sebelumnya.");
        }

        penggantian.setNamaBarang(request.getNamaBarang());
        penggantian.setMerk(request.getMerk());
        penggantian.setQuantity(request.getQuantity());
        penggantian.setWaktuPenggantian(request.getWaktuPenggantian());
        penggantian.setKeterangan(request.getKeterangan());

        if (file != null && !file.isEmpty()) {
            penggantian.setContohBarang(saveFileToLocal(file));
        }

        penggantian.setUser(currentUser);
        penggantian.setNamaPengaju(currentUser.getName());
        penggantian.setRolePengaju(currentUser.getRole());
        penggantian.setUnitPengaju(currentUser.getUnit());

        penggantian.setStatus(Status.DIAJUKAN.name());

        return mapToResponse(repoPenggantian.save(penggantian));
    }

    @Override
    public void deletePengajuan(String idPenggantian) {
        User currentUser = getCurrentUserEntity();

        PenggantianBarangRusak penggantian = repoPenggantian.findByIdPenggantian(idPenggantian)
                .orElseThrow(() -> new IllegalStateException("Pengajuan tidak ditemukan"));

        if (!penggantian.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Anda hanya dapat menghapus pengajuan milik Anda sendiri");
        }

        if (!penggantian.getStatus().equals(Status.DIAJUKAN.name())) {
            throw new IllegalStateException("Pengajuan hanya bisa dihapus jika status DIAJUKAN");
        }

        repoPenggantian.delete(penggantian);
    }

    private UserDetailsImpl getCurrentUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    }

    private User getCurrentUserEntity() {
        UserDetailsImpl principal = getCurrentUserDetails();
        return userRepository.findById(principal.getId())
            .orElseThrow(() -> new IllegalStateException("User tidak ditemukan"));
    }

    private String saveFileToLocal(MultipartFile file) {
        try {
            String cleanName = file.getOriginalFilename().replaceAll("\\s+", "_");
            String filename = UUID.randomUUID() + ".png";
            Path root = Paths.get("uploads/contoh-gambar");
            if (!Files.exists(root)) Files.createDirectories(root);
            Files.copy(file.getInputStream(), root.resolve(filename));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }

    private PenggantianBarangRusakResponseDTO mapToResponse(PenggantianBarangRusak p) {
        String alasan = null;
        String reviewerRole = null;
        String namaReviewer = null;
        LocalDateTime reviewCreatedAt = null;
        LocalDateTime reviewUpdatedAt = null;
        
        if (!"DIAJUKAN".equals(p.getStatus())) {
            var tinjauan = tinjauRepo
                .findFirstByPenggantian_IdPenggantianOrderByUpdatedAtDesc(p.getIdPenggantian())
                .orElse(null);
            
            if (tinjauan != null) {
                alasan = tinjauan.getAlasan(); 
                reviewerRole = tinjauan.getReviewerRole() != null ? tinjauan.getReviewerRole().name() : null;
                namaReviewer = tinjauan.getUser() != null 
                    ? tinjauan.getUser().getName() 
                    : null;
                reviewCreatedAt = tinjauan.getCreatedAt();
                reviewUpdatedAt = tinjauan.getUpdatedAt();

    //             private LocalDateTime updatedAt;
    // private LocalDateTime createdAt;

    // private UUID userId;
    // private String reviewerRole;
    // private String namaReviewer; namaReviewer
            }
        }
        return PenggantianBarangRusakResponseDTO.builder()
                .idPenggantian(p.getIdPenggantian())
                .namaBarang(p.getNamaBarang())
                .waktuPenggantian(p.getWaktuPenggantian())
                .quantity(p.getQuantity())
                .merk(p.getMerk())
                .contohBarang(p.getContohBarang())
                .status(p.getStatus())
                .keterangan(p.getKeterangan())
                .namaPengaju(p.getNamaPengaju())
                .unitPengaju(p.getUnitPengaju())
                .rolePengaju(p.getRolePengaju().name())
                .alasan(p.getReviewPengajuan())
                .reviewCreatedAt(reviewCreatedAt)
                .reviewUpdatedAt(reviewUpdatedAt)
                .reviewerRole(reviewerRole)
                .namaReviewer(namaReviewer)
                .build();
    }

}