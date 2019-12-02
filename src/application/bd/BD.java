package application.bd;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BD {

	private static BD bd = null;

	private static final String URL_BD = "jdbc:h2:~/dev/H2-Databases/simuladorfx";
	private static Connection con = null;
	private static Statement stmt = null;

	// CONSTRUCTOR
	private BD() {
		crearConexion();
		crearTablaProceso();
	}

	public static BD getInstance() {
		if (bd == null) bd = new BD();
		return bd;
	}

	// CREAR CONEXION
	public void crearConexion() {
		try {
			con = DriverManager.getConnection(URL_BD, "sa", "");
			System.out.println("Conexión a la Base de Datos exitosa.");
		} catch (Exception e) {
			System.out.println("Fallo en la conexión a la Base de Datos.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fallo en la conexión a la Base de Datos");
			alert.setHeaderText(null);
			alert.setContentText("Las funciones 'Guardar CT' y 'Cargar CT' están desactivadas.");
			alert.showAndWait();
			e.printStackTrace();
		}
	}
	
	// CREAR TABLA 'PROCESO'
	public void crearTablaProceso() {
		String NOMBRE_TABLA = "PROCESO";
		try {
			stmt = con.createStatement();
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet tablas = dbmd.getTables(null, null, NOMBRE_TABLA.toUpperCase(), null);
			if (tablas.next())
				System.out.println("La tabla " + NOMBRE_TABLA + " ya está creada.");
			else {
				stmt.execute("CREATE TABLE " + NOMBRE_TABLA + "("
						+ " 	id int auto_increment primary key,\n"
						+ " 	nombre varchar(200),\n"
						+ " 	idProceso varchar(200),\n"
						+ " 	tamanio varchar(200),\n"
						+ " 	tArribo varchar(200),\n"
						+ " 	cpu1 varchar(200),\n"
						+ " 	es1 varchar(200),\n"
						+ " 	cpu2 varchar(200),\n"
						+ " 	es2 varchar(200),\n"
						+ " 	cpu3 varchar(200),\n"
						+ " 	prioridad varchar(200),\n"
						+ " )");
			}
		} catch (Exception e) {
			System.out.println("Error al crear la tabla 'PROCESO'");
		}
	}
	
	// SELECT
	public ResultSet select(String query) {
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("ERROR en la consulta 'SELECT': " + e.getLocalizedMessage());
			return null;
		}
		return rs;
	}

	// INSERT
	public boolean insert(String query) {
		try {
			stmt = con.createStatement();
			stmt.execute(query);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR en la acción 'INSERT': " + e.getLocalizedMessage());
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error al guardar datos");
			alert.setHeaderText(null);
			alert.setContentText("Se produjo un error al intentar guardar la carga de trabajo.");
			alert.showAndWait();
			return false;
		}
	}

}
