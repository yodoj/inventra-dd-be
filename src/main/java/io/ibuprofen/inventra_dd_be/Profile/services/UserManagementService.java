package io.ibuprofen.inventra_dd_be.Profile.services;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.AdminUpdatePasswordRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.CreateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.UpdateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.SarprasLintasUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    Page<UserPerUnitResponseDTO> getUsersInSameUnit(String search, Role role, Pageable pageable);
    
    Page<UserPerUnitResponseDTO> getAllUsers(String unit, String search, Role role, Pageable pageable);

    void createUser(CreateUserRequestDTO request);
    
    UserPerUnitResponseDTO getUserDetailInSameUnit(java.util.UUID userId);

    void deleteUser(java.util.UUID targetId);

    UserPerUnitResponseDTO updateUser(java.util.UUID targetId, UpdateUserRequestDTO request);

    void updateUserPassword(java.util.UUID targetId, AdminUpdatePasswordRequestDTO request);

    UserPerUnitResponseDTO getUserDetailByAdmin(java.util.UUID userId);

    UserPerUnitResponseDTO updateUserByAdmin(java.util.UUID targetId, UpdateUserRequestDTO request);

    void updateUserPasswordByAdmin(java.util.UUID targetId, AdminUpdatePasswordRequestDTO request);

    void deleteUserByAdmin(java.util.UUID targetId);

    Page<SarprasLintasUnitResponseDTO> getSarprasLintasUnit(String filterUnit, String search, Pageable pageable);
}
