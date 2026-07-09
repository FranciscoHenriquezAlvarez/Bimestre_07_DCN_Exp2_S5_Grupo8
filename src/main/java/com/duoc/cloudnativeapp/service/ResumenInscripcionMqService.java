package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.InscripcionResumenDTO;
import com.duoc.cloudnativeapp.dto.ResumenInscripcionMensaje;
import com.duoc.cloudnativeapp.model.ResumenInscripcionMq;
import com.duoc.cloudnativeapp.repository.ResumenInscripcionMqRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResumenInscripcionMqService {

    private final InscripcionService inscripcionService;
    private final RabbitTemplate rabbitTemplate;
    private final ResumenInscripcionMqRepository resumenInscripcionMqRepository;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public ResumenInscripcionMqService(InscripcionService inscripcionService,
                                       RabbitTemplate rabbitTemplate,
                                       ResumenInscripcionMqRepository resumenInscripcionMqRepository) {
        this.inscripcionService = inscripcionService;
        this.rabbitTemplate = rabbitTemplate;
        this.resumenInscripcionMqRepository = resumenInscripcionMqRepository;
    }

    public ResumenInscripcionMensaje enviarResumenACola(Long inscripcionId) {
        InscripcionResumenDTO resumen = inscripcionService.obtenerPorId(inscripcionId);
        ResumenInscripcionMensaje mensaje = construirMensaje(resumen);

        rabbitTemplate.convertAndSend(exchangeName, routingKey, mensaje);
        return mensaje;
    }

    public ResumenInscripcionMq consumirResumenDesdeCola() {
        Object payload = rabbitTemplate.receiveAndConvert(queueName);
        if (payload == null) {
            return null;
        }

        ResumenInscripcionMensaje mensaje = convertirMensaje(payload);

        ResumenInscripcionMq resumenGuardado = new ResumenInscripcionMq();
        resumenGuardado.setInscripcionId(mensaje.getInscripcionId());
        resumenGuardado.setEstudiante(mensaje.getEstudiante());
        resumenGuardado.setCurso(mensaje.getCurso());
        resumenGuardado.setFechaInscripcion(mensaje.getFechaInscripcion());
        resumenGuardado.setTotalPagar(mensaje.getTotalPagar());
        resumenGuardado.setContenidoResumen(mensaje.getContenidoResumen());
        resumenGuardado.setFechaProcesamiento(LocalDateTime.now());

        return resumenInscripcionMqRepository.save(resumenGuardado);
    }

    public List<ResumenInscripcionMq> listarResumenesProcesados() {
        return resumenInscripcionMqRepository.findAllByOrderByIdAsc();
    }

    private ResumenInscripcionMensaje construirMensaje(InscripcionResumenDTO resumen) {
        String cursos = resumen.getCursos()
                .stream()
                .map(detalle -> detalle.getNombre())
                .collect(Collectors.joining(", "));

        String contenidoResumen = """
                Resumen de inscripcion
                Inscripcion: %d
                Estudiante: %s
                Fecha: %s
                Cursos: %s
                Total a pagar: %.2f
                """.formatted(
                resumen.getInscripcionId(),
                resumen.getEstudiante(),
                resumen.getFechaInscripcion(),
                cursos,
                resumen.getTotalPagar().doubleValue()
        );

        return new ResumenInscripcionMensaje(
                resumen.getInscripcionId(),
                resumen.getEstudiante(),
                cursos,
                resumen.getFechaInscripcion().toString(),
                resumen.getTotalPagar().doubleValue(),
                contenidoResumen
        );
    }

    private ResumenInscripcionMensaje convertirMensaje(Object payload) {
        if (payload instanceof ResumenInscripcionMensaje mensaje) {
            return mensaje;
        }

        if (payload instanceof Map<?, ?> map) {
            ResumenInscripcionMensaje mensaje = new ResumenInscripcionMensaje();
            Object inscripcionId = map.get("inscripcionId");
            if (inscripcionId instanceof Number number) {
                mensaje.setInscripcionId(number.longValue());
            }
            mensaje.setEstudiante((String) map.get("estudiante"));
            mensaje.setCurso((String) map.get("curso"));
            mensaje.setFechaInscripcion((String) map.get("fechaInscripcion"));

            Object totalPagar = map.get("totalPagar");
            if (totalPagar instanceof Number number) {
                mensaje.setTotalPagar(number.doubleValue());
            }

            mensaje.setContenidoResumen((String) map.get("contenidoResumen"));
            return mensaje;
        }

        throw new IllegalStateException("No fue posible convertir el mensaje recibido desde RabbitMQ");
    }
}
