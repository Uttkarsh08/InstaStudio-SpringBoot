package com.uttkarsh.InstaStudio.advices;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.uttkarsh.InstaStudio.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>>handleResourceNotFoundException(ResourceNotFoundException exception){
        ApiError apiError =ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AdminAlreadyAssignedException.class)
    public ResponseEntity<ApiResponse<?>> handleAdminAlreadyAssignedException(AdminAlreadyAssignedException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(EventIsParentEventException.class)
    public ResponseEntity<ApiResponse<?>> handleEventIsParentException(EventIsParentEventException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(EventIsSubEventException.class)
    public ResponseEntity<ApiResponse<?>> handleEventIsSubEventException(EventIsSubEventException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }


    @ExceptionHandler(EventAlreadyAddedException.class)
    public ResponseEntity<ApiResponse<?>> handleEventAlreadyAddedException(EventAlreadyAddedException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(EventAlreadyAssignedException.class)
    public ResponseEntity<ApiResponse<?>> handleEventAlreadyAssignedException(EventAlreadyAssignedException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(UserAlreadyAssignedException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyAssignedException(UserAlreadyAssignedException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(EventNotAssignedException.class)
    public ResponseEntity<ApiResponse<?>> handleEventNotAssignedException(EventNotAssignedException ex){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(error);
    }

    @ExceptionHandler(UnregisteredUserException.class)
    public ResponseEntity<ApiResponse<?>> handleUnregisteredUser(UnregisteredUserException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidToken(InvalidTokenException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Field '%s': %s", error.getField(), error.getDefaultMessage()))
                .toList();

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .subErrors(validationErrors)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseException(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request or invalid enum value";

        if (ex.getCause() instanceof InvalidFormatException invalidFormat) {
            Class<?> targetType = invalidFormat.getTargetType();
            if (targetType.isEnum()) {
                Object[] validEnums = targetType.getEnumConstants();
                message = "Invalid value. Allowed values: " + Arrays.toString(validEnums);
            }
        }

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .build();

        return buildErrorResponseEntity(apiError);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }
}
