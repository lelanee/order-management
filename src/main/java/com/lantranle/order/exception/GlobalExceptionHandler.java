package com.lantranle.order.exception;

import com.lantranle.order.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Converts missing domain records into a consistent 404 response body for clients.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), null);
  }

  /**
   * Converts unique constraint conflicts such as duplicate user data into HTTP 409.
   */
  @ExceptionHandler(EntityExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleEntityExistsException(EntityExistsException exception) {
    return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), null);
  }

  /**
   * Converts business validation failures into HTTP 400 with a readable message.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
  }

  /**
   * Returns field-level validation errors for invalid request DTO payloads.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();

    exception.getBindingResult().getFieldErrors()
      .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  private ErrorResponse buildErrorResponse(
    HttpStatus status,
    String message,
    Map<String, String> fieldErrors
  ) {
    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(status.value())
      .error(status.getReasonPhrase())
      .message(message)
      .fieldErrors(fieldErrors)
      .build();
  }
}
