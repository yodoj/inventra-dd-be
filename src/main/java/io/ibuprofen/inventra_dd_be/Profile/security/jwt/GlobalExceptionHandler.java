package io.ibuprofen.inventra_dd_be.Profile.security.jwt;

import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import java.util.NoSuchElementException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(BaseResponseDTO.error(400, "Validation Error: " + errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "JSON parse error: Data format tidak sesuai atau ada field yang salah";

        // Log the actual cause for debugging if needed
        // logger.error("Parse error: ", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(BaseResponseDTO.error(400, message));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(404)
                .body(BaseResponseDTO.error(404, "Error: " + ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(403)
                .body(BaseResponseDTO.error(403, "Error: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500)
                .body(BaseResponseDTO.error(500, "Internal Server Error: " + ex.getMessage()));
    }
}
