package io.ibuprofen.inventra_dd_be.restcontroller;

import io.ibuprofen.inventra_dd_be.model.User;
import io.ibuprofen.inventra_dd_be.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.restdto.response.ProfileResponseDTO;
import io.ibuprofen.inventra_dd_be.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(
                    BaseResponseDTO.error(401, "Error: User not authenticated")
                );
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UUID userId = userDetails.getId();

            // Get user from database
            Optional<User> userOptional = userRepository.findById(userId);
            
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(404).body(
                    BaseResponseDTO.error(404, "Error: User not found")
                );
            }

            User user = userOptional.get();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(item -> item.getAuthority())
                    .orElse(null);

            // Validate NISN dan Kelas hanya untuk SISWA
            if (!role.equals("SISWA")) {
                if (user.getNisn() != null || user.getKelas() != null) {
                    return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400, "Error: NISN dan Kelas hanya untuk role SISWA")
                    );
                }
            } else {
                // SISWA harus memiliki NISN dan Kelas
                if (user.getNisn() == null || user.getNisn().isBlank() ||
                    user.getKelas() == null || user.getKelas().isBlank()) {
                    return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400, "Error: NISN dan Kelas tidak boleh kosong untuk SISWA")
                    );
                }
                // Validasi NISN hanya angka
                if (!user.getNisn().matches("^[0-9]+$")) {
                    return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400, "Error: NISN harus hanya berisi angka")
                    );
                }
            }

            // Build profile response
            ProfileResponseDTO profileResponse = ProfileResponseDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(role)
                    .unit(user.getUnit())
                    .phoneNumber(user.getPhoneNumber())
                    .password("**********")
                    .nisn(user.getNisn())
                    .kelas(user.getKelas())
                    .build();

            return ResponseEntity.ok(BaseResponseDTO.ok(profileResponse, "Profile retrieved successfully"));
        } catch (Exception e) {
            System.err.println("Error retrieving profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                BaseResponseDTO.error(500, "Error: An unexpected error occurred")
            );
        }
    }
}
