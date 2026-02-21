package io.ibuprofen.inventra_dd_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private T data;

    public static <T> BaseResponseDTO<T> ok(T data, String message) {
        return BaseResponseDTO.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponseDTO<T> error(int status, String message) {
        return BaseResponseDTO.<T>builder()
                .status(status)
                .message(message)
                .build();
    }
}
