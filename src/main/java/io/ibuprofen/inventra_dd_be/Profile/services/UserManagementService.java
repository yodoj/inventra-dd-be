package io.ibuprofen.inventra_dd_be.Profile.services;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.restdto.request.CreateUserRequestDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.UserPerUnitResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    Page<UserPerUnitResponseDTO> getUsersInSameUnit(String search, Role role, Pageable pageable);
    
    Page<UserPerUnitResponseDTO> getAllUsers(String unit, String search, Role role, Pageable pageable);

    void createUser(CreateUserRequestDTO request);
    
    UserPerUnitResponseDTO getUserDetailInSameUnit(java.util.UUID userId);
}
