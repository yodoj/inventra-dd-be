package io.ibuprofen.inventra_dd_be.Profile.repository;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCaseAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.unit = :unit " +
           "AND (:role IS NULL OR u.role = :role) " +
           "AND (:search = '' OR LOWER(u.name) LIKE :search " +
           "OR LOWER(u.email) LIKE :search " +
           "OR LOWER(u.phoneNumber) LIKE :search) " +
           "AND u.isDeleted = false")
    Page<User> findUsersByUnitWithFilters(@Param("unit") String unit, 
                                          @Param("role") Role role, 
                                          @Param("search") String search, 
                                          Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(:unit = '' OR u.unit = :unit) " +
           "AND (:role IS NULL OR u.role = :role) " +
           "AND (:search = '' OR LOWER(u.name) LIKE :search " +
           "OR LOWER(u.email) LIKE :search " +
           "OR LOWER(u.phoneNumber) LIKE :search) " +
           "AND u.isDeleted = false")
    Page<User> findAllUsersWithFilters(@Param("unit") String unit, 
                                       @Param("role") Role role, 
                                       @Param("search") String search, 
                                       Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role " +
           "AND u.unit != :currentUnit " +
           "AND u.unit NOT IN ('YAYASAN', 'SUPERADMIN') " +
           "AND (:filterUnit = '' OR u.unit = :filterUnit) " +
           "AND (:namePhoneSearch = '' OR replace(lower(u.name), ' ', '') LIKE :namePhoneSearch " +
           "OR replace(lower(u.phoneNumber), ' ', '') LIKE :namePhoneSearch " +
           "OR lower(u.email) LIKE :emailSearch) " +
           "AND u.isDeleted = false")
    Page<User> findSarprasFromOtherUnits(@Param("role") Role role,
                                         @Param("currentUnit") String currentUnit,
                                         @Param("filterUnit") String filterUnit,
                                         @Param("namePhoneSearch") String namePhoneSearch,
                                         @Param("emailSearch") String emailSearch,
                                         Pageable pageable);
}
