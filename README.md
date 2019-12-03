# simuladorfx

- Descargar Java 11: https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

- Descargar JavaFX 11: https://gluonhq.com/products/javafx/

- Descargar eclipse: https://www.eclipse.org/downloads/

- Clonar el repositorio e importarlo a eclipse

- Agregar el .jar para la base de datos H2: click derecho en el proyecto --> Build Path --> Configure Build Path --> Java Build Path --> Classpath --> Add external jars --> h2-1.4.199.jar

- Agregar las librerias de JavaFX 11: https://openjfx.io/openjfx-docs/#IDE-Eclipse

#

Con esto debería correr pero no se va a conectar a la base de datos. Para establecer la conexión:

- Descargar H2 Database: https://www.h2database.com/html/main.html

- Crear una base de datos llamada 'simuladorfx'. 

- En la clase BD del paquete app **(simuladorfx/src/app/bd/BD.java)** está la variable ***URL_DB*** con la ubicación de la base de datos, por defecto tiene el siguiente valor **"jdbc:h2:~/dev/H2-Databases/simuladorfx"** que es donde yo tengo mi base de datos. Cámbienla por la de ustedes.
