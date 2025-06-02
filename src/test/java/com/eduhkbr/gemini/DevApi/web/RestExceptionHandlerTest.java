package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RestExceptionHandlerTest {
    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    @DisplayName("Deve tratar BusinessException e retornar status 422")
    void testHandleBusinessException() {
        BusinessException ex = new BusinessException("Erro de negócio");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/test");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsEntry("error", "Erro de negócio");
        assertThat(response.getBody()).containsEntry("path", "/api/test");
    }

    @Test
    @DisplayName("Deve tratar Exception genérica e retornar status 500")
    void testHandleGenericException() {
        Exception ex = new Exception("Falha inesperada");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/erro");
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("error").toString()).contains("Falha inesperada");
        assertThat(response.getBody()).containsEntry("path", "/api/erro");
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException e retornar status 400 com fieldErrors")
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/valida");
        // Simula lista de field errors
        org.springframework.validation.FieldError fieldError = new org.springframework.validation.FieldError("obj", "campo", "msg");
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("path", "/api/valida");
        assertThat(response.getBody()).containsKey("fieldErrors");
        Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
        assertThat(fieldErrors).containsEntry("campo", "msg");
    }
}
