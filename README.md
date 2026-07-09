# Cloud Native App

## Descripcion general

Este proyecto corresponde a la asignatura Desarrollo Cloud Native de Duoc UC.
La aplicacion representa una plataforma educativa Cloud Native para la gestion de cursos, estudiantes e inscripciones, permitiendo registrar una inscripcion, generar su resumen asociado, almacenar archivos en AWS S3 y Amazon EFS, proteger el backend con Azure AD B2C y procesar mensajeria asincrona con RabbitMQ.

La documentacion de este `README.md` consolida la evolucion del proyecto como una continuidad academica hasta la implementacion tecnica de Semana 07.

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

### Semana 03

En esta etapa se consolida la persistencia operativa de los resumenes:

- se formaliza la generacion de resumenes como parte del flujo de inscripcion;
- se deja preparada la persistencia temporal sobre almacenamiento compartido;
- se alinea el manejo de archivos para trabajar con EFS y posterior envio a S3.

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

### Semana 06

En esta etapa se incorpora autorizacion por rol usando authorities directas en el JWT:

- el claim utilizado para autorizacion es `extension_consultaRole`;
- los valores validos son `PROFESOR` y `ESTUDIANTE`;
- no se usa prefijo `ROLE_`;
- `GET /api/inscripciones/{id}/descargar-s3` queda habilitado para `PROFESOR` y `ESTUDIANTE`;
- el resto de `/api/**` queda permitido solo para `PROFESOR`.

### Semana 07

En esta etapa se incorpora mensajeria asincrona con RabbitMQ:

- se agrega RabbitMQ como broker de mensajes;
- la aplicacion envia a cola el resumen de una inscripcion;
- un endpoint separado consume el mensaje desde la cola;
- el mensaje consumido se guarda en Oracle Cloud en una tabla dedicada;
- RabbitMQ se despliega junto con `cloudnativeapp` en Docker sobre EC2.

## Arquitectura general

La solucion sigue una arquitectura por capas orientada a una API REST con responsabilidades separadas:

- capa `controller`: expone los endpoints HTTP;
- capa `service`: concentra la logica de negocio;
- capa `repository`: realiza la persistencia con Spring Data JPA;
- capa `model`: define las entidades del dominio;
- capa `dto`: modela solicitudes y respuestas de la API;
- capa `config`: centraliza configuraciones tecnicas, como AWS S3, seguridad y RabbitMQ;
- capa `exception`: maneja errores de forma uniforme.

Adicionalmente, para Semana 07 se considera el siguiente flujo de seguridad y consumo:

1. Postman o un cliente REST obtiene un token JWT emitido por Azure AD B2C.
2. El cliente invoca AWS API Gateway usando `Authorization: Bearer <token>`.
3. API Gateway valida el JWT mediante su `authorizer`.
4. Spring Boot recibe la solicitud y vuelve a validar el JWT mediante `Spring Security` y `OAuth2 Resource Server`.
5. Segun el endpoint, la aplicacion opera con Oracle Cloud, AWS S3 y Amazon EFS.
6. Para Semana 07, el backend puede enviar resumenes a RabbitMQ y luego consumirlos para persistirlos en Oracle Cloud.

## Tecnologias principales

- `Spring Boot`
- `Spring Security`
- `Spring Data JPA`
- `Oracle Cloud`
- `AWS API Gateway`
- `Azure AD B2C`
- `AWS S3`
- `Amazon EFS`
- `RabbitMQ`
- `Docker`
- `GitHub Actions`

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

- `GET /api/health`

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

## Endpoints Semana 07 RabbitMQ

- `POST /api/inscripciones/{inscripcionId}/enviar-mq`
- `POST /api/inscripciones/resumenes/consumir-mq`
- `GET /api/inscripciones/resumenes-mq`

Estos endpoints quedan protegidos por la regla general de `/api/**` y requieren authority `PROFESOR`.

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
- `JWT_JWK_SET_URI`
- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `AWS_S3_BUCKET_NAME`
- `RESUMENES_PATH`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`

### Ejemplo de configuracion

```bash
export DB_URL=jdbc:oracle:thin:@servidor:1521/servicio
export DB_USERNAME=usuario
export DB_PASSWORD=clave
export SERVER_PORT=8080
export JWT_ISSUER_URI=https://<tenant>.b2clogin.com/tfp/<tenant-id>/<policy>/v2.0/
export JWT_AUDIENCE=<client-id>
export JWT_JWK_SET_URI=https://<tenant>.b2clogin.com/<tenant-domain>/<policy>/discovery/v2.0/keys
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY_ID=tu_access_key
export AWS_SECRET_ACCESS_KEY=tu_secret_key
export AWS_SESSION_TOKEN=tu_session_token
export AWS_S3_BUCKET_NAME=tu_bucket
export RESUMENES_PATH=archivos/resumenes
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USERNAME=guest
export RABBITMQ_PASSWORD=guest
```

## Variables JWT

La configuracion actual del `OAuth2 Resource Server` utiliza:

- `JWT_ISSUER_URI`: valida el `issuer` del token emitido por Azure AD B2C;
- `JWT_AUDIENCE`: valida la audiencia esperada por el backend;
- `JWT_JWK_SET_URI`: permite que Spring Boot obtenga explicitamente las claves publicas desde Azure B2C cuando la resolucion automatica desde `issuer-uri` no es suficiente.

## Variables RabbitMQ

Ejemplo para EC2 con Docker Compose:

```bash
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

Ejemplo para local:

```bash
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

## Pruebas locales con curl o Postman

### 1. Levantar la aplicacion

```bash
./mvnw spring-boot:run
```

### 2. Verificar salud del backend

```bash
curl http://localhost:8080/api/health
```

### 3. Obtener un token JWT valido desde Azure AD B2C

Para probar los endpoints protegidos desde Semana 05 en adelante, se debe invocar la API usando `Authorization: Bearer <token>`.

### 4. Verificar el endpoint `/api/health`

Sin token, la respuesta esperada es `200 OK`.

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
- `JWT_JWK_SET_URI`: endpoint explicito para obtener las claves publicas de Azure AD B2C.

El workflow de despliegue debe inyectar estos secretos en el archivo `.env` utilizado por `docker-compose.ec2.yml`, de modo que el contenedor arranque con la configuracion de `Spring Security` y `OAuth2 Resource Server`.

Actualmente el despliegue en EC2 levanta dos contenedores:

- `cloudnativeapp`
- `rabbitmq`

Comando de validacion sugerido:

```bash
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}"
```

## Configuracion esperada en AWS API Gateway

Para la continuidad de las semanas de seguridad, se documenta la preparacion del backend para ser expuesto mediante AWS API Gateway:

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
- configuracion de `JWT_ISSUER_URI`, `JWT_AUDIENCE` y `JWT_JWK_SET_URI` para el `OAuth2 Resource Server` del backend.

Esta documentacion no afirma que Azure AD B2C o API Gateway ya se encuentren operativos en un entorno productivo; se documenta la preparacion del backend para ese escenario.

## Explicacion de autenticacion y seguridad

El comportamiento actual de autenticacion y autorizacion se entiende de la siguiente manera:

- sin token JWT, API Gateway debe responder `Unauthorized`;
- con un token JWT valido, API Gateway permite consumir el endpoint expuesto;
- Spring Boot valida nuevamente el JWT usando `Spring Security` como `OAuth2 Resource Server`;
- `GET /api/health` es publico y responde sin token;
- el resto de `/api/**` requiere un JWT valido;
- `GET /api/inscripciones/{id}/descargar-s3` permite `PROFESOR` y `ESTUDIANTE`;
- el resto de `/api/**` permite solo `PROFESOR`;
- la autorizacion se basa en `hasAuthority` y `hasAnyAuthority`, sin usar `hasRole` ni prefijo `ROLE_`;
- el claim utilizado para autorizacion es `extension_consultaRole`;
- los valores validos del claim son `PROFESOR` y `ESTUDIANTE`.

## Semana 07 - RabbitMQ y mensajeria asincrona

En la implementacion actual se incorporo RabbitMQ como broker de mensajeria para desacoplar parte del procesamiento del resumen de inscripcion.

El flujo esperado es:

1. El backend construye un resumen real de la inscripcion.
2. El endpoint `POST /api/inscripciones/{inscripcionId}/enviar-mq` envia ese resumen a la cola.
3. El endpoint `POST /api/inscripciones/resumenes/consumir-mq` consume el mensaje desde RabbitMQ.
4. El mensaje consumido se guarda en Oracle Cloud en la tabla `RESUMEN_INSCRIPCION_MQ`.
5. El endpoint `GET /api/inscripciones/resumenes-mq` permite verificar los registros persistidos.

Configuracion documentada:

- Queue: `resumen-inscripcion-queue`
- Exchange: `resumen-inscripcion-exchange`
- Routing key: `resumen.inscripcion`
- Contenedor: `rabbitmq`
- Imagen: `rabbitmq:3-management`
- Puerto AMQP: `5672`
- Puerto RabbitMQ Management: `15672`

RabbitMQ se levanta como contenedor adicional en `docker-compose.ec2.yml` junto con `cloudnativeapp`.

## Tunnel SSH para RabbitMQ Management

Para revisar RabbitMQ Management en EC2 sin exponer publicamente el puerto `15672`, se puede utilizar un tunel SSH desde Windows PowerShell:

```powershell
ssh -o ServerAliveInterval=30 -o ServerAliveCountMax=5 -i "$env:USERPROFILE\Downloads\llave_pipeline.pem" -N -L 15673:localhost:15672 ec2-user@<EC2_HOST>
```

Luego abrir en el navegador:

```text
http://localhost:15673
```

Esto evita abrir publicamente el puerto `15672` en AWS.

Credenciales referenciales:

- `guest`
- `guest`

## Oracle Cloud, S3 y EFS

La version actual mantiene:

- persistencia principal en Oracle Cloud;
- almacenamiento temporal local sobre volumen respaldado por Amazon EFS;
- operaciones de publicacion, consulta, descarga, reemplazo y eliminacion de archivos sobre AWS S3.

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
- `InscripcionMqControllerTest`
- `SecurityIntegrationTest`
- `ResumenArchivoServiceTest`
- `S3StorageServiceTest`

## Pruebas funcionales

El comportamiento del proyecto, se comprueba con los siguientes escenarios:

Sin token:

- `GET /api/cursos` -> `401 Unauthorized`

Con token `PROFESOR`:

- `GET /api/cursos` -> `200 OK`
- `POST /api/inscripciones/{id}/enviar-mq` -> `200 OK`
- `POST /api/inscripciones/resumenes/consumir-mq` -> `200 OK`
- `GET /api/inscripciones/resumenes-mq` -> `200 OK`

Con token `ESTUDIANTE`:

- `GET /api/cursos` -> `403 Forbidden`
- `POST /api/inscripciones/{id}/enviar-mq` -> `403 Forbidden`
- `GET /api/inscripciones/{id}/descargar-s3` -> permitido si existe archivo

RabbitMQ Management:

- despues de enviar mensaje: `Ready = 1`
- despues de consumir mensaje: `Ready = 0`

## Comandos utiles

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Nota final

Este `README.md` deja documentado el estado tecnico actual del proyecto hasta Semana 07, manteniendo la base academica de gestion de cursos e inscripciones y sumando seguridad JWT con roles, almacenamiento en Oracle/S3/EFS y mensajeria asincrona con RabbitMQ.
