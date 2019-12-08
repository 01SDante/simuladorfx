# simuladorfx

Simulador de Planificación de Procesos hecho con JavaFX. A continuación se enumeran los pasos necesarios para importar y ejecutar el proyecto en Eclipse o NetBeans.

## Requisitos

### Tener instalado Java 8+:

- Descargar Java 8: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

- Descargar Java 11: https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

### Tener instalado Eclipse o NetBeans:

- Descargar Eclipse: https://www.eclipse.org/downloads/

- Descargar NetBeans: https://netbeans.apache.org/download/index.html

## Pasos para ejecutar

### Con java 8 

Basta con importar el proyecto a la IDE y ejecutarlo. Se creará una base de datos llamada '*simuladorfx.mv.db*' en la carpeta *HOME* del usuario.

Al driver de la base de datos lo debería detectar automáticamente, si esto no ocurre realizar los siguientes pasos:

#### Eclipse: 
```
Click derecho en el proyecto -> Build Path -> Configure Build Path -> Java Build Path -> Classpath -> Add external jars -> h2-1.4.199.jar
```

#### NetBeans: 
```
Click derecho en el proyecto -> Properties -> Libraries -> Add Jar/Folder -> h2-1.4.199.jar
```

### Con Java 9+

Descargar JavaFX 11: https://gluonhq.com/products/javafx/

#### Eclipse

Crear una nueva librería de usuario (*User Library*) en:

```
Eclipse -> Window -> Preferences -> Java -> Build Path -> User Libraries -> New
```

Nombrarla ```JavaFX 11``` e incluir los jars que se encuentran en la carpeta ```lib``` dentro de la carpeta ```javafx-sdk-11.0.2```

Agregar la librería recién creada al classpath:

```
Click derecho en el proyecto -> Build Path -> Configure Build Path... -> Java Build Path -> Classpath -> Add Library... -> User Library... -> JavaFX 11
```

Crear un nuevo *run configuration* en ```Run -> Run Configurations...  -> Java Application``` y en ```VM arguments``` agregar:

```--module-path "\path\to\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.fxml``` donde ```"\path\to\javafx-sdk-11.0.2\lib"``` es la ruta donde se encuentran las librerías Java FX descargadas anteriormente.

#### NetBeans

Crear una nueva librería global (*Global Library*) en:

```
Tools -> Libraries -> New Library
```

Nombrarla ```JavaFX 11``` e incluir los jars que se encuentran en la carpeta ```lib``` dentro de la carpeta ```javafx-sdk-11.0.2``` sin incluir el archivo ```src.zip``` ya que causará una excepción al correr el proyecto.

Agregar la librería recién creada al classpath:

```
Properties -> Libraries -> Compile -> Class-path -> + -> Add Library -> JavaFX 11
```

Ir a ```Properties -> Run``` y en ```VM arguments``` agregar: ```--module-path "\path\to\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.fxml``` donde ```"\path\to\javafx-sdk-11.0.2\lib"``` es la ruta donde se encuentran las librerías Java FX descargadas anteriormente.
