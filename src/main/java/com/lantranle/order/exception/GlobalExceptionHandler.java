package com.lantranle.order.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Converts missing domain records into a readable web error page.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ModelAndView handleEntityNotFoundException(EntityNotFoundException exception) {
    return buildErrorPage(HttpStatus.NOT_FOUND, exception.getMessage(), null);
  }

  /**
   * Converts unique constraint conflicts such as duplicate user data into HTTP 409.
   */
  @ExceptionHandler(EntityExistsException.class)
  public ModelAndView handleEntityExistsException(EntityExistsException exception) {
    return buildErrorPage(HttpStatus.CONFLICT, exception.getMessage(), null);
  }

  /**
   * Converts business validation failures into HTTP 400 with a readable message.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ModelAndView handleIllegalArgumentException(IllegalArgumentException exception) {
    return buildErrorPage(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
  }

  /**
   * Returns field-level validation errors for invalid request DTO payloads.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ModelAndView handleValidationException(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();

    exception.getBindingResult().getFieldErrors()
      .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return buildErrorPage(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  private ModelAndView buildErrorPage(
    HttpStatus status,
    String message,
    Map<String, String> fieldErrors
  ) {
    ModelAndView modelAndView = new ModelAndView("error");
    ModelMap model = modelAndView.getModelMap();
    model.addAttribute("timestamp", LocalDateTime.now());
    model.addAttribute("status", status.value());
    model.addAttribute("error", status.getReasonPhrase());
    model.addAttribute("message", message);
    model.addAttribute("fieldErrors", fieldErrors);
    modelAndView.setStatus(status);
    return modelAndView;
  }
}
