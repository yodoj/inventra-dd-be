package io.ibuprofen.inventra_dd_be.Profile.repository;

import io.ibuprofen.inventra_dd_be.Profile.model.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {
    List<PasswordHistory> findByUserIdOrderByChangedAtDesc(UUID userId);
}
