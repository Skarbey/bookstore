package com.parkhomovsky.bookstore.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, Object> body = new LinkedMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        body.put(ERROR, errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleUniqueDataDuplicate(
            DataIntegrityViolationException ex
    ) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        String errorMessage = "An error occurred while processing the request.";
        if (rootCause != null) {
            errorMessage = "Error: " + rootCause.getMessage();
        }
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    private String getErrorMessage(ObjectError ex) {
        if (ex instanceof FieldError) {
            String field = ((FieldError) ex).getField();
            String defaultMessage = ex.getDefaultMessage();
            return defaultMessage + " for " + field;
        }
        return ex.getDefaultMessage();
    }
}