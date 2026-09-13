# TV Maze Middleware API

API Middleware desarrollada con **Spring Boot 3.3.3**, **Java 17** y **MongoDB Atlas** para la gestión y consulta de shows de televisión utilizando la API externa de TV Maze.

## 🚀 Requisitos e Instalación

1. Clonar el repositorio.
2. Configurar las credenciales de MongoDB Atlas en el archivo `src/main/resources/application.properties`.
3. Asegurar que la configuración de red de tu clúster de MongoDB Atlas permita la IP `0.0.0.0/0`.
4. Compilar y ejecutar el proyecto con Maven:
   ```bash
   mvn clean spring-boot:run
   ```

## 🛠️ Endpoints Disponibles

- **GET** `/api/shows/search?search_query={criterio}`: Busca shows en la API externa y concatena sus comentarios guardados.
- **GET** `/api/shows/{show_id}`: Consulta un show específico implementando una caché en MongoDB Atlas y agrega sus comentarios.
- **POST** `/api/shows/comment`: Registra un comentario y calificación (rango 0-5) para un show.
