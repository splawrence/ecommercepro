package com.splawrence.ecommercepro.controller;

import com.splawrence.ecommercepro.exception.ResourceNotFoundException;
import com.splawrence.ecommercepro.model.ErrorMessage;
import java.time.LocalDateTime;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

  /**
   * Handles the ResourceNotFoundException and returns a ResponseEntity with an ErrorMessage.
   *
   * @param ex      The ResourceNotFoundException that was thrown.
   * @param request The WebRequest object containing the request details.
   * @return A ResponseEntity containing an ErrorMessage and the HTTP status code.
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorMessage> resourceNotFoundException(
      ResourceNotFoundException ex,
      WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(
        404,
        LocalDateTime.now(),
        ex.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles the BadRequestException and returns a ResponseEntity with an ErrorMessage.
   *
   * @param ex      The BadRequestException that was thrown.
   * @param request The WebRequest object containing the request details.
   * @return A ResponseEntity containing an ErrorMessage and HttpStatus.BAD_REQUEST.
   */
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorMessage> badRequestException(
      BadRequestException ex,
      WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(
        400,
        LocalDateTime.now(),
        ex.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions thrown by the controller.
   *
   * @param ex      The exception that was thrown.
   * @param request The web request that triggered the exception.
   * @return A ResponseEntity containing an ErrorMessage object and the HTTP status code.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> exceptionHandler(
      Exception ex,
      WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(
        500,
        LocalDateTime.now(),
        ex.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
