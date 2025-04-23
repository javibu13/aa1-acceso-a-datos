# Acceso a Datos - Actividad de Aprendizaje 2 (AA2)
Proyecto sobre tests y despliegue de la API desarrollada con SpringBoot en Java para la asignatura de Acceso a Datos del 2º curso de DAM

# Requisitos para la realización de la actividad
En esta Actividad de Aprendizaje se trabajará a partir de la API desarrollada como Actividad de Aprendizaje 1 de la asignatura de Acceso a Datos. Se pide una serie de requisitos a cumplir para la evaluación de la actividad.

## Requisitos obligatorios
✅ Pepara tests unitarios y de integración para todas las clases obligatorias de tu API (eran 5 clases). En el caso de los tests de integración, preparar al menos para los casos 20X, 400 y 404 para cada operación.

✅ Diseña una API Virtual de forma que existan, al menos, 3 Casos de Uso para cada operación (tanto de OK como para KO). Prepara una colección Postman con todos los casos de prueba

⬜ Prepara 3 tests en cada uno de los casos de prueba en una colección de Postman (la de la API que has implementado o la de la API Virtual)  forma que ésta se pueda lanzar utilizando el Collection Runner de Postman

⬜ Instala y pon en marcha APIMan

⬜ Publica la API de la 1ª Evaluación en APIMan (Gateway + Developer Portal) y configúralas para que sea necesario un API token para usarlas. Añade también al menos 2 políticas que afecten a su funcionamiento (limitación de uso, por ejemplo)

## Requisitos opcionales
⬜ Utiliza las herramientas Git y GitHub durante todo el desarrollo de la aplicación. Utiliza Git Flow.

⬜ Parametriza ambas colecciones Postman de forma que sea fácil cambiar el host, puerto o basePath de la API

⬜ Despliega en la nube alguna de tus APIs y publícala asi en APIMan

⬜ Instala Newman para lanzar las colecciones de tus APIs y sus tests desde la consola. Genera un informe con el resultado

⬜ Utilizando docker compose, preparar un proyecto que permita lanzar tanto el servicio como la base de datos

⬜ Utilizando docker compose, prepara un entorno de pruebas con base de datos para utilizar localmente mientras desarrollas

⬜ Securiza tu API con JWT


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

# Mock API
La API Mock se encuentra en el directorio `wiremock`. Para poder ejecutarla se debe añadir en la carpeta de `wiremock` el archivo `wiremock-standalone-4.0.0-beta.1.jar` (con el que ha sido desarrollada y probada) que se puede descargar desde la página oficial de WireMock. Una vez descargado el archivo, se debe ejecutar el siguiente comando desde la terminal en la carpeta `wiremock`:
```
java -jar wiremock-standalone-4.0.0-beta.1.jar
```
La API Mock estará disponible en el puerto 8080 y se puede acceder a ella desde Postman o desde cualquier navegador web. La API Mock tiene la misma estructura que la API real, pero no realiza ninguna operación sobre la base de datos. En su lugar, devuelve respuestas predefinidas para cada una de las peticiones realizadas.