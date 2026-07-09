package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.dto.ArchivoResumenResponseDTO;
import com.duoc.cloudnativeapp.dto.InscripcionRequestDTO;
import com.duoc.cloudnativeapp.dto.InscripcionResumenDTO;
import com.duoc.cloudnativeapp.dto.ResumenInscripcionMensaje;
import com.duoc.cloudnativeapp.model.ResumenInscripcionMq;
import com.duoc.cloudnativeapp.service.InscripcionService;
import com.duoc.cloudnativeapp.service.ResumenInscripcionMqService;
import com.duoc.cloudnativeapp.service.ResumenArchivoService;
import com.duoc.cloudnativeapp.service.S3StorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final ResumenArchivoService resumenArchivoService;
    private final S3StorageService s3StorageService;
    private final ResumenInscripcionMqService resumenInscripcionMqService;

    // Inyeccion del servicio por constructor
    public InscripcionController(InscripcionService inscripcionService,
                                 ResumenArchivoService resumenArchivoService,
                                 S3StorageService s3StorageService,
                                 ResumenInscripcionMqService resumenInscripcionMqService) {
        this.inscripcionService = inscripcionService;
        this.resumenArchivoService = resumenArchivoService;
        this.s3StorageService = s3StorageService;
        this.resumenInscripcionMqService = resumenInscripcionMqService;
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResumenDTO>> listar() {
        return ResponseEntity.ok(inscripcionService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<InscripcionResumenDTO> crear(@Valid @RequestBody InscripcionRequestDTO inscripcionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.guardar(inscripcionRequestDTO));
    }

    // Envia el resumen de una inscripcion a RabbitMQ para procesamiento asincrono.
    @PostMapping("/{inscripcionId}/enviar-mq")
    public ResponseEntity<ResumenInscripcionMensaje> enviarResumenAMq(@PathVariable Long inscripcionId) {
        return ResponseEntity.ok(resumenInscripcionMqService.enviarResumenACola(inscripcionId));
    }

    // Consume un resumen pendiente desde RabbitMQ y lo guarda en Oracle Cloud.
    @PostMapping("/resumenes/consumir-mq")
    public ResponseEntity<ResumenInscripcionMq> consumirResumenDesdeMq() {
        ResumenInscripcionMq resumenProcesado = resumenInscripcionMqService.consumirResumenDesdeCola();
        if (resumenProcesado == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resumenProcesado);
    }

    // Lista los resumenes procesados y persistidos luego del consumo desde RabbitMQ.
    @GetMapping("/resumenes-mq")
    public ResponseEntity<List<ResumenInscripcionMq>> listarResumenesMq() {
        return ResponseEntity.ok(resumenInscripcionMqService.listarResumenesProcesados());
    }

    // Genera el archivo fisico del resumen de inscripcion.
    @PostMapping("/{inscripcionId}/generar-archivo")
    public ResponseEntity<ArchivoResumenResponseDTO> generarArchivo(@PathVariable Long inscripcionId) {
        return ResponseEntity.ok(resumenArchivoService.generarArchivo(inscripcionId));
    }

    // Sube a S3 el archivo local del resumen.
    @PostMapping("/{inscripcionId}/subir-s3")
    public ResponseEntity<ArchivoResumenResponseDTO> subirArchivoAS3(@PathVariable Long inscripcionId) {
        Path archivo = resumenArchivoService.obtenerArchivoLocalOGenerar(inscripcionId);
        return ResponseEntity.ok(s3StorageService.subirArchivo(inscripcionId, archivo));
    }

    // Regenera el resumen y reemplaza el archivo guardado en S3.
    @PutMapping("/{inscripcionId}/reemplazar-s3")
    public ResponseEntity<ArchivoResumenResponseDTO> reemplazarArchivoEnS3(@PathVariable Long inscripcionId) {
        Path archivo = resumenArchivoService.regenerarArchivo(inscripcionId);
        return ResponseEntity.ok(s3StorageService.reemplazarArchivo(inscripcionId, archivo));
    }

    // Verifica si el resumen existe en el bucket configurado.
    @GetMapping("/{inscripcionId}/consultar-s3")
    public ResponseEntity<ArchivoResumenResponseDTO> consultarArchivoEnS3(@PathVariable Long inscripcionId) {
        inscripcionService.obtenerPorId(inscripcionId);
        return ResponseEntity.ok(s3StorageService.consultarArchivo(inscripcionId));
    }

    // Descarga desde S3 el archivo del resumen como texto plano.
    @GetMapping("/{inscripcionId}/descargar-s3")
    public ResponseEntity<Resource> descargarArchivoDesdeS3(@PathVariable Long inscripcionId) {
        inscripcionService.obtenerPorId(inscripcionId);

        byte[] contenido = s3StorageService.descargarArchivo(inscripcionId);
        String nombreArchivo = resumenArchivoService.obtenerNombreArchivo(inscripcionId);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(new ByteArrayResource(contenido));
    }

    // Elimina desde S3 el archivo asociado a la inscripcion.
    @DeleteMapping("/{inscripcionId}/eliminar-s3")
    public ResponseEntity<ArchivoResumenResponseDTO> eliminarArchivoDeS3(@PathVariable Long inscripcionId) {
        inscripcionService.obtenerPorId(inscripcionId);
        return ResponseEntity.ok(s3StorageService.eliminarArchivo(inscripcionId));
    }
}
