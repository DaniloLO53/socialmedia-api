package org.xuxuchat.app.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.xuxuchat.app.payload.api.APIExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> customConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();

        e.getConstraintViolations().forEach(violation -> {
            String completeFieldPath = violation.getPropertyPath().toString();
            String fieldName = completeFieldPath.substring(completeFieldPath.lastIndexOf(".") + 1);
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> customMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIExceptionResponse> customConflictException(ConflictException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.CONFLICT.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIExceptionResponse> customBadRequestException(BadRequestException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.BAD_REQUEST.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<APIExceptionResponse> customForbiddenException(ForbiddenException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.FORBIDDEN.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    // It handles Spring MVC exception for denied access
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<APIExceptionResponse> handleAccessDenied(AuthorizationDeniedException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.FORBIDDEN.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<APIExceptionResponse> customResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.CONFLICT.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIExceptionResponse> customResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.NOT_FOUND.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<APIExceptionResponse> customUnprocessableEntityException(UnprocessableEntityException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiResponse);
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<APIExceptionResponse> customInvalidJwtException(InvalidJwtException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.UNAUTHORIZED.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIExceptionResponse> RuntimeException(RuntimeException e) {
        String message = e.getMessage();
        Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        APIExceptionResponse apiResponse = new APIExceptionResponse(message, statusCode);

        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
