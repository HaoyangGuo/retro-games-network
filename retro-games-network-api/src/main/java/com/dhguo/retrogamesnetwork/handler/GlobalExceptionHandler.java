package com.dhguo.retrogamesnetwork.handler;

import com.dhguo.retrogamesnetwork.exception.InvalidImageException;
import com.dhguo.retrogamesnetwork.exception.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .errorMessage("Not authorized.")
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
