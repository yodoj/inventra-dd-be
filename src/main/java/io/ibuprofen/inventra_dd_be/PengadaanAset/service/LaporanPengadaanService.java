package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaporanPengadaanService {

    @Autowired
    private PengadaanAsetRepository repo;

    @Autowired
    private UserRepository userRepository;

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    public List<LaporanPengadaanResponseDTO> getLaporanPengadaan() {

        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String role = user.getRole().name();
        String userUnit = user.getUnit();

        List<LaporanPengadaanResponseDTO> data;

        if (role.equalsIgnoreCase("YAYASAN") || role.equalsIgnoreCase("SUPERADMIN")) {
            // lihat semua data
            data = repo.findAllLaporan();
        } else if (role.equalsIgnoreCase("KEPSEK") || role.equalsIgnoreCase("SARPRAS")) {
            // hanya unit sendiri
            data = repo.findLaporanByUnit(userUnit);
        } else {
            throw new RuntimeException("Unauthorized role");
        }

        return data;
    }
}