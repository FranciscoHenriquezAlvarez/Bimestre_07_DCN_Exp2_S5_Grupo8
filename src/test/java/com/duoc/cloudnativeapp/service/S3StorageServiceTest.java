package com.duoc.cloudnativeapp.service;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class S3StorageServiceTest {

    @Test
    void debeConstruirLaKeyEsperadaParaS3() {
        S3StorageService s3StorageService = new S3StorageService(mock(S3Client.class), "bucket-prueba");

        assertThat(s3StorageService.construirKey(15L))
                .isEqualTo("resumenes/15/resumen-inscripcion-15.txt");
    }
}
