package com.duoc.cloudnativeapp.dto;

// Respuesta usada para informar el estado del archivo local o del objeto en S3.
public class ArchivoResumenResponseDTO {

    private String mensaje;
    private String rutaLocal;
    private String nombreArchivo;
    private String bucket;
    private String key;
    private Boolean existe;

    public ArchivoResumenResponseDTO() {
    }

    public ArchivoResumenResponseDTO(String mensaje,
                                     String rutaLocal,
                                     String nombreArchivo,
                                     String bucket,
                                     String key,
                                     Boolean existe) {
        this.mensaje = mensaje;
        this.rutaLocal = rutaLocal;
        this.nombreArchivo = nombreArchivo;
        this.bucket = bucket;
        this.key = key;
        this.existe = existe;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRutaLocal() {
        return rutaLocal;
    }

    public void setRutaLocal(String rutaLocal) {
        this.rutaLocal = rutaLocal;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getExiste() {
        return existe;
    }

    public void setExiste(Boolean existe) {
        this.existe = existe;
    }
}
