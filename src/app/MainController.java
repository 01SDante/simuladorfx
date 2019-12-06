package app;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import app.algoritmos.colas_multinivel.ColasMultinivelFijasBestFit;
import app.algoritmos.colas_multinivel.ColasMultinivelFijasFirstFit;
import app.algoritmos.colas_multinivel.ColasMultinivelVariablesFirstFit;
import app.algoritmos.colas_multinivel.ColasMultinivelVariablesWorstFit;
import app.algoritmos.fcfs.FCFSFijasBestFit;
import app.algoritmos.fcfs.FCFSFijasFirstFit;
import app.algoritmos.fcfs.FCFSVariablesFirstFit;
import app.algoritmos.fcfs.FCFSVariablesWorstFit;
import app.algoritmos.fcfs._1es.FCFSFijasBestFit1ES;
import app.algoritmos.fcfs._1es.FCFSFijasFirstFit1ES;
import app.algoritmos.fcfs._1es.FCFSVariablesFirstFit1ES;
import app.algoritmos.fcfs._1es.FCFSVariablesWorstFit1ES;
import app.algoritmos.fcfs._2es.FCFSFijasBestFit2ES;
import app.algoritmos.fcfs._2es.FCFSFijasFirstFit2ES;
import app.algoritmos.fcfs._2es.FCFSVariablesFirstFit2ES;
import app.algoritmos.fcfs._2es.FCFSVariablesWorstFit2ES;
import app.algoritmos.rr.RRFijasBestFit;
import app.algoritmos.rr.RRFijasFirstFit;
import app.algoritmos.rr.RRVariablesFirstFit;
import app.algoritmos.rr.RRVariablesWorstFit;
import app.algoritmos.sjf.SJFFijasBestFit;
import app.algoritmos.sjf.SJFFijasFirstFit;
import app.algoritmos.sjf.SJFVariablesFirstFit;
import app.algoritmos.sjf.SJFVariablesWorstFit;
import app.algoritmos.sjf._1es.SJFFijasBestFit1ES;
import app.algoritmos.sjf._1es.SJFFijasFirstFit1ES;
import app.algoritmos.sjf._1es.SJFVariablesFirstFit1ES;
import app.algoritmos.sjf._1es.SJFVariablesWorstFit1ES;
import app.algoritmos.srtf.SRTFFijasBestFit;
import app.algoritmos.srtf.SRTFFijasFirstFit;
import app.algoritmos.srtf.SRTFVariablesFirstFit;
import app.algoritmos.srtf.SRTFVariablesWorstFit;
import app.algoritmos.srtf._1es.SRTFFijasBestFit1ES;
import app.algoritmos.srtf._1es.SRTFFijasFirstFit1ES;
import app.algoritmos.srtf._1es.SRTFVariablesFirstFit1ES;
import app.algoritmos.srtf._1es.SRTFVariablesWorstFit1ES;
import app.algoritmos.srtf._2es.SRTFFijasBestFit2ES;
import app.algoritmos.srtf._2es.SRTFFijasFirstFit2ES;
import app.algoritmos.srtf._2es.SRTFVariablesFirstFit2ES;
import app.algoritmos.srtf._2es.SRTFVariablesWorstFit2ES;
import app.algoritmos.srtf.c_prioridad.SRTFcPrioridadFijasBestFit;
import app.algoritmos.srtf.c_prioridad.SRTFcPrioridadFijasFirstFit;
import app.algoritmos.srtf.c_prioridad.SRTFcPrioridadVariablesFirstFit;
import app.algoritmos.srtf.c_prioridad.SRTFcPrioridadVariablesWorstFit;
import app.bd.BD;
import app.estadisticas.EstadisticasController;
import app.gantt.GanttCPU;
import app.gantt.GanttCPUES;
import app.mapa_memoria.MemoryMapController;
import app.modelo.ElementoTablaParticion;
import app.modelo.ElementoTablaProceso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

	// MENU
	@FXML private MenuItem menuItemNuevo;
	@FXML private MenuItem menuItemSalir;
	@FXML private MenuItem menuItemAyuda;
	
	// PESTANIAS
	@FXML private Tab particionesFijas;

	// BOTONES
	@FXML private Button botonEjecutar;
	@FXML private Button botonMapaMemoria;
	@FXML private Button botonGantt;
	@FXML private Button botonEstadisticas;

	// CAMPOS
	@FXML private TextField limiteMemoria;
	@FXML private TextField cantidadParticionesFijas;
	@FXML private TextField quantum;

	// CHOICEBOX

	/*
	 * Particiones
	 */
	private ObservableList<String> listaParticiones = FXCollections.observableArrayList("Fijas", "Variables");
	@FXML private ChoiceBox<String> particiones;

	/*
	 * Politicas
	 */
	private ObservableList<String> listaPoliticasFijas = FXCollections.observableArrayList("First-Fit", "Best-Fit");
	private ObservableList<String> listaPoliticasVariables = FXCollections.observableArrayList("First-Fit", "Worst-Fit");
	@FXML private ChoiceBox<String> politicas;

	/*
	 * Algoritmos
	 */
	private ObservableList<String> listaAlgoritmos = FXCollections.observableArrayList("FCFS", "SJF", "SRTF",
			"SRTF (c/Prioridad)", "Round-Robin", "Colas Multinivel");
	@FXML private ChoiceBox<String> algoritmos;

	/*
	 * E/S
	 */
	private ObservableList<String> listaEs = FXCollections.observableArrayList(" 0 ", " 1 ", " 2 ");
	@FXML private ChoiceBox<String> es;
	
	/*
	 * Colas Multinivel
	 */
	
	private ObservableList<String> listaCola1 = FXCollections.observableArrayList("FCFS", "SRTF");
	@FXML private ChoiceBox<String> cola1;
	
	private ObservableList<String> listaCola2 = FXCollections.observableArrayList("FCFS", "SRTF");
	@FXML private ChoiceBox<String> cola2;
	
	private ObservableList<String> listaCola3 = FXCollections.observableArrayList("FCFS", "SRTF");
	@FXML private ChoiceBox<String> cola3;

	/*
	 *  BARRA NOTIFICACIONES
	 */
	@FXML private Label notificaciones;

	// VARIABLES AUXILIARES
	private int idProcesoNuevo = 1; // ID auto-generado procesos
	private int idParticionFija = 1; // ID auto-generado particiones fijas
	private int cantidadRestante = 0; // Cantidad de particiones restantes
	private int memoriaDisponible; // Memoria disponible para particiones fijas
	private int direccionInicio; // Direccion inicio tabla particiones fijas
	private int direccionFin = 0; // Direccion fin tabla particiones fijas
	private int mayorTamanioProceso = 0; // Tamanio mayor proceso
	private int mayorTamanioParticion = 0; // Tamanio mayor particion
	// Almacena los tamanios de las particiones que se van ingresando para evitar repetidas
	private HashSet<Integer> tamaniosParticiones = new HashSet<Integer>();

	@FXML
	public void initialize() { // Metodo propio de JavaFX, se ejecuta cuando se carga la ventana Main

		// INICIALIZAMOS LOS CHOICEBOX
		particiones.setValue("Fijas");
		particiones.setItems(listaParticiones);

		algoritmos.setValue("FCFS");
		algoritmos.setItems(listaAlgoritmos);

		es.setValue(" 0 ");
		es.setItems(listaEs);
		
		cola1.setValue("FCFS");
		cola1.setItems(listaCola1);
		cola2.setValue("FCFS");
		cola2.setItems(listaCola2);
		cola3.setValue("FCFS");
		cola3.setItems(listaCola3);
		
		// CARGAMOS LAS CT GUARDADAS
		leerCT();
		guardados.setValue(" - Seleccione una CT - ");
		guardados.setItems(listaGuardados);

		// EVENTO QUE DESCUENTA 0.10 A LA MEMORIA RAM INGRESADA
		limiteMemoria.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				int ram = Integer.parseInt(newValue);
				memoriaDisponible = (int) (ram - (ram * 0.10));
				direccionInicio = (int) (ram * 0.10) + 1; // De la tabla particiones fijas
				notificaciones.setText("Memoria disponible: " + memoriaDisponible + " u.m.");
			} catch (Exception e) {
				System.out.println("Error en ingreso de datos en campo 'Límite Memoria'. ERROR: " + e.getMessage());
			}
		});

		// INICIALIZAMOS LAS COLUMNAS DE LA TABLA PARTICIONES
		inicializarColumnasTablaParticiones();

		// INICIALIZAMOS LAS COLUMNAS DE LA TABLA PROCESOS
		inicializarColumnasTablaProcesos();
		
		/*
		 * datos de prueba
		 */
		limiteMemoria.setText("639");
		cantidadParticionesFijas.setText("5");

		elementosTablaParticionesFijas.add(new ElementoTablaParticion(1, 100, 1, 100));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(2, 150, 101, 250));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(3, 200, 251, 450));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(4, 75, 451, 525));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(5, 50, 526, 575));
		
		tablaParticion.getItems().setAll(elementosTablaParticionesFijas);
		
		mayorTamanioParticion = 300;
		
	}

	/*
	 * ------------------------------ EVENTOS MENU ------------------------------
	 * 
	 */

	// EVENTO MENU NUEVO
	@FXML
	public void menuItemNuevo(ActionEvent event) {

		// LIMPIO LAS TABLAS
		elementosTablaParticionesFijas.clear();
		tablaParticion.getItems().setAll(elementosTablaParticionesFijas);
		elementosTablaProcesos.clear();
		tablaProceso.getItems().setAll(elementosTablaProcesos);

		// REINICIO LOS IDs
		idParticionFija = 1;
		idProcesoNuevo = 1;
		
		// LIMPIO MEMORIA Y CANTIDAD
		limiteMemoria.setText("");
		cantidadParticionesFijas.setText("");
		
		// LIMPIO QUANTUM
		quantum.setText("");

		// REINICIO LAS VARIABLES AUXILIARES
		mayorTamanioProceso = 0;
		mayorTamanioParticion = 0;
		cantidadRestante = 0;
		ejecutado = false;
		tamaniosParticiones.clear();

		notificaciones.setText("Nuevo");
	}

	// EVENTO MENU SALIR
	@FXML
	public void menuItemSalir(ActionEvent event) {
		System.exit(0);
	}
	
	// EVENTO MENU AYUDA
	@FXML
	public void menuItemAyuda(ActionEvent event) {
		Label label = new Label("Si se rompe: Menu --> Nuevo");

		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(label);
		
		Scene scene = new Scene(stackPane, 300, 100);
		
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Acerca de");
		stage.setResizable(false);
		stage.showAndWait();
	}

	/*
	 * ------------------------------ EVENTOS BOTONES ------------------------------
	 * 
	 */

	// METODO PARA CARGAR LAS VISTAS
	public void abrirVentana(String location, String title) {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource(location));
			Scene scene = new Scene(parent);

			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			System.out.println("Error cargando vista: '" + title + "'. ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// EVENTO BOTON EJECUTAR
	private String algoritmoEjecutado = "";
	private String particionesEjecutado = "";
	private String politicaEjecutado = "";
	private boolean ejecutado = false;

	@FXML
	public void ejecutar(ActionEvent event) {
		
		// ------------------ RESTRICCIONES ------------------
		
		/*
		 * Veo si hay al menos un proceso cargado
		 * 
		 */
		if (elementosTablaProcesos.isEmpty()) {
			alerta("Cargar procesos antes de ejecutar");
			return;
		}
		
		/*
		 * Veo si no se supera el limite de memoria RAM
		 * 
		 */
		try {
			int limiteMemoria = Integer.parseInt(this.limiteMemoria.getText().trim());
			if (limiteMemoria < 100 || limiteMemoria > 1000) {
				alerta("Ingresar un entero entre 100 y 1000 para el campo 'Tamaño de memoria RAM'.");
				return;
			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Límite Memoria'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Tamaño de memoria RAM'.");
			return;
		}
		
		/*
		 * Veo si el proceso mas grande entra con particiones variables
		 * 
		 */
		int ram = Integer.parseInt(limiteMemoria.getText());
		int memoriaDisponibleParticionVariable = (int) (ram - (ram * 0.10));
		if (particiones.getValue() == "Variables" && memoriaDisponibleParticionVariable < mayorTamanioProceso) {
			alerta("La memoria RAM ingresada es insuficiente para los procesos cargados.");
			return;
			/*
			 * Sino veo si entra con particiones fijas
			 */
		} else if (particiones.getValue() == "Fijas" && mayorTamanioParticion < mayorTamanioProceso) {
			alerta("Al menos una partición debe poder almacenar al proceso más grande de tamaño: " + mayorTamanioProceso
					+ " u.m.");
			return;
		}
		
		// ------------------- ALGORITMOS --------------------
		
		/*
		 * FCFS
		 * 
		 */
		if (algoritmos.getValue() == "FCFS") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit" && es.getValue() == " 0 ")
					FCFSFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					FCFSFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					FCFSVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					FCFSVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);

			/*
			 * 1 Rafaga de E/S
			 */
			} else if (es.getValue() == " 1 ") {

				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}

				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					FCFSFijasFirstFit1ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					FCFSFijasBestFit1ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					FCFSVariablesFirstFit1ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					FCFSVariablesWorstFit1ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);

			}

			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {

				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0 | p.getEs2() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}

				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					FCFSFijasFirstFit2ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					FCFSFijasBestFit2ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					FCFSVariablesFirstFit2ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);

				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					FCFSVariablesWorstFit2ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
			}

		} // Fin FCFS

		/*
		 * SJF
		 * 
		 */
		else if (algoritmos.getValue() == "SJF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					SJFFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					SJFFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					SJFVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					SJFVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					SJFFijasFirstFit1ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					SJFFijasBestFit1ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					SJFVariablesFirstFit1ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					SJFVariablesWorstFit1ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0 | p.getEs2() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}
				
//				// Particiones Fijas - First-Fit
//				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
//					SJFFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
//				
//				// Particiones Fijas - Best-Fit
//				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
//					SJFFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
//				
//				// Particiones Variables - First-Fit
//				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
//					SJFVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
//				
//				// Particiones Variables - Worst-Fit
//				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
//					SJFVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
			}

			
		} // Fin SJF

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmos.getValue() == "SRTF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					SRTFFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					SRTFFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					SRTFVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					SRTFVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					SRTFFijasFirstFit1ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					SRTFFijasBestFit1ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					SRTFVariablesFirstFit1ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					SRTFVariablesWorstFit1ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0 | p.getEs2() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
					SRTFFijasFirstFit2ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
					SRTFFijasBestFit2ES.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
				
				// Particiones Variables - First-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
					SRTFVariablesFirstFit2ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
				// Particiones Variables - Worst-Fit
				else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
					SRTFVariablesWorstFit2ES.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
				
			}


		} // Fin SRTF

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmos.getValue() == "SRTF (c/Prioridad)") {
			
			for (ElementoTablaProceso p : elementosTablaProcesos) {
				if (p.getPrioridad() == 0) {
					alerta("Éste algoritmo no es aplicable a una CT sin prioridades.");
					return;
				}
			}

			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
				SRTFcPrioridadFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Fijas - Best-Fit
			else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
				SRTFcPrioridadFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Variables - First-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
				SRTFcPrioridadVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);

			// Particiones Variables - Worst-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
				SRTFcPrioridadVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos);
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmos.getValue() == "Round-Robin") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				// Particiones Fijas - First-Fit
				if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit") {
					try {
						RRFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos,
								Integer.parseInt(quantum.getText()));
					} catch (Exception e) {
						System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
						alerta("Ingresar un entero para el campo 'Quantum'.");
					}
				}

				// Particiones Fijas - Best-Fit
				else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit") {
					try {
						RRFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos,
								Integer.parseInt(quantum.getText()));
					} catch (Exception e) {
						System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
						alerta("Ingresar un entero para el campo 'Quantum'.");
					}
				}

				// Particiones Variables - First-Fit
				if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit") {
					try {
						RRVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos,
								Integer.parseInt(quantum.getText()));
					} catch (Exception e) {
						System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
						alerta("Ingresar un entero para el campo 'Quantum'.");
					}
				}

				// Particiones Variables - Worst-Fit
				if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit") {
					try {
						RRVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos,
								Integer.parseInt(quantum.getText()));
					} catch (Exception e) {
						System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
						alerta("Ingresar un entero para el campo 'Quantum'.");
					}
				}
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}
				
				
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				for (ElementoTablaProceso p : elementosTablaProcesos) {
					if (p.getEs1() == 0 | p.getEs2() == 0) {
						notificaciones.setText(
								"Modificar la cantidad de ráfagas de E/S en la pestaña de 'Condiciones Iniciales' o bien modificar la CT.");
						alerta("La CT cargada no tiene las ráfagas suficientes de E/S por lo tanto no es posible aplicar el algoritmo.");
						return;
					}
				}
				
				
				
			}

			

		} // Fin RR
		
		/*
		 * Colas Multinivel
		 * 
		 */
		else if (algoritmos.getValue() == "Colas Multinivel") {
			
			for (ElementoTablaProceso p : elementosTablaProcesos) {
				if (p.getPrioridad() == 0) {
					alerta("Éste algoritmo no es aplicable a una CT sin prioridades.");
					return;
				}
			}

			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit") {
				ColasMultinivelFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos,
						cola1.getValue(), cola2.getValue(), cola3.getValue());
			}
			
			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit") {
				ColasMultinivelFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos,
						cola1.getValue(), cola2.getValue(), cola3.getValue());
			}

			// Particiones Variables - First-Fit
			if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit") {
				ColasMultinivelVariablesFirstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos,
						cola1.getValue(), cola2.getValue(), cola3.getValue());
			}

			// Particiones Variables - Worst-Fit
			if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit") {
				ColasMultinivelVariablesWorstFit.ejecutar(memoriaDisponibleParticionVariable, elementosTablaProcesos,
						cola1.getValue(), cola2.getValue(), cola3.getValue());
			}

		} // Fin Colas Multinivel

		// Armo la barra de notificaciones
		notificaciones.setText("Ejecutado: Algoritmo " + algoritmos.getValue() + " | Particiones "
				+ particiones.getValue() + " | Política " + politicas.getValue());
		
		// Para saber que salidas ejecutar
		algoritmoEjecutado = algoritmos.getValue();
		particionesEjecutado = particiones.getValue();
		politicaEjecutado = politicas.getValue();
		
		// Habilito las salidas
		ejecutado = true;
		exito("Ejecutado: " + algoritmoEjecutado + " | Particiones "
				+ particionesEjecutado + " | Política " + politicaEjecutado);
	}

	// EVENTO BOTON MAPA DE MEMORIA
	@FXML
	public void mapaMemoria(ActionEvent event) {

		if (!ejecutado) {
			alerta("Ejecutar un algoritmo primero.");
			return;
		}
		
		// Calculo memoria del SO
		int ramSO = Integer.parseInt(limiteMemoria.getText());
		if (particiones.getValue() == "Fijas") {
			for (ElementoTablaParticion e : elementosTablaParticionesFijas) {
				ramSO -= e.getTamanio();
			}
		} else {
			int ram = Integer.parseInt(limiteMemoria.getText());
			ramSO = (int) (ram * 0.10);
		}

		// Armo los mapas

		/*
		 * FCFS
		 * 
		 */
		if (algoritmoEjecutado == "FCFS") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasFirstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasBestFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesFirstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesWorstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
			}

			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasFirstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasBestFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesFirstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesWorstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasFirstFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasBestFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesFirstFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesWorstFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
			}
			
		}

		/*
		 * SJF
		 * 
		 */
		else if (algoritmoEjecutado == "SJF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SJFFijasFirstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SJFFijasBestFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SJFVariablesFirstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SJFVariablesWorstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SJFFijasFirstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SJFFijasBestFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SJFVariablesFirstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SJFVariablesWorstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
			}
			
		} // Fin SJF

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasFirstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasBestFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesFirstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesWorstFit.getMapaMemoria(), ramSO,
							notificaciones.getText());
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasFirstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasBestFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesFirstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesWorstFit1ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasFirstFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasBestFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesFirstFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesWorstFit2ES.getMapaMemoria(), ramSO,
							notificaciones.getText());
				
			}
			
		} // Fin SRTF

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SRTFcPrioridadFijasFirstFit.getMapaMemoria(), ramSO,
						notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SRTFcPrioridadFijasBestFit.getMapaMemoria(), ramSO,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SRTFcPrioridadVariablesFirstFit.getMapaMemoria(),
						ramSO, notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SRTFcPrioridadVariablesWorstFit.getMapaMemoria(),
						ramSO, notificaciones.getText());
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmoEjecutado == "Round-Robin") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(RRFijasFirstFit.getMapaMemoria(), ramSO,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(RRFijasBestFit.getMapaMemoria(), ramSO,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(RRVariablesFirstFit.getMapaMemoria(), ramSO,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(RRVariablesWorstFit.getMapaMemoria(), ramSO,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
		}

		/*
		 * Colas Multinivel
		 * 
		 */
		else if (algoritmoEjecutado == "Colas Multinivel") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(ColasMultinivelFijasFirstFit.getMapaMemoria(), ramSO,
						notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(ColasMultinivelFijasBestFit.getMapaMemoria(), ramSO,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(ColasMultinivelVariablesFirstFit.getMapaMemoria(), ramSO,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(ColasMultinivelVariablesWorstFit.getMapaMemoria(), ramSO,
						notificaciones.getText());
		}

	}

	// EVENTO BOTON GANTT
	@FXML
	public void gantt(ActionEvent event) {
		
		if (!ejecutado) {
			alerta("Ejecutar un algoritmo primero.");
			return;
		}

		/*
		 * FCFS
		 * 
		 */
		if (algoritmoEjecutado == "FCFS") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPU.generarGanttCPU(FCFSFijasFirstFit.getGanttCpu(), notificaciones.getText());
				
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPU.generarGanttCPU(FCFSFijasBestFit.getGanttCpu(), notificaciones.getText());
				
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPU.generarGanttCPU(FCFSVariablesFirstFit.getGanttCpu(), notificaciones.getText());
				
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPU.generarGanttCPU(FCFSVariablesWorstFit.getGanttCpu(), notificaciones.getText());
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(FCFSFijasFirstFit1ES.getGanttCpu(), FCFSFijasFirstFit1ES.getGanttEs(),
							notificaciones.getText());
				
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPUES.generarGanttCPUES(FCFSFijasBestFit1ES.getGanttCpu1ES(), FCFSFijasBestFit1ES.getGantt1ES(),
							notificaciones.getText());
				
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(FCFSVariablesFirstFit1ES.getGanttCpu(), FCFSVariablesFirstFit1ES.getGanttEs(),
							notificaciones.getText());
				
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPUES.generarGanttCPUES(FCFSVariablesWorstFit1ES.getGanttCpu(), FCFSVariablesWorstFit1ES.getGanttEs(),
							notificaciones.getText());
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(FCFSFijasFirstFit2ES.getGanttCpu(), FCFSFijasFirstFit2ES.getGanttEs(),
							notificaciones.getText());
				
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPUES.generarGanttCPUES(FCFSFijasBestFit2ES.getGanttCpu(), FCFSFijasBestFit2ES.getGanttEs(),
							notificaciones.getText());
				
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(FCFSVariablesFirstFit2ES.getGanttCpu(), FCFSVariablesFirstFit2ES.getGanttEs(),
							notificaciones.getText());
				
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPUES.generarGanttCPUES(FCFSVariablesWorstFit2ES.getGanttCpu1ES(), FCFSVariablesWorstFit2ES.getGantt1ES(),
							notificaciones.getText());
				
			}
			
		} // Fin FCFS

		/*
		 * SJF
		 * 
		 */
		else if (algoritmoEjecutado == "SJF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPU.generarGanttCPU(SJFFijasFirstFit.getGanttCpu(), notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPU.generarGanttCPU(SJFFijasBestFit.getGanttCpu(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPU.generarGanttCPU(SJFVariablesFirstFit.getGanttCpu(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPU.generarGanttCPU(SJFVariablesWorstFit.getGanttCpu(), notificaciones.getText());
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(SJFFijasFirstFit1ES.getGanttCpu(), SJFFijasFirstFit1ES.getGanttEs(), notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPUES.generarGanttCPUES(SJFFijasBestFit1ES.getGanttCpu(), SJFFijasBestFit1ES.getGanttEs(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(SJFVariablesFirstFit1ES.getGanttCpu(), SJFVariablesFirstFit1ES.getGanttEs(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPUES.generarGanttCPUES(SJFVariablesWorstFit1ES.getGanttCpu(), SJFVariablesWorstFit1ES.getGanttEs(), notificaciones.getText());
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
//				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
//					GanttCPU.generarGanttCPU(SJFFijasFirstFit.getGanttCpu(), notificaciones.getText());
//				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
//					GanttCPU.generarGanttCPU(SJFFijasBestFit.getGanttCpu(), notificaciones.getText());
//				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
//					GanttCPU.generarGanttCPU(SJFVariablesFirstFit.getGanttCpu(), notificaciones.getText());
//				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
//					GanttCPU.generarGanttCPU(SJFVariablesWorstFit.getGanttCpu(), notificaciones.getText());
				
			}
			
		} // Fin SJF

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPU.generarGanttCPU(SRTFFijasFirstFit.getGanttCpu(), notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPU.generarGanttCPU(SRTFFijasBestFit.getGanttCpu(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPU.generarGanttCPU(SRTFVariablesFirstFit.getGanttCpu(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPU.generarGanttCPU(SRTFVariablesWorstFit.getGanttCpu(), notificaciones.getText());
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(SRTFFijasFirstFit1ES.getGanttCpu(), SRTFFijasFirstFit1ES.getGanttEs(),
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPUES.generarGanttCPUES(SRTFFijasBestFit1ES.getGanttCpu(), SRTFFijasBestFit1ES.getGanttEs(),
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(SRTFVariablesFirstFit1ES.getGanttCpu(),
							SRTFVariablesFirstFit1ES.getGanttEs(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPUES.generarGanttCPUES(SRTFVariablesWorstFit1ES.getGanttCpu(),
							SRTFVariablesWorstFit1ES.getGanttEs(), notificaciones.getText());

			}
			
			/*
			 * 
			 */
			else if (es.getValue() == " 2 ") {
				
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(SRTFFijasFirstFit2ES.getGanttCpu(), SRTFFijasFirstFit2ES.getGanttEs(),
							notificaciones.getText());
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					GanttCPUES.generarGanttCPUES(SRTFFijasBestFit2ES.getGanttCpu(), SRTFFijasBestFit2ES.getGanttEs(),
							notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					GanttCPUES.generarGanttCPUES(SRTFVariablesFirstFit2ES.getGanttCpu(),
							SRTFVariablesFirstFit2ES.getGanttEs(), notificaciones.getText());
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					GanttCPUES.generarGanttCPUES(SRTFVariablesWorstFit2ES.getGanttCpu(),
							SRTFVariablesWorstFit2ES.getGanttEs(), notificaciones.getText());
				
			}
			
		} // Fin SRTF

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				GanttCPU.generarGanttCPU(SRTFcPrioridadFijasFirstFit.getGanttCpu(), notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				GanttCPU.generarGanttCPU(SRTFcPrioridadFijasBestFit.getGanttCpu(), notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				GanttCPU.generarGanttCPU(SRTFcPrioridadVariablesFirstFit.getGanttCpu(), notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				GanttCPU.generarGanttCPU(SRTFcPrioridadVariablesWorstFit.getGanttCpu(), notificaciones.getText());
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmoEjecutado == "Round-Robin") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				
				
			}
			
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				GanttCPU.generarGanttCPU(RRFijasFirstFit.getGanttCpu(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				GanttCPU.generarGanttCPU(RRFijasBestFit.getGanttCpu(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				GanttCPU.generarGanttCPU(RRVariablesFirstFit.getGanttCpu(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				GanttCPU.generarGanttCPU(RRVariablesWorstFit.getGanttCpu(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			
		} // Fin RR

		/*
		 * Colas Multinivel
		 * 
		 */
		else if (algoritmoEjecutado == "Colas Multinivel") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				GanttCPU.generarGanttCPU(ColasMultinivelFijasFirstFit.getGanttCpu(), notificaciones.getText());
			
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				GanttCPU.generarGanttCPU(ColasMultinivelFijasBestFit.getGanttCpu(), notificaciones.getText());
			
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				GanttCPU.generarGanttCPU(ColasMultinivelVariablesFirstFit.getGanttCpu(), notificaciones.getText());
			
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				GanttCPU.generarGanttCPU(ColasMultinivelVariablesWorstFit.getGanttCpu(), notificaciones.getText());
		}
	}

	// EVENTO BOTON ESTADISTICAS
	@FXML
	public void estadisticas(ActionEvent event) {
		
		if (!ejecutado) {
			alerta("Ejecutar un algoritmo primero.");
			return;
		}

		/*
		 * FCFS
		 * 
		 */
		if (algoritmoEjecutado == "FCFS") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(FCFSFijasFirstFit.getSalida(), FCFSFijasFirstFit.getArribo(),
							FCFSFijasFirstFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(FCFSFijasBestFit.getSalida(), FCFSFijasBestFit.getArribo(),
							FCFSFijasBestFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(FCFSVariablesFirstFit.getSalida(), FCFSVariablesFirstFit.getArribo(),
							FCFSVariablesFirstFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(FCFSVariablesWorstFit.getSalida(), FCFSVariablesWorstFit.getArribo(),
							FCFSVariablesWorstFit.getIrrupcion(), notificaciones.getText());
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(FCFSFijasFirstFit1ES.getSalida(), FCFSFijasFirstFit1ES.getArribo(),
							FCFSFijasFirstFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(FCFSFijasBestFit1ES.getSalida(), FCFSFijasBestFit1ES.getArribo(),
							FCFSFijasBestFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(FCFSVariablesFirstFit1ES.getSalida(), FCFSVariablesFirstFit1ES.getArribo(),
							FCFSVariablesFirstFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(FCFSVariablesWorstFit1ES.getSalida(), FCFSVariablesWorstFit1ES.getArribo(),
							FCFSVariablesWorstFit1ES.getIrrupcion(), notificaciones.getText());
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(FCFSFijasFirstFit2ES.getSalida(), FCFSFijasFirstFit2ES.getArribo(),
							FCFSFijasFirstFit2ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(FCFSFijasBestFit2ES.getSalida(), FCFSFijasBestFit2ES.getArribo(),
							FCFSFijasBestFit2ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(FCFSVariablesFirstFit2ES.getSalida(), FCFSVariablesFirstFit2ES.getArribo(),
							FCFSVariablesFirstFit2ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(FCFSVariablesWorstFit2ES.getSalida(), FCFSVariablesWorstFit2ES.getArribo(),
							FCFSVariablesWorstFit2ES.getIrrupcion(), notificaciones.getText());
			}

		} // Fin FCFS

		/*
		 * SJF
		 * 
		 */
		else if (algoritmoEjecutado == "SJF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SJFFijasFirstFit.getSalida(), SJFFijasFirstFit.getArribo(),
							SJFFijasFirstFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(SJFFijasBestFit.getSalida(), SJFFijasBestFit.getArribo(),
							SJFFijasBestFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SJFVariablesFirstFit.getSalida(), SJFVariablesFirstFit.getArribo(),
							SJFVariablesFirstFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(SJFVariablesWorstFit.getSalida(), SJFVariablesWorstFit.getArribo(),
							SJFVariablesWorstFit.getIrrupcion(), notificaciones.getText());
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SJFFijasFirstFit1ES.getSalida(), SJFFijasFirstFit1ES.getArribo(),
							SJFFijasFirstFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(SJFFijasBestFit1ES.getSalida(), SJFFijasBestFit1ES.getArribo(),
							SJFFijasBestFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SJFVariablesFirstFit1ES.getSalida(), SJFVariablesFirstFit1ES.getArribo(),
							SJFVariablesFirstFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(SJFVariablesWorstFit1ES.getSalida(), SJFVariablesWorstFit1ES.getArribo(),
							SJFVariablesWorstFit1ES.getIrrupcion(), notificaciones.getText());
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
//				// Particiones Fijas - First-Fit
//				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
//					EstadisticasController.estadisticas(SJFFijasFirstFit.getSalida(), SJFFijasFirstFit.getArribo(),
//							SJFFijasFirstFit.getIrrupcion(), notificaciones.getText());
//				
//				// Particiones Fijas - Best-Fit
//				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
//					EstadisticasController.estadisticas(SJFFijasBestFit.getSalida(), SJFFijasBestFit.getArribo(),
//							SJFFijasBestFit.getIrrupcion(), notificaciones.getText());
//				
//				// Particiones Variables - First-Fit
//				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
//					EstadisticasController.estadisticas(SJFVariablesFirstFit.getSalida(), SJFVariablesFirstFit.getArribo(),
//							SJFVariablesFirstFit.getIrrupcion(), notificaciones.getText());
//				
//				// Particiones Variables - Worst-Fit
//				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
//					EstadisticasController.estadisticas(SJFVariablesWorstFit.getSalida(), SJFVariablesWorstFit.getArribo(),
//							SJFVariablesWorstFit.getIrrupcion(), notificaciones.getText());
				
			}


		} // Fin SJF

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SRTFFijasFirstFit.getSalida(), SRTFFijasFirstFit.getArribo(),
							SRTFFijasFirstFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(SRTFFijasBestFit.getSalida(), SRTFFijasBestFit.getArribo(),
							SRTFFijasBestFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SRTFVariablesFirstFit.getSalida(), SRTFVariablesFirstFit.getArribo(),
							SRTFVariablesFirstFit.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(SRTFVariablesWorstFit.getSalida(), SRTFVariablesWorstFit.getArribo(),
							SRTFVariablesWorstFit.getIrrupcion(), notificaciones.getText());
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SRTFFijasFirstFit1ES.getSalida(), SRTFFijasFirstFit1ES.getArribo(),
							SRTFFijasFirstFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(SRTFFijasBestFit1ES.getSalida(), SRTFFijasBestFit1ES.getArribo(),
							SRTFFijasBestFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SRTFVariablesFirstFit1ES.getSalida(), SRTFVariablesFirstFit1ES.getArribo(),
							SRTFVariablesFirstFit1ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(SRTFVariablesWorstFit1ES.getSalida(), SRTFVariablesWorstFit1ES.getArribo(),
							SRTFVariablesWorstFit1ES.getIrrupcion(), notificaciones.getText());
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				// Particiones Fijas - First-Fit
				if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SRTFFijasFirstFit2ES.getSalida(), SRTFFijasFirstFit2ES.getArribo(),
							SRTFFijasFirstFit2ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Fijas - Best-Fit
				else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
					EstadisticasController.estadisticas(SRTFFijasBestFit2ES.getSalida(), SRTFFijasBestFit2ES.getArribo(),
							SRTFFijasBestFit2ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - First-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
					EstadisticasController.estadisticas(SRTFVariablesFirstFit2ES.getSalida(), SRTFVariablesFirstFit2ES.getArribo(),
							SRTFVariablesFirstFit2ES.getIrrupcion(), notificaciones.getText());
				
				// Particiones Variables - Worst-Fit
				else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
					EstadisticasController.estadisticas(SRTFVariablesWorstFit2ES.getSalida(), SRTFVariablesWorstFit2ES.getArribo(),
							SRTFVariablesWorstFit2ES.getIrrupcion(), notificaciones.getText());
				
			}

		} // Fin SRTF

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)") {

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.estadisticas(SRTFcPrioridadFijasFirstFit.getSalida(),
						SRTFcPrioridadFijasFirstFit.getArribo(), SRTFcPrioridadFijasFirstFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.estadisticas(SRTFcPrioridadFijasBestFit.getSalida(),
						SRTFcPrioridadFijasBestFit.getArribo(), SRTFcPrioridadFijasBestFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.estadisticas(SRTFcPrioridadVariablesFirstFit.getSalida(),
						SRTFcPrioridadVariablesFirstFit.getArribo(), SRTFcPrioridadVariablesFirstFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Variables - Worst-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.estadisticas(SRTFcPrioridadVariablesWorstFit.getSalida(),
						SRTFcPrioridadVariablesWorstFit.getArribo(), SRTFcPrioridadVariablesWorstFit.getIrrupcion(),
						notificaciones.getText());
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmoEjecutado == "Round-Robin") {
			
			/*
			 * Sin E/S
			 */
			if (es.getValue() == " 0 ") {
				
				
				
			}
			
			/*
			 * 1 Rafaga de E/S
			 */
			else if (es.getValue() == " 1 ") {
				
				
				
			}
			
			/*
			 * 2 Rafagas de E/S
			 */
			else if (es.getValue() == " 2 ") {
				
				
				
			}

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.estadisticas(RRFijasFirstFit.getSalida(), RRFijasFirstFit.getArribo(),
						RRFijasFirstFit.getIrrupcion(), notificaciones.getText() + " | Quantum = " + quantum.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.estadisticas(RRFijasBestFit.getSalida(), RRFijasBestFit.getArribo(),
						RRFijasBestFit.getIrrupcion(), notificaciones.getText() + " | Quantum = " + quantum.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.estadisticas(RRVariablesFirstFit.getSalida(), RRVariablesFirstFit.getArribo(),
						RRVariablesFirstFit.getIrrupcion(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());

			// Particiones Variables - Worst-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.estadisticas(RRVariablesWorstFit.getSalida(), RRVariablesWorstFit.getArribo(),
						RRVariablesWorstFit.getIrrupcion(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			
		} // Fin RR

		/*
		 * Colas Multinivel
		 * 
		 */
		else if (algoritmoEjecutado == "Colas Multinivel") {
			
			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.estadisticas(ColasMultinivelFijasFirstFit.getSalida(),
						ColasMultinivelFijasFirstFit.getArribo(), ColasMultinivelFijasFirstFit.getIrrupcion(),
						notificaciones.getText());
			
			// Particiones Fijas - Best-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.estadisticas(ColasMultinivelFijasBestFit.getSalida(),
						ColasMultinivelFijasBestFit.getArribo(), ColasMultinivelFijasBestFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Variables - First-Fit
			if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.estadisticas(ColasMultinivelVariablesFirstFit.getSalida(),
						ColasMultinivelVariablesFirstFit.getArribo(), ColasMultinivelVariablesFirstFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Variables - Worst-Fit
			if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.estadisticas(ColasMultinivelVariablesWorstFit.getSalida(),
						ColasMultinivelVariablesWorstFit.getArribo(), ColasMultinivelVariablesWorstFit.getIrrupcion(),
						notificaciones.getText());
			
		}

	}

	/*
	 * ----------------------------- EVENTOS CHOICEBOX -----------------------------
	 * 
	 */

	// EVENTO CHOICEBOX PARTICION FIJA/VARIABLE
	@FXML
	public void particionFijaVariable(ActionEvent event) {
		if (particiones.getValue().equals("Fijas")) {
			politicas.setItems(listaPoliticasFijas);
		} else {
			politicas.setItems(listaPoliticasVariables);
		}
		politicas.setValue("First-Fit");
	}

	// EVENTO CHOICEBOX ALGORITMO PESTANIA CONDICIONES INICIALES
	@FXML
	public void algoritmoCondicionesIniciales(ActionEvent event) {
		if (algoritmos.getValue() == "Round-Robin")
			quantum.setDisable(false);
		else
			quantum.setDisable(true);

		if (algoritmos.getValue() == "SRTF (c/Prioridad)" || algoritmos.getValue() == "Colas Multinivel")
			prioridadNuevoProceso.setDisable(false);
		else
			prioridadNuevoProceso.setDisable(true);
		
		if (algoritmos.getValue() == "Colas Multinivel") {
			es.setDisable(true);
			cola1.setDisable(false);
			cola2.setDisable(false);
			cola3.setDisable(false);
		} else {
			es.setDisable(false);
			cola1.setDisable(true);
			cola2.setDisable(true);
			cola3.setDisable(true);
		}
	}

	// EVENTO CHOICEBOX RAFAGAS DE E/S
	@FXML
	public void rafagasEs(ActionEvent event) {
		if (es.getValue() == " 1 ") {
			es1NuevoProceso.setDisable(false);
			cpu2NuevoProceso.setDisable(false);
			es2NuevoProceso.setDisable(true);
			cpu3NuevoProceso.setDisable(true);
		} else if (es.getValue() == " 2 ") {
			es1NuevoProceso.setDisable(false);
			cpu2NuevoProceso.setDisable(false);
			es2NuevoProceso.setDisable(false);
			cpu3NuevoProceso.setDisable(false);
		} else {
			es1NuevoProceso.setDisable(true);
			cpu2NuevoProceso.setDisable(true);
			es2NuevoProceso.setDisable(true);
			cpu3NuevoProceso.setDisable(true);
		}
	}

	/*
	 * ------------------------------ VENTANAS EMERGENTES ------------------------------
	 * 
	 */

	// VENTANA ERROR
	public void alerta(String mensaje) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error - Simulador");
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
	
	// VENTANA EXITO
	public void exito(String mensaje) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Información - Simulador");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
	}
	
	// VENTANA EXITO
	public void confirmacion(String mensaje) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmación - Simulador");
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

	/*
	 * ------------------------ PESTANIA PARTICIONES FIJAS ------------------------
	 * 
	 */

	// TABLA PARTICIONES FIJAS
	@FXML
	private TableView<ElementoTablaParticion> tablaParticion;
	@FXML
	private TableColumn<ElementoTablaParticion, Integer> idParticion;
	@FXML
	private TableColumn<ElementoTablaParticion, Integer> tamanioParticion;
	@FXML
	private TableColumn<ElementoTablaParticion, Integer> dirInicio;
	@FXML
	private TableColumn<ElementoTablaParticion, Integer> dirFin;

	private ObservableList<ElementoTablaParticion> elementosTablaParticionesFijas = FXCollections.observableArrayList();

	// BOTON LIMPIAR TABLA PARTICIONES
	@FXML
	private Button limpiarTablaParticiones;

	// EVENTO BOTON LIMPIAR TABLA PARTICIONES
	@FXML
	private void limpiarTablaParticiones(ActionEvent event) {

		// Se ejecuta solo si la tabla NO esta vacia
		if (!elementosTablaParticionesFijas.isEmpty()) {

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmación - Simulador");
			alert.setHeaderText(null);
			alert.setContentText("Limpiar tabla de particiones fijas, ¿estás seguro?");

			// Si confirma, limpio la tabla
			Optional<ButtonType> respuesta = alert.showAndWait();
			if (respuesta.get() == ButtonType.OK) {
				// LIMPIO LA TABLA
				elementosTablaParticionesFijas.clear();
				tablaParticion.getItems().setAll(elementosTablaParticionesFijas);

				// REINICIO LOS IDs
				idParticionFija = 1;

				// REINICIO LAS VARIABLES AUXILIARES
				mayorTamanioParticion = 0;
				cantidadRestante = 0;
				tamaniosParticiones.clear();
				
				String aux = limiteMemoria.getText();
				limiteMemoria.setText("");
				limiteMemoria.setText(aux);
				
				String notif = notificaciones.getText();
				notif += " | Cantidad de particiones restantes: " + cantidadParticionesFijas.getText();
				notificaciones.setText(notif);
			}
		}
	}
	

	// INICIALIZAR COLUMNAS TABLA PARTICIONES
	private void inicializarColumnasTablaParticiones() {
		idParticion.setCellValueFactory(new PropertyValueFactory<>("id"));
		tamanioParticion.setCellValueFactory(new PropertyValueFactory<>("tamanio"));
		dirInicio.setCellValueFactory(new PropertyValueFactory<>("dirInicio"));
		dirFin.setCellValueFactory(new PropertyValueFactory<>("dirFin"));
	}
	
	// BOTONES AGREGAR PARTICION
	@FXML
	private Button agregarParticion;

	// CAMPO TAMANIO NUEVA PARTICION
	@FXML
	private TextField tamanioNuevaParticion;

	// EVENTO BOTON AGREGAR NUEVA PARTICION
	@FXML
	public void botonAgregarParticion(ActionEvent event) {

		// RESTRICCION MEMORIA RAM
		try {
			int limiteMemoria = Integer.parseInt(this.limiteMemoria.getText().trim());
			if (limiteMemoria < 100 || limiteMemoria > 1000) {
				alerta("Ingresar un entero entre 100 y 1000 para el campo 'Tamaño de memoria RAM'.");
				return;
			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Límite Memoria'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Tamaño de memoria RAM'.");
			return;
		}

		// RESTRICCION CANTIDAD PARTICIONES
		try {
			int cantidadParticionesFijas = Integer.parseInt(this.cantidadParticionesFijas.getText().trim());
			if (cantidadParticionesFijas < 3 || cantidadParticionesFijas > 10) {
				alerta("Ingresar un entero entre 3 y 10 para el campo 'Cantidad'.");
				return;
			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Cantidad'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Cantidad'.");
			return;
		}

		// PARTICION NUEVA
		try {
			int tamanioNuevaParticion = Integer.parseInt(this.tamanioNuevaParticion.getText().trim());
			
			// Controlo que la particion ingresada no sea repetida
			if (cantidadRestante != 0 && tamaniosParticiones.contains(tamanioNuevaParticion)) {
				alerta("Ya existe una partición de ese tamaño. Ingrese un nuevo valor.");
				return;
			}
			
			// Descuento en 1 la cantidad de aprticiones disponibles
			cantidadRestante = Integer.parseInt(cantidadParticionesFijas.getText()) - idParticionFija + 1;

			if (cantidadRestante == 0) {
				alerta("No hay particiones disponibles");
			} else if (tamanioNuevaParticion < 1 || tamanioNuevaParticion > memoriaDisponible) {
				alerta("Ingresar un entero entre 1 y la memoria disponible.");
			} else if (cantidadRestante == 1 && tamanioNuevaParticion != memoriaDisponible) {
				alerta("La última partición debe coincidir con la memoria disponible.");
			} else {
				direccionFin = direccionInicio + tamanioNuevaParticion - 1;

				// Creo la nueva particion
				ElementoTablaParticion particion = new ElementoTablaParticion(idParticionFija, tamanioNuevaParticion,
						direccionInicio, direccionFin);
				elementosTablaParticionesFijas.add(particion);
				tablaParticion.getItems().setAll(elementosTablaParticionesFijas);
				idParticionFija++;

				// Actualizo memoria disponible y particiones restantes
				direccionInicio = direccionFin + 1;
				memoriaDisponible -= tamanioNuevaParticion;
				cantidadRestante--;
				notificaciones.setText("Memoria disponible: " + memoriaDisponible
						+ " u.m. | Cantidad de particiones restantes: " + cantidadRestante);
				
				// Resguardo mayor particion
				if (tamanioNuevaParticion > mayorTamanioParticion)
					mayorTamanioParticion = tamanioNuevaParticion;
				
				// Agrego la particion al SET para evitar repetidas
				tamaniosParticiones.add(tamanioNuevaParticion);

			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Tamaño Partición'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Tamaño Partición'.");
		}
	}

	/*
	 * ----------------------------- PESTANIA PROCESOS -----------------------------
	 * 
	 */
	
	// GUARDAR/CARGAR
	@FXML
	private TextField nombreCT;
	@FXML
	private Button guardarCT;

	private ObservableList<String> listaGuardados = FXCollections.observableArrayList();
	@FXML
	private ChoiceBox<String> guardados;
	@FXML
	private Button cargarCT;

	// EVENTO BOTON GUARDAR CT
	@FXML
	public void botonGuardarCT(ActionEvent event) {
		BD bd = BD.getInstance();
		
		// Chequeo que se ingrese un nombre
		if (nombreCT.getText().isEmpty()) {
			alerta("Ingresar un nombre para la carga de trabajo.");
			return;
		}
		
		// Chequeo que la tabla no este vacia
		if (elementosTablaProcesos.isEmpty()) {
			alerta("Agregar procesos antes de guardar.");
			return;
		}
		
		String nombre = nombreCT.getText();
		
		// Chequeo que el nombre ingresado este disponible
		HashSet<String> setTemporalDeNombres = new HashSet<String>();
		try {
			ResultSet rs = bd.select("SELECT * FROM PROCESO");
			while (rs.next()) {
				String nombreTemporal = rs.getString("nombre");
				setTemporalDeNombres.add(nombreTemporal);
			}
		} catch (Exception e) {
			System.out.println("ERROR al intentar guardar la carga de trabajo.");
		}
		if (setTemporalDeNombres.contains(nombre)) {
			alerta("Ya existe una CT con ese nombre.");
			return;
		}
		
		// INSERT
		for (ElementoTablaProceso p: elementosTablaProcesos) {
			
			// Armamos el query para cada proceso
			String query = "INSERT INTO PROCESO(NOMBRE, IDPROCESO, TAMANIO, TARRIBO, CPU1, ES1, CPU2, ES2, CPU3, PRIORIDAD) VALUES ("
	                + "'" + nombre + "',"
	                + "'" + p.getId() + "',"
	                + "'" + p.getTamanio() + "',"
	                + "'" + p.getTArribo() + "',"
	                + "'" + p.getCpu1() + "',"
	                + "'" + p.getEs1() + "',"
	                + "'" + p.getCpu2() + "',"
	                + "'" + p.getEs2() + "',"
	                + "'" + p.getCpu3() + "',"
	                + "'" + p.getPrioridad() + "'"
	                + ")";
			
			System.out.println(query); // Imprimimos por consola
			
			// Realizamos el INSERT
			if (!bd.insert(query)) return;
		}
		
        exito("Carga de trabajo guardada");
        
        leerCT(); // Actualizo el choicebox de CT
        guardados.setValue(" - Seleccione una CT - ");
	}
	
	// LISTADO CT
	private void leerCT() {
		BD bd = BD.getInstance();
		String query = "SELECT * FROM PROCESO";
		HashSet<String> cargasDeTrabajo = new HashSet<String>();
		try {
			ResultSet rs = bd.select(query);
			while (rs.next()) {
				String nombre = rs.getString("nombre");
				cargasDeTrabajo.add(nombre);
			}
		} catch (Exception e) {
			System.out.println("ERROR al cargar las cargas de trabajo.");
		}
		listaGuardados.clear();
		listaGuardados.add(" - Seleccione una CT - ");
		Iterator<String> nombre = cargasDeTrabajo.iterator();
		while (nombre.hasNext()) {
			listaGuardados.add(nombre.next());
		}
	}
	
	// EVENTO BOTON CARGAR CT
	@FXML
	public void botonCargarCT(ActionEvent event) {
		BD bd = BD.getInstance();
		String ejercicio = guardados.getValue();
		String query = "SELECT * FROM PROCESO WHERE nombre = '" + ejercicio + "'";
		try {
			if (ejercicio != " - Seleccione una CT - ") {
				// Ejecuto el query
				ResultSet rs = bd.select(query);
				
				// Limpio la tabla de procesos
				elementosTablaProcesos.clear();
				
				while (rs.next()) {
					int id = Integer.parseInt(rs.getString("idproceso"));
					int tamanio = Integer.parseInt(rs.getString("tamanio"));
					int tArribo = Integer.parseInt(rs.getString("tarribo"));
					int cpu1 = Integer.parseInt(rs.getString("cpu1"));
					int es1 = Integer.parseInt(rs.getString("es1"));
					int cpu2 = Integer.parseInt(rs.getString("cpu2"));
					int es2 = Integer.parseInt(rs.getString("es2"));
					int cpu3 = Integer.parseInt(rs.getString("cpu3"));
					int prioridad = Integer.parseInt(rs.getString("prioridad"));
					elementosTablaProcesos.add(new ElementoTablaProceso(id, tamanio, tArribo, cpu1, es1, cpu2, es2, cpu3, prioridad));
				}
				tablaProceso.getItems().setAll(elementosTablaProcesos);
				notificaciones.setText("Cargada CT: '" + ejercicio + "'.");
				
				// Actualizo el proceso de mayor tamanio
				for (ElementoTablaProceso p: elementosTablaProcesos) {
					if (p.getTamanio() > mayorTamanioProceso)
						mayorTamanioProceso = p.getTamanio();
				}
				
				// Y el ID de proceso
				idProcesoNuevo = elementosTablaProcesos.size() + 1;
				
				exito("Cargada CT: " + ejercicio);
			}
			
		} catch (Exception e) {
			System.out.println("ERROR al cargar la carga de trabajo '" + ejercicio + "'.");
		}
	}
	
	// TABLA PROCESOS
	@FXML
	private TableView<ElementoTablaProceso> tablaProceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> idProceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> tamanioProceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> tArriboProceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> cpu1Proceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> es1Proceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> cpu2Proceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> es2Proceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> cpu3Proceso;
	@FXML
	private TableColumn<ElementoTablaProceso, Integer> prioridadProceso;

	private ObservableList<ElementoTablaProceso> elementosTablaProcesos = FXCollections.observableArrayList();
	
	// BOTON LIMPIAR TABLA PROCESOS
	@FXML
	private Button limpiarTablaProcesos;
	
	// EVENTO BOTON LIMPIAR TABLA PROCESOS
	@FXML
	private void limpiarTablaProcesos(ActionEvent event) {
		
		// Se ejecuta solo si la tabla NO esta vacia
		if (!elementosTablaProcesos.isEmpty()) {
			
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmación - Simulador");
			alert.setHeaderText(null);
			alert.setContentText("Limpiar tabla de procesos, ¿estás seguro?");
			
			// Si confirma, limpio la tabla
			Optional<ButtonType> respuesta = alert.showAndWait();
			if (respuesta.get() == ButtonType.OK) {
				// LIMPIO LA TABLA
				elementosTablaProcesos.clear();
				tablaProceso.getItems().setAll(elementosTablaProcesos);
				
				// REINICIO LOS IDs
				idProcesoNuevo = 1;
				
				// REINICIO LAS VARIABLES AUXILIARES
				mayorTamanioProceso = 0;
				
			}
		}
	}

	// CAMPOS NUEVO PROCESO
	@FXML
	private TextField tamanioNuevoProceso;
	@FXML
	private TextField tArriboNuevoProceso;
	@FXML
	private TextField cpu1NuevoProceso;
	@FXML
	private TextField es1NuevoProceso;
	@FXML
	private TextField cpu2NuevoProceso;
	@FXML
	private TextField es2NuevoProceso;
	@FXML
	private TextField cpu3NuevoProceso;
	@FXML
	private TextField prioridadNuevoProceso;

	// INICIALIZAR COLUMNAS TABLA PROCESOS
	private void inicializarColumnasTablaProcesos() {
		idProceso.setCellValueFactory(new PropertyValueFactory<>("id"));
		tamanioProceso.setCellValueFactory(new PropertyValueFactory<>("tamanio"));
		tArriboProceso.setCellValueFactory(new PropertyValueFactory<>("tArribo"));
		cpu1Proceso.setCellValueFactory(new PropertyValueFactory<>("cpu1"));
		es1Proceso.setCellValueFactory(new PropertyValueFactory<>("es1"));
		cpu2Proceso.setCellValueFactory(new PropertyValueFactory<>("cpu2"));
		es2Proceso.setCellValueFactory(new PropertyValueFactory<>("es2"));
		cpu3Proceso.setCellValueFactory(new PropertyValueFactory<>("cpu3"));
		prioridadProceso.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
	}

	// BOTON AGREGAR NUEVO PROCESO
	@FXML
	private Button agregarProceso;
	
	// EVENTO BOTON AGREGAR NUEVO PROCESO
	@FXML
	public void botonAgregarProceso(ActionEvent event) {

		try {
			int tamanioNuevoProceso = Integer.parseInt(this.tamanioNuevoProceso.getText().trim());
			int tArriboNuevoProceso = Integer.parseInt(this.tArriboNuevoProceso.getText().trim());
			int cpu1NuevoProceso = Integer.parseInt(this.cpu1NuevoProceso.getText().trim());
			int es1NuevoProceso = Integer.parseInt(this.es1NuevoProceso.getText().trim());
			int cpu2NuevoProceso = Integer.parseInt(this.cpu2NuevoProceso.getText().trim());
			int es2NuevoProceso = Integer.parseInt(this.es2NuevoProceso.getText().trim());
			int cpu3NuevoProceso = Integer.parseInt(this.cpu3NuevoProceso.getText().trim());
			int prioridadNuevoProceso = Integer.parseInt(this.prioridadNuevoProceso.getText().trim());

			if (idProcesoNuevo <= 10) {
				
				/*
				 * 900 u.m. es el tamanio maximo de un proceso
				 */
				if (tamanioNuevoProceso > 900) {
					alerta("El tamaño máximo de un proceso es 900 u.m.");
					return;
				} else if (tamanioNuevoProceso > mayorTamanioProceso) {
					// Actualizo el proceso de mayor tamanio
					mayorTamanioProceso = tamanioNuevoProceso;
				}
				
				/*
				 * Agrego el proceso
				 */
				ElementoTablaProceso proceso = new ElementoTablaProceso(idProcesoNuevo, tamanioNuevoProceso,
						tArriboNuevoProceso, cpu1NuevoProceso, es1NuevoProceso, cpu2NuevoProceso, es2NuevoProceso,
						cpu3NuevoProceso, prioridadNuevoProceso);
				elementosTablaProcesos.add(proceso);
				tablaProceso.getItems().setAll(elementosTablaProcesos);
				notificaciones.setText("Límite cantidad de procesos: 10 | Agregado proceso: " + idProcesoNuevo);
				idProcesoNuevo++;
			} else
				alerta("El número máximo de procesos es 10.");

		} catch (Exception e) {
			alerta("Completar todos los campos con valores enteros.");
		}
	}

}
