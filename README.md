# Acceso a Datos - Actividad de Aprendizaje 1 (AA1)
API desarrollada con SpringBoot en Java para la asignatura de Acceso a Datos del 2º curso de DAM


# Requisitos para la realización de la actividad
Se pide una serie de requisitos a cumplir para la evaluación de la actividad.

## Requisitos obligatorios
⬜ Crear una API REST con SpringBoot en Java.

⬜ Diseña la API y escribe el fichero OpenAPI 3.0 de la API. Incluye, al menos, 
los casos de éxito (20X), 400, 404 y los 500.

⬜ El modelo de datos estará compuesto de, al menos, 5 clases y tendrán que existir 
relaciones entre ellas. Cada clase tendrá, al menos, 6 atributos (String, int, float, 
boolean y algún tipo para almacenar fechas). Cada clase tendrá, al menos, 2 
atributos obligatorios y algún otro con algún tipo de restricción de 
formato/validación. 

⬜ Se tendrá que poder realizar, al menos, las operaciones CRUD sobre cada una de 
las clases. Se controlarán, al menos, los errores 400, 404 y 500 

⬜ Añade opciones de filtrado para al menos una operación en cada clase en donde se 
puedan indicar hasta 3 campos diferentes (solo aplicable para operaciones GET).

⬜ Prepara una colección Postman que permita probar todas las operaciones 
desarrolladas.

## Requisitos opcionales
⬜ Añade una operación PATCH para cada una de las clases del modelo.

⬜ Utiliza la herramienta Git (y GitHub) durante todo el desarrollo de la API. Escribe 
el fichero README.md para explicar cómo poner en marcha el proyecto. Utiliza el 
gestor de Issues para los problemas/fallos que vayan surgiendo.

⬜ Securiza algunas de tus operaciones de la API con un token JWT.

⬜ Añade 3 operaciones que utilicen consultas SQL nativas para extraer la información 
de la base de datos.

⬜ Añade al fichero de especificación de la API (OpenAPI 3.0) un par de ejemplos 
para cada operación.

⬜ Añade un log a la API que registre las trazas de todas las operaciones y errores que 
se produzcan.


# Descripción
API REST que permite realizar operaciones CRUD sobre una base de datos MySQL. La base de datos contiene las tablas necesarias para gestionar una serie de entidades relacionadas entre sí. En este proyecto se ha decidido trabajar con las siguientes entidades:
- Conferencia (Conference): Representa un evento que se celebra en un lugar, en una fecha concreta, con unos asistentes determinados, organizado por una persona y en el que se van a desempeñar actividades.
- Lugar (Place): Representa un lugar en el que se puede celebrar un evento.
- Persona (Person): Representa una persona que puede organizar un evento o asistir a él.
- Actividad (Activity): Representa una actividad que se va a desempeñar en un evento.
- Asistencia (Attendance): Representa la asistencia de una persona a un evento.


