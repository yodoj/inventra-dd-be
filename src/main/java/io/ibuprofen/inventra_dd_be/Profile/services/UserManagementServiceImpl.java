package io.ibuprofen.inventra_dd_be.Profile.services;

import io.ibuprofen.inventra_dd_be.Profile.exception.DuplicateEmailException;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.AdminUpdatePasswordRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.CreateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.UpdateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.SarprasLintasUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @Override
    public Page<UserPerUnitResponseDTO> getUsersInSameUnit(String search, Role role, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Optional<User> currentUserOpt = userRepository.findById(userId);
        if (currentUserOpt.isEmpty()) {
            throw new RuntimeException("Error: Current user not found");
        }

        User currentUser = currentUserOpt.get();
        String currentUnit = currentUser.getUnit();

        // RBAC check: Only SARPRAS
        if (currentUser.getRole() != Role.SARPRAS) {
            throw new RuntimeException("Error: Unauthorized access, SARPRAS role required");
        }

        String searchParam = (search == null || search.trim().isEmpty()) ? "" : "%" + search.toLowerCase() + "%";
        Page<User> usersPage = userRepository.findUsersByUnitWithFilters(currentUnit, role, searchParam, pageable);

        return usersPage.map(user -> UserPerUnitResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .unit(user.getUnit())
                .build());
    }

    @Override
    public Page<UserPerUnitResponseDTO> getAllUsers(String unit, String search, Role role, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Optional<User> currentUserOpt = userRepository.findById(userId);
        if (currentUserOpt.isEmpty()) {
            throw new RuntimeException("Error: Current user not found");
        }

        User currentUser = currentUserOpt.get();

        // RBAC check: Only ADMIN (Superadmin)
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Error: Unauthorized access, ADMIN role required");
        }

        String searchParam = (search == null || search.trim().isEmpty()) ? "" : "%" + search.toLowerCase() + "%";
        String unitParam = (unit == null || unit.trim().isEmpty() || unit.equalsIgnoreCase("Semua Unit")) ? "" : unit;
        
        Page<User> usersPage = userRepository.findAllUsersWithFilters(unitParam, role, searchParam, pageable);

        return usersPage.map(user -> UserPerUnitResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .unit(user.getUnit())
                .build());
    }

    @Override
    public void createUser(CreateUserRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID creatorId = userDetails.getId();

        Optional<User> creatorOpt = userRepository.findById(creatorId);
        if (creatorOpt.isEmpty()) {
            throw new RuntimeException("Error: Creator user not found");
        }

        User creator = creatorOpt.get();
        Role creatorRole = creator.getRole();

        // RBAC: Only SARPRAS and ADMIN can create accounts
        if (creatorRole != Role.SARPRAS && creatorRole != Role.ADMIN) {
            throw new RuntimeException("Error: Unauthorized access, SARPRAS or ADMIN role required");
        }

        // Email uniqueness check (case-insensitive, only among active accounts)
        if (userRepository.findByEmailIgnoreCaseAndIsDeletedFalse(request.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email sudah terdaftar di sistem");
        }

        // Password policy validation
        validatePassword(request.getPassword());

        // Role-based constraints
        String assignedUnit;
        if (request.getRole() == Role.ADMIN) {
            assignedUnit = "SUPERADMIN";
        } else if (request.getRole() == Role.YAYASAN) {
            assignedUnit = "YAYASAN";
        } else {
            if (creatorRole == Role.SARPRAS) {
                assignedUnit = creator.getUnit();
            } else {
                // ADMIN creator must provide a unit for roles other than ADMIN/YAYASAN
                if (request.getUnit() == null || request.getUnit().isBlank()) {
                    throw new RuntimeException("Error: Unit wajib diisi");
                }
                assignedUnit = request.getUnit();
            }
        }

        // Siswa validation
        if (request.getRole() == Role.SISWA) {
            if (request.getNisn() == null || request.getNisn().isBlank()) {
                throw new RuntimeException("Error: NISN wajib diisi untuk role Siswa");
            }
            if (!request.getNisn().matches("^[0-9]+$")) {
                throw new RuntimeException("Error: NISN harus hanya berisi angka");
            }
            if (request.getKelas() == null || request.getKelas().isBlank()) {
                throw new RuntimeException("Error: Kelas wajib diisi untuk role Siswa");
            }
        } else {
            // Non-siswa: nisn and kelas should be null/ignored
            if ((request.getNisn() != null && !request.getNisn().isBlank()) || 
                (request.getKelas() != null && !request.getKelas().isBlank())) {
                throw new RuntimeException("Error: NISN dan Kelas hanya bisa diisi untuk role Siswa");
            }
        }

        String phone;
        if (request.getNomorTelepon() == null || request.getNomorTelepon().isBlank()) {
            phone = "-";
        } else {
            phone = request.getNomorTelepon().trim();
            if (!phone.matches("^[0-9]+$")) {
                throw new RuntimeException("Error: Nomor telepon harus hanya berisi angka");
            }
            if (!phone.startsWith("08")) {
                throw new RuntimeException("Error: Format nomor telepon harus diawali dengan 08 (contoh: 08123456789)");
            }
        }

        User newUser = User.builder()
                .name(request.getNamaLengkap())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .unit(assignedUnit)
                .phoneNumber(phone)
                .nisn(request.getRole() == Role.SISWA ? request.getNisn() : null)
                .kelas(request.getRole() == Role.SISWA ? request.getKelas() : null)
                .build();

        userRepository.save(newUser);
    }

    @Override
    public UserPerUnitResponseDTO getUserDetailInSameUnit(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID sarprasId = userDetails.getId();

        Optional<User> sarprasOpt = userRepository.findById(sarprasId);
        if (sarprasOpt.isEmpty()) {
            throw new RuntimeException("Error: Current user not found");
        }

        User sarprasUser = sarprasOpt.get();
        String sarprasUnit = sarprasUser.getUnit();

        // RBAC check: Only SARPRAS
        if (sarprasUser.getRole() != Role.SARPRAS) {
            throw new IllegalStateException("Unauthorized access, SARPRAS role required");
        }

        // Fetch the requested user (active only)
        Optional<User> targetUserOpt = userRepository.findByIdAndIsDeletedFalse(userId);
        if (targetUserOpt.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }

        User targetUser = targetUserOpt.get();

        // Check if target user is in the same unit as the SARPRAS user
        if (!targetUser.getUnit().equals(sarprasUnit)) {
            throw new IllegalStateException("Unauthorized access");
        }

        // Build the response DTO with role-specific fields
        UserPerUnitResponseDTO.UserPerUnitResponseDTOBuilder builder = UserPerUnitResponseDTO.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .name(targetUser.getName())
                .phoneNumber(targetUser.getPhoneNumber())
                .role(targetUser.getRole() != null ? targetUser.getRole().name() : null)
                .unit(targetUser.getUnit())
                .password("**********");

        // Add role-specific fields
        if (targetUser.getRole() == Role.SISWA) {
            builder.nisn(targetUser.getNisn()).kelas(targetUser.getKelas());
        }

        return builder.build();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 64) {
            throw new RuntimeException("Error: Password minimal 8 karakter dan maksimal 64 karakter");
        }

        if (password.contains(" ") || password.contains("\t") || password.contains("\n") || password.contains("\r")) {
            throw new RuntimeException("Error: Password tidak boleh mengandung whitespace");
        }

        int categories = 0;
        if (Pattern.compile("[A-Z]").matcher(password).find()) categories++;
        if (Pattern.compile("[a-z]").matcher(password).find()) categories++;
        if (Pattern.compile("[0-9]").matcher(password).find()) categories++;
        if (Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]").matcher(password).find()) categories++;

        if (categories < 4) {
            throw new RuntimeException("Error: Password harus memenuhi 4 kategori: huruf besar, huruf kecil, angka, dan simbol");
        }
    }

    @Override
    public void deleteUser(UUID targetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID sarprasId = userDetails.getId();

        Optional<User> sarprasOpt = userRepository.findById(sarprasId);
        if (sarprasOpt.isEmpty()) {
            throw new RuntimeException("Error: Current user not found");
        }

        User sarprasUser = sarprasOpt.get();

        // RBAC: hanya SARPRAS yang boleh memanggil method ini
        if (sarprasUser.getRole() != Role.SARPRAS) {
            throw new RuntimeException("Error: Unauthorized access, SARPRAS role required");
        }

        String sarprasUnit = sarprasUser.getUnit();

        // Cari target user (active only) — kalau tidak ada / sudah deleted → 404
        User targetUser = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Cek unit sama — unit berbeda → 403
        if (!targetUser.getUnit().equals(sarprasUnit)) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk menghapus akun di unit lain");
        }

        // Soft delete
        targetUser.setIsDeleted(true);
        targetUser.setDeletedAt(LocalDateTime.now());
        targetUser.setDeletedBy(sarprasId);
        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public UserPerUnitResponseDTO updateUser(UUID targetId, UpdateUserRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID sarprasId = userDetails.getId();

        Optional<User> sarprasOpt = userRepository.findById(sarprasId);
        if (sarprasOpt.isEmpty()) {
            throw new RuntimeException("Error: Current user not found");
        }

        User sarprasUser = sarprasOpt.get();

        // RBAC: only SARPRAS
        if (sarprasUser.getRole() != Role.SARPRAS) {
            throw new RuntimeException("Error: Unauthorized access, SARPRAS role required");
        }

        String sarprasUnit = sarprasUser.getUnit();

        // Find target user (active only)
        User targetUser = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new NoSuchElementException("User tidak ditemukan"));

        // Check same unit
        if (!targetUser.getUnit().equals(sarprasUnit)) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk mengubah akun di unit lain");
        }

        // Unit field must not be provided
        if (request.getUnit() != null) {
            throw new RuntimeException("Error: Field unit tidak dapat diubah");
        }

        // Role validation: only school-unit roles allowed (not ADMIN or YAYASAN)
        Role newRole = request.getRole();
        if (newRole == Role.ADMIN || newRole == Role.YAYASAN) {
            throw new RuntimeException("Error: Role " + newRole.name() + " tidak dapat ditugaskan oleh Sarpras");
        }

        // Self-role guard: Sarpras cannot change their own role
        if (targetId.equals(sarprasId) && newRole != sarprasUser.getRole()) {
            throw new AccessDeniedException("Sarpras tidak dapat mengubah role miliknya sendiri");
        }

        // Email uniqueness check (only if email has changed)
        String newEmail = request.getEmail();
        if (!newEmail.equalsIgnoreCase(targetUser.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmailIgnoreCaseAndIsDeletedFalse(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(targetId)) {
                throw new DuplicateEmailException("Email sudah digunakan oleh pengguna lain");
            }
        }

        // SISWA field validation
        if (newRole == Role.SISWA) {
            if (request.getNisn() == null || request.getNisn().isBlank()) {
                throw new RuntimeException("Error: NISN wajib diisi untuk role Siswa");
            }
            if (!request.getNisn().matches("^[0-9]{10}$")) {
                throw new RuntimeException("Error: NISN harus tepat 10 digit angka");
            }
            if (request.getKelas() == null || request.getKelas().isBlank()) {
                throw new RuntimeException("Error: Kelas wajib diisi untuk role Siswa");
            }
        }

        // Apply updates
        targetUser.setName(request.getNamaLengkap());
        targetUser.setEmail(newEmail);
        targetUser.setPhoneNumber(
                (request.getNomorTelepon() == null || request.getNomorTelepon().isBlank()) ? "-" : request.getNomorTelepon()
        );
        targetUser.setRole(newRole);

        if (newRole == Role.SISWA) {
            targetUser.setNisn(request.getNisn());
            targetUser.setKelas(request.getKelas());
        } else {
            targetUser.setNisn(null);
            targetUser.setKelas(null);
        }

        userRepository.save(targetUser);

        UserPerUnitResponseDTO.UserPerUnitResponseDTOBuilder builder = UserPerUnitResponseDTO.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .name(targetUser.getName())
                .phoneNumber(targetUser.getPhoneNumber())
                .role(targetUser.getRole() != null ? targetUser.getRole().name() : null)
                .unit(targetUser.getUnit());

        if (newRole == Role.SISWA) {
            builder.nisn(targetUser.getNisn()).kelas(targetUser.getKelas());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void updateUserPassword(UUID targetId, AdminUpdatePasswordRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID sarprasId = userDetails.getId();

        Optional<User> sarprasOpt = userRepository.findById(sarprasId);
        if (sarprasOpt.isEmpty()) {
            throw new RuntimeException("Error: Current user not found");
        }

        User sarprasUser = sarprasOpt.get();

        // RBAC: only SARPRAS
        if (sarprasUser.getRole() != Role.SARPRAS) {
            throw new RuntimeException("Error: Unauthorized access, SARPRAS role required");
        }

        // Find target user (active only)
        User targetUser = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new NoSuchElementException("User tidak ditemukan"));

        // Check same unit
        if (!targetUser.getUnit().equals(sarprasUser.getUnit())) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk mengubah password akun di unit lain");
        }

        // Validate new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Error: Password baru dan konfirmasi password tidak sama");
        }

        // Validate password policy
        validatePassword(request.getNewPassword());

        // Update password
        targetUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(targetUser);

        // Record password history
        passwordHistoryService.recordPasswordChange(targetId, sarprasUser);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN (Superadmin) methods — no unit restriction
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public UserPerUnitResponseDTO getUserDetailByAdmin(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID adminId = userDetails.getId();

        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Error: Current user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Error: Unauthorized access, ADMIN role required");
        }

        User targetUser = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        UserPerUnitResponseDTO.UserPerUnitResponseDTOBuilder builder = UserPerUnitResponseDTO.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .name(targetUser.getName())
                .phoneNumber(targetUser.getPhoneNumber())
                .role(targetUser.getRole() != null ? targetUser.getRole().name() : null)
                .unit(targetUser.getUnit())
                .password("**********");

        if (targetUser.getRole() == Role.SISWA) {
            builder.nisn(targetUser.getNisn()).kelas(targetUser.getKelas());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public UserPerUnitResponseDTO updateUserByAdmin(UUID targetId, UpdateUserRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID adminId = userDetails.getId();

        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Error: Current user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Error: Unauthorized access, ADMIN role required");
        }

        User targetUser = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new NoSuchElementException("User tidak ditemukan"));

        // Self-role-change guard (consistent with SARPRAS: role checks before email check)
        Role newRole = request.getRole();
        if (targetId.equals(adminId) && newRole != adminUser.getRole()) {
            throw new AccessDeniedException("Admin tidak dapat mengubah role miliknya sendiri");
        }

        // Role-unit determination (auto-assign for ADMIN/YAYASAN; validate for school roles)
        java.util.Set<String> validSchoolUnits = java.util.Set.of("KB-TK", "SD", "SMP", "SMA");
        String effectiveUnit;
        if (newRole == Role.ADMIN) {
            effectiveUnit = "SUPERADMIN";
        } else if (newRole == Role.YAYASAN) {
            effectiveUnit = "YAYASAN";
        } else {
            String requestedUnit = request.getUnit();
            if (requestedUnit == null || requestedUnit.isBlank()) {
                throw new RuntimeException("Error: Unit wajib diisi untuk role ini");
            }
            if (!validSchoolUnits.contains(requestedUnit)) {
                throw new RuntimeException("Error: Unit tidak valid untuk role ini. Pilih salah satu dari KB-TK, SD, SMP, SMA");
            }
            effectiveUnit = requestedUnit;
        }

        // Email uniqueness check (only if email changed)
        String newEmail = request.getEmail();
        if (!newEmail.equalsIgnoreCase(targetUser.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmailIgnoreCaseAndIsDeletedFalse(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(targetId)) {
                throw new DuplicateEmailException("Email sudah digunakan oleh pengguna lain");
            }
        }

        // SISWA field validation
        if (newRole == Role.SISWA) {
            if (request.getNisn() == null || request.getNisn().isBlank()) {
                throw new RuntimeException("Error: NISN wajib diisi untuk role Siswa");
            }
            if (!request.getNisn().matches("^[0-9]{10}$")) {
                throw new RuntimeException("Error: NISN harus tepat 10 digit angka");
            }
            if (request.getKelas() == null || request.getKelas().isBlank()) {
                throw new RuntimeException("Error: Kelas wajib diisi untuk role Siswa");
            }
        }

        // Apply updates
        targetUser.setName(request.getNamaLengkap());
        targetUser.setEmail(newEmail);
        targetUser.setPhoneNumber(
                (request.getNomorTelepon() == null || request.getNomorTelepon().isBlank()) ? "-" : request.getNomorTelepon()
        );
        targetUser.setRole(newRole);
        targetUser.setUnit(effectiveUnit);

        if (newRole == Role.SISWA) {
            targetUser.setNisn(request.getNisn());
            targetUser.setKelas(request.getKelas());
        } else {
            targetUser.setNisn(null);
            targetUser.setKelas(null);
        }

        userRepository.save(targetUser);

        UserPerUnitResponseDTO.UserPerUnitResponseDTOBuilder builder = UserPerUnitResponseDTO.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .name(targetUser.getName())
                .phoneNumber(targetUser.getPhoneNumber())
                .role(targetUser.getRole() != null ? targetUser.getRole().name() : null)
                .unit(targetUser.getUnit());

        if (newRole == Role.SISWA) {
            builder.nisn(targetUser.getNisn()).kelas(targetUser.getKelas());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void updateUserPasswordByAdmin(UUID targetId, AdminUpdatePasswordRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID adminId = userDetails.getId();

        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Error: Current user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Error: Unauthorized access, ADMIN role required");
        }

        User targetUser = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new NoSuchElementException("User tidak ditemukan"));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Error: Password baru dan konfirmasi password tidak sama");
        }

        validatePassword(request.getNewPassword());

        targetUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(targetUser);

        passwordHistoryService.recordPasswordChange(targetId, adminUser);
    }

    @Override
    public void deleteUserByAdmin(UUID targetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID adminId = userDetails.getId();

        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Error: Current user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Error: Unauthorized access, ADMIN role required");
        }

        // Find target (active only) — 404 if not found
        User targetUser = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Soft delete — no self-delete block (ADMIN can delete self)
        targetUser.setIsDeleted(true);
        targetUser.setDeletedAt(LocalDateTime.now());
        targetUser.setDeletedBy(adminId);
        userRepository.save(targetUser);
    }

    @Override
    public Page<SarprasLintasUnitResponseDTO> getSarprasLintasUnit(String filterUnit, String search, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Error: User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: Current user not found"));

        String currentUnit = currentUser.getUnit();
        if (currentUnit == null || currentUnit.isBlank()) {
            throw new RuntimeException("Error: Unit pengguna tidak ditemukan");
        }

        String filterUnitParam = (filterUnit == null || filterUnit.trim().isEmpty()) ? "" : filterUnit.trim();
        if (!filterUnitParam.isEmpty()) {
            List<String> validUnits = List.of("KB-TK", "SD", "SMP", "SMA");
            if (!validUnits.contains(filterUnitParam)) {
                throw new RuntimeException("Error: Unit filter tidak valid. Nilai yang diizinkan: KB-TK, SD, SMP, SMA");
            }
        }

        String namePhoneSearch = normalizeNamePhoneSearch(search);
        String emailSearch = normalizeEmailSearch(search);

        Page<User> usersPage = userRepository.findSarprasFromOtherUnits(
                Role.SARPRAS, currentUnit, filterUnitParam, namePhoneSearch, emailSearch, pageable);

        return usersPage.map(user -> SarprasLintasUnitResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .unit(user.getUnit())
                .build());
    }

    private String normalizeNamePhoneSearch(String search) {
        if (search == null || search.trim().isEmpty()) return "";
        // Remove all non-alphanumeric characters (including spaces and special chars)
        String cleaned = search.replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase();
        return cleaned.isEmpty() ? "" : "%" + cleaned + "%";
    }

    private String normalizeEmailSearch(String search) {
        if (search == null || search.trim().isEmpty()) return "";
        return "%" + search.trim().toLowerCase() + "%";
    }
}
