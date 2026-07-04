package com.duoc.cloudnativeapp.exception;

import org.springframework.http.HttpStatus;

// Representa problemas de configuracion, conexion u operacion contra AWS S3.
public class S3StorageException extends RuntimeException {

    private final HttpStatus status;

    public S3StorageException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public S3StorageException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
