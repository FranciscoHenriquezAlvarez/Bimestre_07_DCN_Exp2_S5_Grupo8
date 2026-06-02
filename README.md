# Cloud Native App

## Objetivo del proyecto

Este proyecto corresponde a la asignatura Desarrollo Cloud Native.
La base de Semana 01 implementa una API REST para gestionar cursos, estudiantes e inscripciones.
La actualizacion de Semana 02 agrega generacion de archivos `.txt` para los resumenes de inscripcion y su almacenamiento en AWS S3, manteniendo la logica de negocio existente.

## Caso de uso

La aplicacion representa una plataforma educativa que permite registrar inscripciones de estudiantes en cursos virtuales.
Cada inscripcion genera un resumen con:

- Numero de inscripcion
- Nombre del estudiante
- Fecha de inscripcion
- Cursos inscritos
- Costo de cada curso
- Total a pagar

Ese resumen ahora puede:

- Guardarse localmente en el servidor o computador
- Subirse a AWS S3
- Consultarse en S3
- Descargarse desde S3
- Reemplazarse en S3
- Eliminarse desde S3

## Tecnologias usadas

- Java 17
- Spring Boot 4.0.6
- Spring Web
- Spring Data JPA
- Spring Validation
- AWS SDK for Java 2.x
- Oracle Database
- H2 para pruebas
- Maven Wrapper
- Docker
- Docker Compose
- GitHub Actions
- Docker Hub
- Amazon EC2

## Estructura principal

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

Archivos relevantes de Semana 02:

- `src/main/java/com/duoc/cloudnativeapp/config/S3Config.java`
- `src/main/java/com/duoc/cloudnativeapp/service/ResumenArchivoService.java`
- `src/main/java/com/duoc/cloudnativeapp/service/S3StorageService.java`
- `src/main/java/com/duoc/cloudnativeapp/dto/ArchivoResumenResponseDTO.java`
- `docker-compose.ec2.yml`
- `.github/workflows/deploy.yml`

## Endpoints base de Semana 01

- `GET /api/cursos`
- `POST /api/cursos`
- `GET /api/estudiantes`
- `POST /api/estudiantes`
- `GET /api/inscripciones`
- `POST /api/inscripciones`

Ejemplo de creacion de inscripcion:

```json
{
  "estudianteId": 1,
  "cursosIds": [1, 2]
}
```

Respuesta ejemplo:

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

## Semana 02 - AWS S3

### Objetivo de la actualizacion

La creacion del resumen de inscripcion ahora permite generar un archivo fisico `.txt` y administrarlo en AWS S3 usando una carpeta por numero de inscripcion.

Ruta local por defecto:

```text
archivos/resumenes/resumen-inscripcion-{inscripcionId}.txt
```

Estructura esperada en S3:

```text
resumenes/{inscripcionId}/resumen-inscripcion-{inscripcionId}.txt
```

### Variables de entorno necesarias

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

### Endpoints nuevos

#### 1. Generar archivo local

`POST /api/inscripciones/{inscripcionId}/generar-archivo`

Respuesta ejemplo:

```json
{
  "mensaje": "Archivo generado correctamente",
  "rutaLocal": "/ruta/del/proyecto/archivos/resumenes/resumen-inscripcion-1.txt",
  "nombreArchivo": "resumen-inscripcion-1.txt",
  "bucket": null,
  "key": null,
  "existe": null
}
```

#### 2. Subir archivo a S3

`POST /api/inscripciones/{inscripcionId}/subir-s3`

Si el archivo local no existe, el sistema lo genera antes de subirlo.

#### 3. Reemplazar archivo en S3

`PUT /api/inscripciones/{inscripcionId}/reemplazar-s3`

Regenera el archivo local y reemplaza el objeto en la misma key.
Si el archivo no existia en S3, se crea y el mensaje lo indica claramente.

#### 4. Consultar archivo en S3

`GET /api/inscripciones/{inscripcionId}/consultar-s3`

Respuesta ejemplo:

```json
{
  "mensaje": "Consulta realizada correctamente",
  "rutaLocal": null,
  "nombreArchivo": null,
  "bucket": "mi-bucket",
  "key": "resumenes/1/resumen-inscripcion-1.txt",
  "existe": true
}
```

#### 5. Descargar archivo desde S3

`GET /api/inscripciones/{inscripcionId}/descargar-s3`

- Respuesta como attachment
- `Content-Type: text/plain`
- `Content-Disposition: attachment; filename="resumen-inscripcion-{id}.txt"`

#### 6. Eliminar archivo en S3

`DELETE /api/inscripciones/{inscripcionId}/eliminar-s3`

Si el archivo no existe en S3, la API responde `404`.

### Ejemplos de pruebas en Postman

1. Crear datos base:

```http
POST /api/cursos
POST /api/estudiantes
POST /api/inscripciones
```

2. Generar archivo local:

```http
POST /api/inscripciones/1/generar-archivo
```

3. Subir el resumen a S3:

```http
POST /api/inscripciones/1/subir-s3
```

4. Verificar si el archivo existe:

```http
GET /api/inscripciones/1/consultar-s3
```

5. Descargar el archivo:

```http
GET /api/inscripciones/1/descargar-s3
```

6. Reemplazar el archivo:

```http
PUT /api/inscripciones/1/reemplazar-s3
```

7. Eliminar el archivo:

```http
DELETE /api/inscripciones/1/eliminar-s3
```

## Ejecucion local

Dar permisos al wrapper si hace falta:

```bash
chmod +x mvnw
```

Levantar la aplicacion:

```bash
./mvnw spring-boot:run
```

## Pruebas

Las pruebas usan H2 y el perfil `test`.
No ejecutan llamadas reales contra AWS.

```bash
./mvnw clean test
```

## Dockerfile multistage

Se mantiene el `Dockerfile` multistage de Semana 01:

- Etapa `build` con `eclipse-temurin:17-jdk`
- Etapa `runtime` con `eclipse-temurin:17-jre`

Construccion local:

```bash
docker build -t cloudnativeapp .
```

## Docker Compose en EC2

Para el despliegue en EC2 ahora se usa `docker compose` en lugar de `docker run` directo.
El archivo `docker-compose.ec2.yml` define el servicio `cloudnativeapp` con:

- Imagen publicada en Docker Hub
- `container_name`
- Puerto `8080`
- `restart: unless-stopped`
- Variables de entorno de base de datos y AWS
- Volumen del Oracle Wallet
- Carpeta persistente para los resumenes locales

En el servidor se usa una carpeta como:

```text
/home/ec2-user/cloudnativeapp
```

## Pipeline CI/CD

El workflow `.github/workflows/deploy.yml` mantiene el flujo principal de Semana 01:

1. `actions/checkout@v4`
2. `actions/setup-java@v4`
3. `chmod +x mvnw`
4. `./mvnw clean test`
5. `docker/login-action@v3`
6. `docker build`
7. `docker push`
8. Despliegue por SSH a EC2

Mejoras aplicadas en Semana 02:

- Login seguro dentro de EC2 con `--password-stdin`
- Publicacion de dos tags de imagen:
  - `latest`
  - `${github.sha}`
- Despliegue con `docker compose pull` y `docker compose up -d`
- Archivo `.env` generado en EC2 con variables de entorno
- Uso de carpeta de despliegue dedicada en `/home/ec2-user/cloudnativeapp`

### Mejora de seguridad aplicada

Se reemplazo esta practica:

```bash
docker login -u "usuario" -p "token"
```

Por esta alternativa mas segura:

```bash
echo "token" | docker login -u "usuario" --password-stdin
```

## Secrets de GitHub requeridos

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `EC2_HOST`
- `EC2_SSH_KEY`
- `USER_SERVER`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `AWS_S3_BUCKET_NAME`

