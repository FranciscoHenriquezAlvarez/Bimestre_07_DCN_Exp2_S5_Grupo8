# Cloud Native App

## Objetivo del proyecto

Este proyecto corresponde a la asignatura Desarrollo Cloud Native, Semana 1.
La aplicacion implementa una API REST simple para gestionar cursos, estudiantes e inscripciones en una plataforma educativa.

## Tecnologias usadas

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Oracle Database
- H2 para pruebas
- Maven Wrapper
- Docker
- GitHub Actions
- Docker Hub
- Amazon EC2

## Estructura del proyecto

```text
src/main/java/com/duoc/cloudnativeapp
├── controller
├── dto
├── exception
├── model
├── repository
├── service
└── CloudnativeappApplication.java
```

## Entidades principales

- `Curso`: guarda nombre, instructor, duracion y costo.
- `Estudiante`: guarda nombre y correo.
- `Inscripcion`: relaciona a un estudiante con una fecha y un total.
- `DetalleInscripcion`: guarda cada curso seleccionado en la inscripcion.

## Endpoints disponibles

### 1. Listar cursos

`GET /api/cursos`

Respuesta ejemplo:

```json
[
  {
    "id": 1,
    "nombre": "Spring Boot Basico",
    "instructor": "Carlos Valverde",
    "duracionHoras": 20,
    "costo": 50000
  }
]
```

### 2. Crear curso

`POST /api/cursos`

Request:

```json
{
  "nombre": "Spring Boot Basico",
  "instructor": "Carlos Valverde",
  "duracionHoras": 20,
  "costo": 50000
}
```

### 3. Crear estudiante

`POST /api/estudiantes`

Request:

```json
{
  "nombre": "Francisco Henriquez",
  "correo": "francisco@email.com"
}
```

### 4. Listar estudiantes

`GET /api/estudiantes`

### 5. Crear inscripcion

`POST /api/inscripciones`

Request:

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
  "fechaInscripcion": "2026-05-25",
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

### 6. Listar inscripciones

`GET /api/inscripciones`

## Variables de entorno necesarias

La aplicacion usa variables de entorno para conectarse a Oracle Cloud:

```bash
export DB_URL=jdbc:oracle:thin:@servidor:1521/servicio
export DB_USERNAME=usuario
export DB_PASSWORD=clave
export SERVER_PORT=8080
```

## Ejecucion local

1. Dar permisos al wrapper si hace falta:

```bash
chmod +x mvnw
```

2. Ejecutar la aplicacion:

```bash
./mvnw spring-boot:run
```

## Ejecucion de pruebas

Las pruebas usan H2 y el perfil `test`.

```bash
./mvnw clean test
```

## Construccion de imagen Docker

```bash
docker build -t cloudnativeapp .
docker run -p 8080:8080 \
  -e DB_URL="$DB_URL" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  -e SERVER_PORT=8080 \
  cloudnativeapp
```

## Pipeline de GitHub Actions

El workflow `.github/workflows/deploy.yml` realiza lo siguiente cuando se hace push a `main`:

1. Descarga el codigo.
2. Configura Java 17.
3. Ejecuta las pruebas con Maven.
4. Construye la imagen Docker.
5. Publica la imagen en Docker Hub.
6. Se conecta por SSH a EC2.
7. Descarga la nueva imagen.
8. Reemplaza el contenedor anterior.
9. Levanta la aplicacion con variables de entorno.

## Secrets en GitHub

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `EC2_HOST`
- `EC2_SSH_KEY`
- `USER_SERVER`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT` con valor `8080`

## Pasos simples para desplegar en EC2

1. Instalar Docker en la instancia.
2. Abrir el puerto 8080 en el security group.
3. Crear los secrets en GitHub.
4. Hacer push a la rama `main`.
5. Verificar el workflow y luego probar la API en EC2.