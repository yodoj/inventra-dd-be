package io.ibuprofen.inventra_dd_be.Profile.restcontroller;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.CreateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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

    @GetMapping("/per-unit/{id}")
    @PreAuthorize("hasAuthority('SARPRAS')")
    public ResponseEntity<?> getUserDetailInSameUnit(@PathVariable UUID id) {
        UserPerUnitResponseDTO user = userManagementService.getUserDetailInSameUnit(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(user, "Successfully retrieved user detail"));
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
            String message = e.getMessage();
            int status = message.contains("Unauthorized") ? 403 : 400;
            return ResponseEntity.status(status).body(BaseResponseDTO.error(status, message));
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }
}
