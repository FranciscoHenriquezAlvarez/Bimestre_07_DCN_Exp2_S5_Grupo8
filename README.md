# Cloud Native App

## Descripcion general

Este proyecto corresponde a la asignatura Desarrollo Cloud Native de Duoc UC.
La aplicacion representa una plataforma educativa para la gestion de cursos, estudiantes e inscripciones, permitiendo registrar una inscripcion y generar su resumen asociado.

La documentacion de este `README.md` consolida la evolucion del proyecto como una continuidad academica desde Semana 01 y Semana 02, considerando la base funcional de Semana 04 y su actual alineacion a Semana 05.

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
- se mantiene la integracion del backend con Oracle Cloud y las operaciones de resumen en AWS S3.

### Semana 05

En esta etapa se mantiene la arquitectura de Semana 04 y se agrega una segunda capa de validacion en el backend:

- se conserva AWS API Gateway como API Manager;
- se conserva el uso de IDaaS con Azure AD B2C para emision de tokens JWT;
- se incorpora `Spring Security` en el backend;
- la aplicacion Spring Boot funciona como `OAuth2 Resource Server`;
- el backend valida nuevamente el JWT usando `issuer` y `audience`;
- los endpoints funcionales de cursos, estudiantes, inscripciones y gestion de resumen se mantienen.

## Arquitectura general

La solucion sigue una arquitectura por capas orientada a una API REST con responsabilidades separadas:

- capa `controller`: expone los endpoints HTTP;
- capa `service`: concentra la logica de negocio;
- capa `repository`: realiza la persistencia con Spring Data JPA;
- capa `model`: define las entidades del dominio;
- capa `dto`: modela solicitudes y respuestas de la API;
- capa `config`: centraliza configuraciones tecnicas, como AWS S3;
- capa `exception`: maneja errores de forma uniforme.

Adicionalmente, para Semana 05 se considera el siguiente flujo de seguridad y consumo:

1. Postman o un cliente REST obtiene un token JWT emitido por Azure AD B2C.
2. El cliente invoca AWS API Gateway usando `Authorization: Bearer <token>`.
3. API Gateway valida el JWT mediante su `authorizer`.
4. Spring Boot recibe la solicitud y vuelve a validar el JWT mediante `Spring Security` y `OAuth2 Resource Server`.
5. Una vez validado el acceso, la aplicacion interactua con Oracle Cloud y AWS S3 segun la operacion solicitada.

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

- `GET /api/health`
- `POST /api/inscripciones`
- `POST /api/inscripciones/{id}/generar-archivo`
- `POST /api/inscripciones/{id}/subir-s3`
- `GET /api/inscripciones/{id}/consultar-s3`
- `GET /api/inscripciones/{id}/descargar-s3`
- `PUT /api/inscripciones/{id}/reemplazar-s3`
- `DELETE /api/inscripciones/{id}/eliminar-s3`

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
- `JWT_ISSUER_URI`
- `JWT_AUDIENCE`
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
export JWT_ISSUER_URI=https://tu-tenant.b2clogin.com/tu-tenant.onmicrosoft.com/v2.0/
export JWT_AUDIENCE=tu_application_client_id
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

### 2. Verificar salud tecnica del contenedor o instancia

```bash
curl http://localhost:8080/actuator/health
```

### 3. Obtener un token JWT valido desde Azure AD B2C

Para probar los endpoints protegidos en Semana 05, se debe invocar la API usando `Authorization: Bearer <token>`.

### 4. Verificar el endpoint `/api/health`

Sin token, la respuesta esperada es `401 Unauthorized`.

Con token valido, el flujo esperado es:

```bash
curl http://localhost:8080/api/health \
  -H "Authorization: Bearer <token>"
```

### 5. Crear datos base

Se recomienda crear primero cursos y estudiantes antes de registrar una inscripcion.

Ejemplos de rutas:

- `POST /api/cursos` con `Authorization: Bearer <token>`
- `POST /api/estudiantes` con `Authorization: Bearer <token>`

### 6. Crear una inscripcion

```bash
curl -X POST http://localhost:8080/api/inscripciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"estudianteId":1,"cursosIds":[1,2]}'
```

### 7. Probar la gestion del resumen

```bash
curl -X POST http://localhost:8080/api/inscripciones/1/generar-archivo -H "Authorization: Bearer <token>"
curl -X POST http://localhost:8080/api/inscripciones/1/subir-s3 -H "Authorization: Bearer <token>"
curl http://localhost:8080/api/inscripciones/1/consultar-s3 -H "Authorization: Bearer <token>"
curl -OJ http://localhost:8080/api/inscripciones/1/descargar-s3 -H "Authorization: Bearer <token>"
curl -X PUT http://localhost:8080/api/inscripciones/1/reemplazar-s3 -H "Authorization: Bearer <token>"
curl -X DELETE http://localhost:8080/api/inscripciones/1/eliminar-s3 -H "Authorization: Bearer <token>"
```

En Postman se puede seguir el mismo flujo usando los metodos y rutas indicadas.

## Despliegue automatizado en EC2

El proyecto considera despliegue automatizado mediante GitHub Actions hacia una instancia EC2 utilizando Docker.

Para este flujo se mantienen los secretos ya utilizados por Oracle Cloud, AWS S3, Docker Hub y EC2, agregando ademas los parametros de seguridad JWT del backend:

- `JWT_ISSUER_URI`: valor exacto del claim `iss` emitido por Azure AD B2C;
- `JWT_AUDIENCE`: valor exacto del claim `aud` esperado por el backend Spring Boot.

El workflow de despliegue debe inyectar estos secretos en el archivo `.env` utilizado por `docker-compose.ec2.yml`, de modo que el contenedor arranque con la configuracion de `Spring Security` y `OAuth2 Resource Server`.

## Configuracion esperada en AWS API Gateway

Para la continuidad de Semana 04 y Semana 05, se documenta la preparacion del backend para ser expuesto mediante AWS API Gateway:

1. Se crea una API HTTP o REST en API Gateway.
2. Se registran las rutas del backend con su metodo HTTP correspondiente.
3. Se configura la integracion hacia la URL publica del backend desplegado.
4. Se configura el `JWT Authorizer` usando los parametros del proveedor IDaaS.
5. Se publican al menos los endpoints de cursos, estudiantes, inscripciones, salud y gestion de resumen.

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

Para esta entrega se considera un escenario en que la autenticacion es administrada por un proveedor IDaaS y validada tanto en AWS API Gateway como en el backend Spring Boot.

De forma esperada, la configuracion contempla:

- un tenant o aplicacion en Azure AD B2C;
- emision de tokens JWT para los consumidores autorizados;
- exposicion del `issuer` y de la configuracion de firma correspondiente;
- integracion del JWT Authorizer de API Gateway con los parametros del proveedor;
- definicion de audiencias validas para los clientes que consumiran la API;
- configuracion de `JWT_ISSUER_URI` y `JWT_AUDIENCE` para el `OAuth2 Resource Server` del backend.

Esta documentacion no afirma que Azure AD B2C o API Gateway ya se encuentren operativos en un entorno productivo; se documenta la preparacion del backend para ese escenario.

## Explicacion de autenticacion y seguridad

En Semana 05, la autenticacion y autorizacion esperada se entienden de la siguiente manera:

- sin token JWT, API Gateway debe responder `Unauthorized`;
- con un token JWT valido, API Gateway permite consumir el endpoint expuesto;
- Spring Boot valida nuevamente el JWT usando `Spring Security` como `OAuth2 Resource Server`;
- los endpoints `/api/**` requieren autenticacion;
- `GET /api/health` requiere token valido;
- `GET /actuator/health` puede permanecer habilitado sin autenticacion para chequeos tecnicos, segun la configuracion de seguridad del proyecto.

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
- `SecurityIntegrationTest`
- `ResumenArchivoServiceTest`
- `S3StorageServiceTest`

## Pruebas funcionales esperadas para Semana 05

Para validar el comportamiento de seguridad en esta etapa, se espera comprobar al menos los siguientes escenarios:

- llamada directa a EC2 sin token sobre `GET /api/health` debe responder `401 Unauthorized`;
- llamada directa a EC2 con `Bearer Token` valido sobre `GET /api/health` debe responder `200 OK`;
- llamada via API Gateway sin token debe responder `401 Unauthorized`;
- llamada via API Gateway con `Bearer Token` valido debe responder `200 OK`;
- endpoints funcionales como cursos, estudiantes, inscripciones y operaciones S3 deben consumirse usando `Authorization: Bearer <token>`.

## Comandos utiles

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Nota final

Este `README.md` consolida la continuidad academica del proyecto desde Semana 01 y Semana 02, pasando por la exposicion mediante API Gateway en Semana 04 y agregando en Semana 05 una segunda capa de seguridad con `Spring Security` en el backend.
