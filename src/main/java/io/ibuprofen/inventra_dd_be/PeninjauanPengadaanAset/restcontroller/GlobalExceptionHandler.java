package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restcontroller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseResponseDTO<?> badRequest(IllegalArgumentException e) {
    return BaseResponseDTO.error(400, e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseResponseDTO<?> illegalState(IllegalStateException e) {
    return BaseResponseDTO.error(400, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BaseResponseDTO<?> validation(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(err -> err.getDefaultMessage())
        .orElse("Validation error");
    return BaseResponseDTO.error(400, msg);
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public BaseResponseDTO<?> forbidden(AccessDeniedException e) {
    return BaseResponseDTO.error(403, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public BaseResponseDTO<?> internal(Exception e) {
    return BaseResponseDTO.error(500, "Internal server error: " + e.getMessage());
  }
}