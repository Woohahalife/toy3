package com.core.toy3.common.exception;

import com.core.toy3.common.response.Response;
import com.core.toy3.common.response.ResponseStatus;
import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> globalExceptionHandler(Exception e) {
        log.error("[InternalServerError Occurs] error: {}", e.getMessage());
        return ResponseEntity.status(ResponseStatus.INTERNAL_SERVER_ERROR.getStatusCode())
                .body(Response.response(ResponseStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response> CustomExceptionHandler(CustomException e) {
        log.warn("[CustomException Occurs] message: {} HttpStatusCode: {}", e.getStatus().getMessage(),
                e.getStatus().getStatusCode());

        return ResponseEntity.status(e.getStatus().getStatusCode())
                .body(Response.response(e.getStatus()));
    }

    @ExceptionHandler(RuntimeException.class) // 서버-클라이언트 충돌시
    public ResponseEntity<Response> runtimeExceptionHandler(RuntimeException e) {
        log.error("[RuntimeException Occurs] error: {}", e.getMessage());

        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.response(HttpStatus.CONFLICT.toString()));
    }
}
