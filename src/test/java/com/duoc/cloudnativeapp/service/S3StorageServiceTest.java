package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.exception.ArchivoNoEncontradoException;
import com.duoc.cloudnativeapp.exception.S3StorageException;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class S3StorageServiceTest {

    @Test
    void debeConstruirLaKeyEsperadaParaS3() {
        S3StorageService s3StorageService = new S3StorageService(mock(S3Client.class), "bucket-prueba");

        assertThat(s3StorageService.construirKey(15L))
                .isEqualTo("resumenes/15/resumen-inscripcion-15.txt");
    }

    @Test
    void debeRetornarErrorCuandoElArchivoNoExisteEnS3() {
        S3Client s3Client = mock(S3Client.class);
        doThrow(S3Exception.builder().statusCode(404).message("No existe").build())
                .when(s3Client)
                .headObject(any(HeadObjectRequest.class));

        S3StorageService s3StorageService = new S3StorageService(s3Client, "bucket-prueba");

        assertThatThrownBy(() -> s3StorageService.descargarArchivo(5L))
                .isInstanceOf(ArchivoNoEncontradoException.class)
                .hasMessage("El archivo no existe en S3");
    }

    @Test
    void debeRetornarErrorClaroCuandoFallaLaConexionConS3() {
        S3Client s3Client = mock(S3Client.class);
        doThrow(SdkClientException.create("Sin conectividad"))
                .when(s3Client)
                .headObject(any(HeadObjectRequest.class));

        S3StorageService s3StorageService = new S3StorageService(s3Client, "bucket-prueba");

        assertThatThrownBy(() -> s3StorageService.consultarArchivo(8L))
                .isInstanceOf(S3StorageException.class)
                .hasMessage("No fue posible conectar con AWS S3 para consultar el archivo");
    }
}
