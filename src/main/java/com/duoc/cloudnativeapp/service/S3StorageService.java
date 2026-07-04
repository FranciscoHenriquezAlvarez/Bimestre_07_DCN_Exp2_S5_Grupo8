package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.ArchivoResumenResponseDTO;
import com.duoc.cloudnativeapp.exception.ArchivoNoEncontradoException;
import com.duoc.cloudnativeapp.exception.S3StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Path;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(S3Client s3Client,
                            @Value("${aws.s3.bucket-name:}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    // Sube a S3 el archivo del resumen usando la key definida para la inscripcion.
    public ArchivoResumenResponseDTO subirArchivo(Long inscripcionId, Path archivo) {
        validarBucketConfigurado();

        String key = construirKey(inscripcionId);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType("text/plain")
                            .build(),
                    RequestBody.fromFile(archivo)
            );
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para subir el archivo", exception);
        } catch (S3Exception exception) {
            throw crearErrorOperacionS3("No fue posible subir el archivo a S3", exception);
        }

        return new ArchivoResumenResponseDTO(
                "Archivo subido correctamente a S3",
                archivo.toAbsolutePath().toString(),
                archivo.getFileName().toString(),
                bucketName,
                key,
                Boolean.TRUE
        );
    }

    // Usa la misma key para reemplazar el archivo existente o crearlo si aun no estaba.
    public ArchivoResumenResponseDTO reemplazarArchivo(Long inscripcionId, Path archivo) {
        boolean existeAntes = existeArchivo(inscripcionId);
        ArchivoResumenResponseDTO respuesta = subirArchivo(inscripcionId, archivo);
        respuesta.setMensaje(existeAntes
                ? "Archivo reemplazado correctamente en S3"
                : "El archivo no existia en S3 y fue creado correctamente");
        return respuesta;
    }

    // Consulta si el archivo ya existe en el bucket configurado.
    public ArchivoResumenResponseDTO consultarArchivo(Long inscripcionId) {
        validarBucketConfigurado();
        boolean existe = existeArchivo(inscripcionId);

        return new ArchivoResumenResponseDTO(
                existe ? "Archivo encontrado en S3" : "Archivo no encontrado en S3",
                null,
                "resumen-inscripcion-" + inscripcionId + ".txt",
                bucketName,
                construirKey(inscripcionId),
                existe
        );
    }

    // Descarga el contenido del archivo guardado en S3.
    public byte[] descargarArchivo(Long inscripcionId) {
        validarBucketConfigurado();

        String key = construirKey(inscripcionId);
        if (!existeArchivo(inscripcionId)) {
            throw new ArchivoNoEncontradoException("El archivo no existe en S3");
        }

        try {
            ResponseBytes<GetObjectResponse> archivo = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
            return archivo.asByteArray();
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para descargar el archivo", exception);
        } catch (S3Exception exception) {
            throw crearErrorOperacionS3("No fue posible descargar el archivo desde S3", exception);
        }
    }

    // Elimina el archivo del resumen despues de validar que exista.
    public ArchivoResumenResponseDTO eliminarArchivo(Long inscripcionId) {
        validarBucketConfigurado();

        String key = construirKey(inscripcionId);
        if (!existeArchivo(inscripcionId)) {
            throw new ArchivoNoEncontradoException("El archivo no existe en S3");
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para eliminar el archivo", exception);
        } catch (S3Exception exception) {
            throw crearErrorOperacionS3("No fue posible eliminar el archivo desde S3", exception);
        }

        return new ArchivoResumenResponseDTO(
                "Archivo eliminado correctamente de S3",
                null,
                null,
                bucketName,
                key,
                Boolean.FALSE
        );
    }

    // Arma la ruta esperada dentro del bucket para cada inscripcion.
    public String construirKey(Long inscripcionId) {
        return "resumenes/" + inscripcionId + "/resumen-inscripcion-" + inscripcionId + ".txt";
    }

    // Usa headObject para verificar si el archivo ya esta almacenado en S3.
    private boolean existeArchivo(Long inscripcionId) {
        String key = construirKey(inscripcionId);

        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para consultar el archivo", exception);
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }

            throw crearErrorOperacionS3("No fue posible consultar el archivo en S3", exception);
        }
    }

    private void validarBucketConfigurado() {
        if (!StringUtils.hasText(bucketName)) {
            throw new S3StorageException("El bucket S3 no esta configurado", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private S3StorageException crearErrorOperacionS3(String mensaje, AwsServiceException exception) {
        return new S3StorageException(mensaje, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private S3StorageException crearErrorConexionS3(String mensaje, SdkClientException exception) {
        return new S3StorageException(mensaje, HttpStatus.SERVICE_UNAVAILABLE, exception);
    }
}
