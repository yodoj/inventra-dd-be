package io.ibuprofen.inventra_dd_be.Profile.services;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.CreateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        // Email uniqueness check (case-insensitive)
        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
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

        String phone = (request.getNomorTelepon() == null || request.getNomorTelepon().isBlank()) 
                      ? "-" : request.getNomorTelepon();

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

        // Fetch the requested user
        Optional<User> targetUserOpt = userRepository.findById(userId);
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
                .unit(targetUser.getUnit());

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
}
