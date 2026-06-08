package io.ibuprofen.inventra_dd_be.Profile.restcontroller;

import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.UpdatePasswordRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.UpdateProfileRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.PasswordHistoryResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.ProfileResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.services.PasswordHistoryService;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @GetMapping("")
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
                // Validasi NISN 10 digit angka
                if (!user.getNisn().matches("^[0-9]{10}$")) {
                    return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400, "Error: NISN harus tepat 10 digit angka")
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

    @PutMapping("")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequestDTO updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(
                    BaseResponseDTO.error(401, "Error: User not authenticated")
                );
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UUID userId = userDetails.getId();

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

            // Update hanya field yang diizinkan: name dan phoneNumber
            if (updateRequest.getName() != null && !updateRequest.getName().isBlank()) {
                user.setName(updateRequest.getName());
            }

            if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isBlank()) {
                String normalizedPhone = normalizePhoneNumber(updateRequest.getPhoneNumber());
                if (!normalizedPhone.matches("^08[0-9]{6,13}$")) {
                    return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400, "Error: Nomor telepon tidak valid. Gunakan format 08xx, +62xx, atau 62xx dengan panjang 8-15 digit")
                    );
                }
                user.setPhoneNumber(normalizedPhone);
            }

            // Untuk SISWA, update NISN dan Kelas
            if (role.equals("SISWA")) {
                if (updateRequest.getNisn() != null && !updateRequest.getNisn().isBlank()) {
                    // Validasi NISN tepat 10 digit angka
                    if (!updateRequest.getNisn().matches("^[0-9]{10}$")) {
                        return ResponseEntity.status(400).body(
                            BaseResponseDTO.error(400, "Error: NISN harus tepat 10 digit angka")
                        );
                    }
                    user.setNisn(updateRequest.getNisn());
                }

                if (updateRequest.getKelas() != null && !updateRequest.getKelas().isBlank()) {
                    user.setKelas(updateRequest.getKelas());
                }
            } else {
                // Non-SISWA tidak boleh memiliki NISN dan Kelas
                if (updateRequest.getNisn() != null || updateRequest.getKelas() != null) {
                    return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400, "Error: NISN dan Kelas hanya untuk role SISWA")
                    );
                }
            }

            // Validasi & save
            userRepository.save(user);

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

            return ResponseEntity.ok(BaseResponseDTO.ok(profileResponse, "Profile updated successfully"));
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                BaseResponseDTO.error(500, "Error: An unexpected error occurred")
            );
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequestDTO passwordRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(
                    BaseResponseDTO.error(401, "Error: User not authenticated")
                );
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UUID userId = userDetails.getId();

            Optional<User> userOptional = userRepository.findById(userId);
            
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(404).body(
                    BaseResponseDTO.error(404, "Error: User not found")
                );
            }

            User user = userOptional.get();

            // Validasi current password cocok
            if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body(
                    BaseResponseDTO.error(401, "Error: Password saat ini salah")
                );
            }

            // Validasi new_password = confirm_password
            if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
                return ResponseEntity.status(400).body(
                    BaseResponseDTO.error(400, "Error: Password baru dan konfirmasi password tidak cocok")
                );
            }

            // Validasi strong password (min 8 chars, uppercase, lowercase, special char)
            String newPassword = passwordRequest.getNewPassword();
            if (newPassword.length() < 8 ||
                    !newPassword.matches(".*[A-Z].*") ||
                    !newPassword.matches(".*[a-z].*") ||
                    !newPassword.matches(".*[0-9].*") ||
                    !newPassword.matches(".*[^A-Za-z0-9].*")) {
                return ResponseEntity.status(400).body(
                        BaseResponseDTO.error(400,
                                "Error: Password harus minimal 8 karakter, mengandung huruf besar, huruf kecil, angka, dan karakter unik"));
            }

            // Validasi new_password != current_password
            if (passwordRequest.getCurrentPassword().equals(passwordRequest.getNewPassword())) {
                return ResponseEntity.status(400).body(
                    BaseResponseDTO.error(400, "Error: Password baru tidak boleh sama dengan password saat ini")
                );
            }

            // Update password dengan hash
            user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            userRepository.save(user);

            // Catat history perubahan password setelah save sukses
            passwordHistoryService.recordPasswordChange(userId, user);

            return ResponseEntity.ok(BaseResponseDTO.ok(null, "Password updated successfully"));
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                BaseResponseDTO.error(500, "Error: An unexpected error occurred")
            );
        }
    }

    private String normalizePhoneNumber(String phone) {
        // Hapus spasi, tanda hubung, titik, dan tanda kurung
        String cleaned = phone.replaceAll("[\\s\\-\\.\\(\\)]", "");
        // +62xxx → 0xxx
        if (cleaned.startsWith("+62")) {
            cleaned = "0" + cleaned.substring(3);
        // 62xxx → 0xxx (hanya jika lebih dari 2 karakter agar tidak salah potong)
        } else if (cleaned.startsWith("62") && cleaned.length() > 5) {
            cleaned = "0" + cleaned.substring(2);
        }
        return cleaned;
    }

    @GetMapping("/password-history")
    public ResponseEntity<?> getPasswordHistory(@RequestParam(required = false) UUID userId) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(
                    BaseResponseDTO.error(401, "Session expired / Unauthorized")
                );
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UUID requesterId = userDetails.getId();
            String requesterRole = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(item -> item.getAuthority())
                    .orElse(null);

            // Tentukan target userId (default: userId dari token, atau parameter jika diberikan)
            UUID targetUserId = (userId != null) ? userId : requesterId;

            // Validasi authorization:
            // - Diizinkan jika requester adalah pemilik akun (SELF)
            // - Atau jika requester adalah SUPERADMIN (ADMIN role)
            // - Atau jika requester adalah SARPRAS dan target user berada di unit yang sama
            boolean isSelf = requesterId.equals(targetUserId);
            boolean isSuperAdmin = "ADMIN".equals(requesterRole);
            boolean isSarpras = "SARPRAS".equals(requesterRole);

            if (!isSelf && !isSuperAdmin) {
                if (!isSarpras) {
                    return ResponseEntity.status(403).body(
                        BaseResponseDTO.error(403, "Forbidden: You can only access your own password history")
                    );
                }

                // SARPRAS: hanya bisa lihat history user dalam unit yang sama
                Optional<User> requesterOpt = userRepository.findById(requesterId);
                Optional<User> targetOpt = userRepository.findByIdAndIsDeletedFalse(targetUserId);

                if (requesterOpt.isEmpty()) {
                    return ResponseEntity.status(403).body(
                        BaseResponseDTO.error(403, "Forbidden: Requester not found")
                    );
                }
                if (targetOpt.isEmpty()) {
                    return ResponseEntity.status(404).body(
                        BaseResponseDTO.error(404, "User not found")
                    );
                }

                String sarprasUnit = requesterOpt.get().getUnit();
                String targetUnit = targetOpt.get().getUnit();

                if (sarprasUnit == null || !sarprasUnit.equals(targetUnit)) {
                    return ResponseEntity.status(403).body(
                        BaseResponseDTO.error(403, "Forbidden: You can only access password history of users in your unit")
                    );
                }
            }

            // Ambil password history
            List<PasswordHistoryResponseDTO> history = passwordHistoryService.getPasswordHistory(targetUserId);

            return ResponseEntity.ok(BaseResponseDTO.ok(history, "Password history retrieved successfully"));
        } catch (Exception e) {
            System.err.println("Error retrieving password history: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                BaseResponseDTO.error(500, "Error: An unexpected error occurred")
            );
        }
    }
}
