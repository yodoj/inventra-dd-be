package io.ibuprofen.inventra_dd_be.Profile.services;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

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
}
