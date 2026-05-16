package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaporanUtilisasiResponseDTO<T> {

    @JsonProperty("status")
    private int status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private List<T> data;

    @JsonProperty("total_data")
    private long totalData;

    @JsonProperty("total_page")
    private int totalPage;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("limit")
    private int limit;

    public static <T> LaporanUtilisasiResponseDTO<T> success(List<T> data, long totalData, int totalPage, int currentPage, int limit, String message) {
        return LaporanUtilisasiResponseDTO.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .totalData(totalData)
                .totalPage(totalPage)
                .currentPage(currentPage)
                .limit(limit)
                .build();
    }
}
