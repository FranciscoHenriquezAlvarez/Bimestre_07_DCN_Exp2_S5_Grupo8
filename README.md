# Cloud Native App

## Descripcion general

Este proyecto corresponde a la asignatura Desarrollo Cloud Native de Duoc UC.
La aplicacion representa una plataforma educativa para la gestion de cursos, estudiantes e inscripciones, permitiendo registrar una inscripcion y generar su resumen asociado.

La documentacion de este `README.md` consolida la evolucion del proyecto como una continuidad academica desde Semana 01, pasando por Semana 02, hasta la actualizacion documental de Semana 04.

## Continuidad del desarrollo por semanas

### Semana 01

En la primera etapa se construye la base del sistema:

- gestion de cursos;
- gestion de estudiantes;
- registro de inscripciones;
- generacion del resumen de inscripcion como respuesta de la API;
- preparacion del proyecto para ejecucion y despliegue.

### Semana 02

En la segunda etapa se amplian las capacidades del flujo de inscripciones:

- se genera un archivo fisico local con el resumen de la inscripcion;
- se incorpora la gestion del resumen en AWS S3;
- se consideran operaciones para subir, consultar, descargar, reemplazar y eliminar el archivo asociado.

### Semana 04

En esta etapa se prepara el proyecto para ser expuesto mediante AWS API Gateway:

- se documentan endpoints REST claros para su publicacion en API Gateway;
- se incorpora un endpoint de salud para validacion rapida del despliegue;
- se considera autenticacion basada en JWT gestionada externamente por IDaaS;
- la validacion JWT se gestiona en API Gateway mediante JWT Authorizer;
- no se agrega `Spring Security` en esta version del backend.

## Arquitectura general

La solucion sigue una arquitectura por capas orientada a una API REST con responsabilidades separadas:

- capa `controller`: expone los endpoints HTTP;
- capa `service`: concentra la logica de negocio;
- capa `repository`: realiza la persistencia con Spring Data JPA;
- capa `model`: define las entidades del dominio;
- capa `dto`: modela solicitudes y respuestas de la API;
- capa `config`: centraliza configuraciones tecnicas, como AWS S3;
- capa `exception`: maneja errores de forma uniforme.

## Estructura por capas del proyecto

```text
src/main/java/com/duoc/cloudnativeapp
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── service
└── CloudnativeappApplication.java
```

## Endpoints principales del sistema

### Cursos

- `GET /api/cursos`
- `POST /api/cursos`

### Estudiantes

- `GET /api/estudiantes`
- `POST /api/estudiantes`

### Inscripciones

- `GET /api/inscripciones`
- `POST /api/inscripciones`

## Endpoints especificos de inscripcion y gestion de resumen

- `POST /api/inscripciones`
- `POST /api/inscripciones/{id}/generar-archivo`
- `POST /api/inscripciones/{id}/subir-s3`
- `GET /api/inscripciones/{id}/consultar-s3`
- `GET /api/inscripciones/{id}/descargar-s3`
- `PUT /api/inscripciones/{id}/reemplazar-s3`
- `DELETE /api/inscripciones/{id}/eliminar-s3`
- `GET /api/health`

### Ejemplo de creacion de inscripcion

```json
{
  "estudianteId": 1,
  "cursosIds": [1, 2]
}
```

### Ejemplo de respuesta de resumen

```json
{
  "inscripcionId": 1,
  "estudiante": "Francisco Henriquez",
  "fechaInscripcion": "2026-06-01",
  "cursos": [
    {
      "cursoId": 1,
      "nombre": "Spring Boot Basico",
      "costo": 50000
    },
    {
      "cursoId": 2,
      "nombre": "Docker Basico",
      "costo": 40000
    }
  ],
  "totalPagar": 90000
}
```

### Respuesta esperada del endpoint de salud

```json
{
  "status": "OK"
}
```

## Variables de entorno requeridas

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `AWS_S3_BUCKET_NAME`
- `RESUMENES_PATH`

### Ejemplo de configuracion

```bash
export DB_URL=jdbc:oracle:thin:@servidor:1521/servicio
export DB_USERNAME=usuario
export DB_PASSWORD=clave
export SERVER_PORT=8080
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY_ID=tu_access_key
export AWS_SECRET_ACCESS_KEY=tu_secret_key
export AWS_SESSION_TOKEN=tu_session_token
export AWS_S3_BUCKET_NAME=tu_bucket
export RESUMENES_PATH=archivos/resumenes
```

## Pruebas locales con curl o Postman

### 1. Levantar la aplicacion

```bash
./mvnw spring-boot:run
```

### 2. Verificar salud

```bash
curl http://localhost:8080/api/health
```

### 3. Crear datos base

Se recomienda crear primero cursos y estudiantes antes de registrar una inscripcion.

Ejemplos de rutas:

- `POST /api/cursos`
- `POST /api/estudiantes`

### 4. Crear una inscripcion

```bash
curl -X POST http://localhost:8080/api/inscripciones \
  -H "Content-Type: application/json" \
  -d '{"estudianteId":1,"cursosIds":[1,2]}'
```

### 5. Probar la gestion del resumen

```bash
curl -X POST http://localhost:8080/api/inscripciones/1/generar-archivo
curl -X POST http://localhost:8080/api/inscripciones/1/subir-s3
curl http://localhost:8080/api/inscripciones/1/consultar-s3
curl -OJ http://localhost:8080/api/inscripciones/1/descargar-s3
curl -X PUT http://localhost:8080/api/inscripciones/1/reemplazar-s3
curl -X DELETE http://localhost:8080/api/inscripciones/1/eliminar-s3
```

En Postman se puede seguir el mismo flujo usando los metodos y rutas indicadas.

## Configuracion esperada en AWS API Gateway

Para la continuidad de Semana 04, se documenta la preparacion del backend para ser expuesto mediante AWS API Gateway:

1. Se crea una API HTTP o REST en API Gateway.
2. Se registran las rutas del backend con su metodo HTTP correspondiente.
3. Se configura la integracion hacia la URL publica del backend desplegado.
4. Se publican al menos los endpoints de cursos, estudiantes, inscripciones, salud y gestion de resumen.

Rutas relevantes para publicar:

- `GET /api/health`
- `GET /api/cursos`
- `POST /api/cursos`
- `GET /api/estudiantes`
- `POST /api/estudiantes`
- `GET /api/inscripciones`
- `POST /api/inscripciones`
- `POST /api/inscripciones/{id}/generar-archivo`
- `POST /api/inscripciones/{id}/subir-s3`
- `GET /api/inscripciones/{id}/consultar-s3`
- `GET /api/inscripciones/{id}/descargar-s3`
- `PUT /api/inscripciones/{id}/reemplazar-s3`
- `DELETE /api/inscripciones/{id}/eliminar-s3`

## Configuracion esperada del IDaaS con Azure AD B2C

Para esta entrega se considera un escenario en que la autenticacion es administrada por un proveedor IDaaS y validada en AWS API Gateway.

De forma esperada, la configuracion contempla:

- un tenant o aplicacion en Azure AD B2C;
- emision de tokens JWT para los consumidores autorizados;
- exposicion del `issuer` y de la configuracion de firma correspondiente;
- integracion del JWT Authorizer de API Gateway con los parametros del proveedor;
- definicion de audiencias validas para los clientes que consumiran la API.

Esta documentacion no afirma que Azure AD B2C o API Gateway ya se encuentren operativos en un entorno productivo; se documenta la preparacion del backend para ese escenario.

## Explicacion de autenticacion

En esta version del proyecto, la autenticacion se entiende de la siguiente manera:

- sin token JWT, API Gateway debe responder `Unauthorized`;
- con un token JWT valido, API Gateway permite consumir el endpoint expuesto;
- Spring Boot no valida directamente el JWT en esta version;
- la validacion JWT se gestiona en API Gateway, fuera del backend.

## Manejo de errores incorporado

La API responde con una estructura uniforme para los errores, facilitando pruebas y consumo desde API Gateway.

Ejemplo de respuesta:

```json
{
  "timestamp": "2026-07-04T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Inscripcion no encontrada: 999",
  "path": "/api/inscripciones/999/generar-archivo"
}
```

Casos documentados:

- inscripcion inexistente;
- archivo inexistente;
- error al generar archivo local;
- error de operacion o conexion con S3.

## Pruebas automatizadas agregadas

Se consideran pruebas automatizadas orientadas a validar la actualizacion y sus casos principales:

- `HealthControllerIntegrationTest`
- `ResumenArchivoServiceTest`
- `S3StorageServiceTest`

## Comandos utiles

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Nota final

Este `README.md` consolida la actualizacion documental de Semana 04 como continuidad del desarrollo realizado en Semana 01 y Semana 02, dejando el proyecto preparado para continuar con las siguientes semanas de la asignatura.
