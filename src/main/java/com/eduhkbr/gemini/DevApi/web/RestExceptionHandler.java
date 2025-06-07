package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para exceções REST.
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Constrói o corpo da resposta de erro.
     *
     * @param error   Mensagem de erro.
     * @param status  Status HTTP.
     * @param request Requisição web, pode ser nula.
     * @return Mapa contendo os detalhes do erro.
     */
    private Map<String, Object> buildErrorBody(String error, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("path", request != null ? request.getDescription(false).replace("uri=", "") : null);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = buildErrorBody("Erro de validação", HttpStatus.BAD_REQUEST, request);
        body.put("fieldErrors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> body = buildErrorBody(ex.getMessage(), HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex, WebRequest request) {
        Map<String, Object> body = buildErrorBody(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, request);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> body = buildErrorBody("Erro interno inesperado: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
