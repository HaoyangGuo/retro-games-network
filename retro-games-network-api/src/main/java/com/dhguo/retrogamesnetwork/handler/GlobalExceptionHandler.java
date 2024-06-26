package com.dhguo.retrogamesnetwork.handler;

import com.dhguo.retrogamesnetwork.exception.EmailAlreadyInUseException;
import com.dhguo.retrogamesnetwork.exception.InvalidImageException;
import com.dhguo.retrogamesnetwork.exception.OperationNotPermittedException;
import com.dhguo.retrogamesnetwork.exception.VerificationEmailSendException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorMessage("Account is locked.")
                .build());
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorMessage("Account is disabled.")
                .build());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorMessage("Bad credentials.")
                .build());
  }

  @ExceptionHandler(OperationNotPermittedException.class)
  public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorMessage(exp.getMessage())
                .build());
  }

  @ExceptionHandler(EmailAlreadyInUseException.class)
  public ResponseEntity<ExceptionResponse> handleException(EmailAlreadyInUseException exp) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .errorMessage("Email already in use.")
                .build());
  }

  @ExceptionHandler(VerificationEmailSendException.class)
  public ResponseEntity<ExceptionResponse> handleException(VerificationEmailSendException exp) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .errorMessage("An error occurred while trying to send you a verification email, please try again later.")
                .build());
  }

  @ExceptionHandler(MessagingException.class)
  public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(exp.getMessage())
                .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
    Set<String> errors = new HashSet<>();
    exp.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              var errorMessage = error.getDefaultMessage();
              errors.add(errorMessage);
            });

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Bad request.")
                .validationErrors(errors)
                .build());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ExceptionResponse> handleException(HttpMessageNotReadableException exp) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Invalid JSON body format.")
                .build());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exp) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(exp.getMessage())
                .build());
  }

  @ExceptionHandler(InvalidImageException.class)
  public ResponseEntity<ExceptionResponse> handleException(InvalidImageException exp) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(exp.getMessage())
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
    exp.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ExceptionResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("Something went wrong, please try again later.")
                .build());
  }
}
