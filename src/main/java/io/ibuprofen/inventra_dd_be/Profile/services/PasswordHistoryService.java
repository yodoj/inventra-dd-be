package io.ibuprofen.inventra_dd_be.Profile.services;

import io.ibuprofen.inventra_dd_be.Profile.model.PasswordHistory;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.PasswordHistoryRepository;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.PasswordHistoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PasswordHistoryService {

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    /**
     * Mencatat history perubahan password.
     * Method ini dipanggil SETELAH password berhasil diupdate dan disimpan ke database.
     * 
     * @param userId User yang passwordnya diubah
     * @param changedByUser User yang melakukan perubahan (bisa same dengan userId atau admin)
     */
    public void recordPasswordChange(UUID userId, User changedByUser) {
        PasswordHistory history = PasswordHistory.builder()
                .userId(userId)
                .changedByUserId(changedByUser.getId())
                .changedByFullName(changedByUser.getName())
                .changedByRole(changedByUser.getRole().toString())
                .changedAt(LocalDateTime.now())
                .build();

        passwordHistoryRepository.save(history);
    }

    /**
     * Mengambil history password untuk user tertentu.
     * 
     * @param userId User yang history passwordnya diminta
     * @return List of PasswordHistoryResponseDTO, diurutkan dari terbaru ke terlama
     */
    public List<PasswordHistoryResponseDTO> getPasswordHistory(UUID userId) {
        List<PasswordHistory> histories = passwordHistoryRepository.findByUserIdOrderByChangedAtDesc(userId);
        
        return histories.stream()
                .map(history -> PasswordHistoryResponseDTO.builder()
                        .changedAt(history.getChangedAt())
                        .changedByFullName(history.getChangedByFullName())
                        .changedByRole(history.getChangedByRole())
                        .build())
                .collect(Collectors.toList());
    }
}
