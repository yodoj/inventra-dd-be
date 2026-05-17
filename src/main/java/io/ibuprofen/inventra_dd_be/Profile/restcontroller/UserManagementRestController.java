package io.ibuprofen.inventra_dd_be.Profile.restcontroller;

import io.ibuprofen.inventra_dd_be.Profile.exception.DuplicateEmailException;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.AdminUpdatePasswordRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.CreateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.UpdateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.SarprasLintasUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserManagementRestController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping("/per-unit")
    public ResponseEntity<?> getUsersInSameUnit(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // Pageable page starts from 0 in Spring Data
            Pageable pageable = PageRequest.of(page - 1, limit);

            Page<UserPerUnitResponseDTO> usersPage = userManagementService.getUsersInSameUnit(search, role, pageable);

            // Constructing response format matching BaseResponseDTO or a custom pagination map
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", usersPage.getContent());
            responseData.put("total_items", usersPage.getTotalElements());
            responseData.put("total_pages", usersPage.getTotalPages());
            responseData.put("current_page", usersPage.getNumber() + 1);

            return ResponseEntity.ok(BaseResponseDTO.ok(responseData, "Successfully retrieved users in unit"));

        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = message.contains("Unauthorized") || message.contains("authenticat") ? 401 : 400; // General mapping
            if (message.contains("Unauthorized access")) status = 403;
            
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error retrieving users in unit: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @GetMapping("/sarpras-lintas-unit")
    @PreAuthorize("hasAuthority('SARPRAS')")
    public ResponseEntity<?> getSarprasLintasUnit(
            @RequestParam(required = false, defaultValue = "") String unit,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit);
            Page<SarprasLintasUnitResponseDTO> usersPage = userManagementService.getSarprasLintasUnit(unit, search, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", usersPage.getContent());
            responseData.put("total_items", usersPage.getTotalElements());
            responseData.put("total_pages", usersPage.getTotalPages());
            responseData.put("current_page", usersPage.getNumber() + 1);

            return ResponseEntity.ok(BaseResponseDTO.ok(responseData, "Successfully retrieved sarpras lintas unit"));
        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = 400;
            if (message.contains("Unauthorized access")) status = 403;
            if (message.contains("not authenticated")) status = 401;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @GetMapping("/per-unit/{id}")
    @PreAuthorize("hasAuthority('SARPRAS')")
    public ResponseEntity<?> getUserDetailInSameUnit(@PathVariable UUID id) {
        UserPerUnitResponseDTO user = userManagementService.getUserDetailInSameUnit(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(user, "Successfully retrieved user detail"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getUserDetailByAdmin(@PathVariable UUID id) {
        try {
            UserPerUnitResponseDTO user = userManagementService.getUserDetailByAdmin(id);
            return ResponseEntity.ok(BaseResponseDTO.ok(user, "Successfully retrieved user detail"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, e.getMessage()));
        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = message != null && message.contains("Unauthorized") ? 403 : 400;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // Pageable page starts from 0 in Spring Data
            Pageable pageable = PageRequest.of(page - 1, limit);

            Page<UserPerUnitResponseDTO> usersPage = userManagementService.getAllUsers(unit, search, role, pageable);

            // Constructing response format matching BaseResponseDTO or a custom pagination map
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", usersPage.getContent());
            responseData.put("total_items", usersPage.getTotalElements());
            responseData.put("total_pages", usersPage.getTotalPages());
            responseData.put("current_page", usersPage.getNumber() + 1);

            return ResponseEntity.ok(BaseResponseDTO.ok(responseData, "Successfully retrieved all users"));

        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = message.contains("Unauthorized") || message.contains("authenticat") ? 401 : 400; // General mapping
            if (message.contains("Unauthorized access")) status = 403;
            
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDTO request) {
        try {
            userManagementService.createUser(request);
            return ResponseEntity.status(201).body(BaseResponseDTO.created(null, "Akun pengguna berhasil ditambahkan"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            String message = e.getMessage();
            int status = message.contains("Unauthorized") ? 403 : 400;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
            if (isAdmin) {
                userManagementService.deleteUserByAdmin(id);
            } else {
                userManagementService.deleteUser(id);
            }
            return ResponseEntity.ok(BaseResponseDTO.ok(null, "Akun pengguna berhasil dihapus"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, e.getMessage()));
        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = message.contains("Unauthorized") ? 403 : 400;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequestDTO request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
            UserPerUnitResponseDTO updatedUser;
            if (isAdmin) {
                updatedUser = userManagementService.updateUserByAdmin(id, request);
            } else {
                updatedUser = userManagementService.updateUser(id, request);
            }
            return ResponseEntity.ok(BaseResponseDTO.ok(updatedUser, "Akun pengguna berhasil diperbarui"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, e.getMessage()));
        } catch (DuplicateEmailException e) {
            return ResponseEntity.status(409).body(BaseResponseDTO.error(409, e.getMessage()));
        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = message != null && message.contains("Unauthorized") ? 403 : 400;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> updateUserPassword(@PathVariable UUID id, @RequestBody AdminUpdatePasswordRequestDTO request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
            if (isAdmin) {
                userManagementService.updateUserPasswordByAdmin(id, request);
            } else {
                userManagementService.updateUserPassword(id, request);
            }
            return ResponseEntity.ok(BaseResponseDTO.ok(null, "Password pengguna berhasil diperbarui"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, e.getMessage()));
        } catch (RuntimeException e) {
            String message = e.getMessage();
            int status = message != null && message.contains("Unauthorized") ? 403 : 400;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error updating user password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }
}
