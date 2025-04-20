# Acceso a Datos - Actividad de Aprendizaje 2 (AA2)
Proyecto sobre tests y despliegue de la API desarrollada con SpringBoot en Java para la asignatura de Acceso a Datos del 2º curso de DAM

# Requisitos para la realización de la actividad
En esta Actividad de Aprendizaje se trabajará a partir de la API desarrollada como Actividad de Aprendizaje 1 de la asignatura de Acceso a Datos. Se pide una serie de requisitos a cumplir para la evaluación de la actividad.

## Requisitos obligatorios
⬜ Pepara tests unitarios y de integración para todas las clases obligatorias de tu API (eran 5 clases). En el caso de los tests de integración, preparar al menos para los casos 20X, 400 y 404 para cada operación.

⬜ Diseña una API Virtual de forma que existan, al menos, 3 Casos de Uso para cada operación (tanto de OK como para KO). Prepara una colección Postman con todos los casos de prueba

⬜ Prepara 3 tests en cada uno de los casos de prueba en una colección de Postman (la de la API que has implementado o la de la API Virtual)  forma que ésta se pueda lanzar utilizando el Collection Runner de Postman

⬜ Instala y pon en marcha APIMan

⬜ Publica la API de la 1ª Evaluación en APIMan (Gateway + Developer Portal) y configúralas para que sea necesario un API token para usarlas. Añade también al menos 2 políticas que afecten a su funcionamiento (limitación de uso, por ejemplo)

## Requisitos opcionales
✅ Añade una operación PATCH para cada una de las clases del modelo.

✅ Utiliza la herramienta Git (y GitHub) durante todo el desarrollo de la API. Escribe 
el fichero README.md para explicar cómo poner en marcha el proyecto. Utiliza el 
gestor de Issues para los problemas/fallos que vayan surgiendo.

⬜ Securiza algunas de tus operaciones de la API con un token JWT.

✅ Añade 3 operaciones que utilicen consultas SQL nativas para extraer la información de la base de datos.

✅ Añade 3 operaciones que utilicen consultas JPQL para extraer la información de la base de datos

✅ Añade al fichero de especificación de la API (OpenAPI 3.0) un par de ejemplos 
para cada operación.

✅ Añade un log a la API que registre las trazas de todas las operaciones y errores que 
se produzcan.


# Descripción
API REST que permite realizar operaciones CRUD sobre una base de datos MySQL. La base de datos contiene las tablas necesarias para gestionar una serie de entidades relacionadas entre sí. En este proyecto se ha decidido trabajar con las siguientes entidades:
- Conferencia (Conference): Representa un evento que se celebra en un lugar, en una fecha concreta, con unos asistentes determinados, organizado por una persona y en el que se van a desempeñar actividades.
- Lugar (Place): Representa un lugar en el que se puede celebrar un evento.
- Persona (Person): Representa una persona que puede organizar un evento o asistir a él.
- Actividad (Activity): Representa una actividad que se va a desempeñar en un evento.
- Asistencia (Attendance): Representa la asistencia de una persona a un evento.


# Despliegue
Desde la terminal se debe ubicar en la carpeta devops del proyecto y ejecutar el siguiente comando:
```
docker compose up
```

Esto levantará el contenedor de la base de datos y el contenedor de la API. La API estará disponible en el puerto 8080 y la base de datos en el puerto 3306.

Las credenciales configuradas son de ejemplo y no son seguras. Se recomienda cambiarlas en un entorno de producción. Para ello se debe modificar el archivo `docker-compose.yml` y cambiar las variables de entorno `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER` y `MYSQL_PASSWORD` por las credenciales deseadas y hacer coincidir la nueva configuración en el archivo `application.properties` de la API.

# Postman
La colección de Postman se encuentra exportada en el fichero con nombre `API_Eventos.postman_collection.json`. Para importar la colección en Postman, se debe abrir Postman y hacer clic en el botón "Importar" en la parte superior izquierda. Luego, seleccionar el archivo `API_Eventos.postman_collection.json` y hacer clic en "Importar". Esto importará la colección de Postman con todas las peticiones necesarias para probar la API.