package com.duoc.cloudnativeapp.exception;

// Se usa cuando el archivo del resumen no existe localmente o en S3.
public class ArchivoNoEncontradoException extends RuntimeException {

    public ArchivoNoEncontradoException(String message) {
        super(message);
    }
}
