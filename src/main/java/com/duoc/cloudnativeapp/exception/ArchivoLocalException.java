package com.duoc.cloudnativeapp.exception;

// Representa errores al generar o manipular el archivo local del resumen.
public class ArchivoLocalException extends RuntimeException {

    public ArchivoLocalException(String message, Throwable cause) {
        super(message, cause);
    }
}
