package app;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;

import app.algoritmos.fcfs.FCFSFijasBestFit;
import app.algoritmos.fcfs.FCFSFijasFirstFit;
import app.algoritmos.fcfs.FCFSVariablesFirstFit;
import app.algoritmos.fcfs.FCFSVariablesWorstFit;
import app.algoritmos.rr.RRFijasBestFit;
import app.algoritmos.rr.RRFijasFirstFit;
import app.algoritmos.rr.RRVariablesFirstFit;
import app.algoritmos.rr.RRVariablesWorstFit;
import app.algoritmos.sjf.SJFFijasBestFit;
import app.algoritmos.sjf.SJFFijasFirstFit;
import app.algoritmos.sjf.SJFVariablesFirstFit;
import app.algoritmos.sjf.SJFVariablesWorstFit;
import app.algoritmos.srtf.SRTFFijasBestFit;
import app.algoritmos.srtf.SRTFFijasFirstFit;
import app.algoritmos.srtf.SRTFVariablesFirstFit;
import app.algoritmos.srtf.SRTFVariablesWorstFit;
import app.algoritmos.srtf.SRTFcPrioridadFijasBestFit;
import app.algoritmos.srtf.SRTFcPrioridadFijasFirstFit;
import app.algoritmos.srtf.SRTFcPrioridadVariablesFirstFit;
import app.algoritmos.srtf.SRTFcPrioridadVariablesWorstFit;
import app.bd.BD;
import app.estadisticas.EstadisticasController;
import app.gantt.GanttCPU;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainController {

	// MENU
	@FXML private MenuItem menuItemNuevo;
	@FXML private MenuItem menuItemSalir;
	
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

	// BARRA NOTIFICACIONES
	@FXML private Label notificaciones;

	// VARIABLES AUXILIARES

	private int idParticionFija = 1; // ID auto-generado particiones fijas
	private int memoriaDisponible; // Memoria disponible para particiones fijas o variables
	private int direccionInicio; // Direccion inicio tabla particiones fijas
	private int direccionFin = 0; // Direccion fin tabla particiones fijas
	private int mayorTamanioProceso = 0; // Tamanio mayor proceso
	private int mayorTamanioParticion = 0; // Tamanio mayor particion

	private int idProcesoNuevo = 1; // ID auto-generado procesos

	@FXML
	public void initialize() {

		// INICIALIZAMOS LOS CHOICEBOX
		particiones.setValue("Fijas");
		particiones.setItems(listaParticiones);

		algoritmos.setValue("FCFS");
		algoritmos.setItems(listaAlgoritmos);

		es.setValue(" 0 ");
		es.setItems(listaEs);
		
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

		// HABILITO Y REINICIO LOS CAMPOS MEMORIA RAM Y CANTIDAD
		limiteMemoria.setDisable(false);
		limiteMemoria.setText(null);

		cantidadParticionesFijas.setDisable(false);
		cantidadParticionesFijas.setText(null);

		// REINICIO LOS IDs
		idParticionFija = 1;
		idProcesoNuevo = 1;

		// REINICIO LAS VARIABLES AUXILIARES
		mayorTamanioProceso = 0;
		mayorTamanioParticion = 0;
		ejecutado = false;

		notificaciones.setText("Nuevo");
	}

	// EVENTO MENU SALIR
	@FXML
	public void menuItemSalir(ActionEvent event) {
		System.exit(0);
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
		 * Veo si el proceso mas grande entra con particiones variables
		 * 
		 */
		if (particiones.getValue() == "Variables" && memoriaDisponible < mayorTamanioProceso) {
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

			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
				FCFSFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Fijas - Best-Fit
			else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
				FCFSFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Variables - First-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
				FCFSVariablesFirstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);

			// Particiones Variables - Worst-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
				FCFSVariablesWorstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);
		}

		/*
		 * SJF
		 * 
		 */
		else if (algoritmos.getValue() == "SJF") {

			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
				SJFFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Fijas - Best-Fit
			else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
				SJFFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Variables - First-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
				SJFVariablesFirstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);

			// Particiones Variables - Worst-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
				SJFVariablesWorstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);
		}

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmos.getValue() == "SRTF") {

			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
				SRTFFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Fijas - Best-Fit
			else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
				SRTFFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Variables - First-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
				SRTFVariablesFirstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);

			// Particiones Variables - Worst-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
				SRTFVariablesWorstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);

		}

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmos.getValue() == "SRTF (c/Prioridad)") {

			// Particiones Fijas - First-Fit
			if (particiones.getValue() == "Fijas" && politicas.getValue() == "First-Fit")
				SRTFcPrioridadFijasFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Fijas - Best-Fit
			else if (particiones.getValue() == "Fijas" && politicas.getValue() == "Best-Fit")
				SRTFcPrioridadFijasBestFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

			// Particiones Variables - First-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "First-Fit")
				SRTFcPrioridadVariablesFirstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);

			// Particiones Variables - Worst-Fit
			else if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit")
				SRTFcPrioridadVariablesWorstFit.ejecutar(memoriaDisponible, elementosTablaProcesos);
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmos.getValue() == "Round-Robin") {

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
					RRVariablesFirstFit.ejecutar(memoriaDisponible, elementosTablaProcesos,
							Integer.parseInt(quantum.getText()));
				} catch (Exception e) {
					System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
					alerta("Ingresar un entero para el campo 'Quantum'.");
				}
			}

			// Particiones Variables - Worst-Fit
			if (particiones.getValue() == "Variables" && politicas.getValue() == "Worst-Fit") {
				try {
					RRVariablesWorstFit.ejecutar(memoriaDisponible, elementosTablaProcesos,
							Integer.parseInt(quantum.getText()));
				} catch (Exception e) {
					System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
					alerta("Ingresar un entero para el campo 'Quantum'.");
				}
			}

		} else
			System.out.println(algoritmos.getValue() + " not yet.");

		// Armo la barra de notificaciones
		notificaciones.setText("Ejecutado: Algoritmo " + algoritmos.getValue() + " | Particiones "
				+ particiones.getValue() + " | Política " + politicas.getValue());
		// Para armar los titulos de las salidas
		algoritmoEjecutado = algoritmos.getValue();
		particionesEjecutado = particiones.getValue();
		politicaEjecutado = politicas.getValue();
		// Habilito las salidas
		ejecutado = true;
	}

	// EVENTO BOTON MAPA DE MEMORIA
	@FXML
	public void mapaMemoria(ActionEvent event) {

		if (!ejecutado) {
			alerta("Ejecutar un algoritmo primero.");
			return;
		}
		
		// Calculo memoria del SO
		int ram = Integer.parseInt(limiteMemoria.getText());
		for (ElementoTablaParticion e : elementosTablaParticionesFijas) {
			ram -= e.getTamanio();
		}

		// Armo los mapas

		/*
		 * FCFS
		 * 
		 */
		if (algoritmoEjecutado == "FCFS") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(FCFSFijasBestFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(FCFSVariablesWorstFit.getMapaMemoria(), ram,
						notificaciones.getText());
		}

		/*
		 * SJF
		 * 
		 */
		else if (algoritmoEjecutado == "SJF") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SJFFijasFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SJFFijasBestFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SJFVariablesFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SJFVariablesWorstFit.getMapaMemoria(), ram,
						notificaciones.getText());
		}

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SRTFFijasBestFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SRTFVariablesWorstFit.getMapaMemoria(), ram,
						notificaciones.getText());
		}

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SRTFcPrioridadFijasFirstFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(SRTFcPrioridadFijasBestFit.getMapaMemoria(), ram,
						notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SRTFcPrioridadVariablesFirstFit.getMapaMemoria(),
						ram, notificaciones.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(SRTFcPrioridadVariablesWorstFit.getMapaMemoria(),
						ram, notificaciones.getText());
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmoEjecutado == "Round-Robin") {
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(RRFijasFirstFit.getMapaMemoria(), ram,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				MemoryMapController.generarMapaMemoriaPartFijas(RRFijasBestFit.getMapaMemoria(), ram,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(RRVariablesFirstFit.getMapaMemoria(), ram,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				MemoryMapController.generarMapaMemoriaPartVariables(RRVariablesWorstFit.getMapaMemoria(), ram,
						notificaciones.getText() + " | Quantum = " + quantum.getText());
		}

		else
			System.out.println(algoritmos.getValue() + " not yet.");

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
		 * SJF
		 * 
		 */
		else if (algoritmoEjecutado == "SJF") {
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
		 * SRTF
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF") {
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
		}

		else
			System.out.println(algoritmos.getValue() + " not yet.");
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

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(FCFSFijasFirstFit.getSalida(), FCFSFijasFirstFit.getArribo(),
						FCFSFijasFirstFit.getIrrupcion(), notificaciones.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.Statistics(FCFSFijasBestFit.getSalida(), FCFSFijasBestFit.getArribo(),
						FCFSFijasBestFit.getIrrupcion(), notificaciones.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(FCFSVariablesFirstFit.getSalida(), FCFSVariablesFirstFit.getArribo(),
						FCFSVariablesFirstFit.getIrrupcion(), notificaciones.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.Statistics(FCFSVariablesWorstFit.getSalida(), FCFSVariablesWorstFit.getArribo(),
						FCFSVariablesWorstFit.getIrrupcion(), notificaciones.getText());
		}

		/*
		 * SJF
		 * 
		 */
		else if (algoritmoEjecutado == "SJF") {

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(SJFFijasFirstFit.getSalida(), SJFFijasFirstFit.getArribo(),
						SJFFijasFirstFit.getIrrupcion(), notificaciones.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.Statistics(SJFFijasBestFit.getSalida(), SJFFijasBestFit.getArribo(),
						SJFFijasBestFit.getIrrupcion(), notificaciones.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(SJFVariablesFirstFit.getSalida(), SJFVariablesFirstFit.getArribo(),
						SJFVariablesFirstFit.getIrrupcion(), notificaciones.getText());

			// Particiones Variables - Worst-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.Statistics(SJFVariablesWorstFit.getSalida(), SJFVariablesWorstFit.getArribo(),
						SJFVariablesWorstFit.getIrrupcion(), notificaciones.getText());

		}

		/*
		 * SRTF
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF") {

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(SRTFFijasFirstFit.getSalida(), SRTFFijasFirstFit.getArribo(),
						SRTFFijasFirstFit.getIrrupcion(), notificaciones.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.Statistics(SRTFFijasBestFit.getSalida(), SRTFFijasBestFit.getArribo(),
						SRTFFijasBestFit.getIrrupcion(), notificaciones.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(SRTFVariablesFirstFit.getSalida(), SRTFVariablesFirstFit.getArribo(),
						SRTFVariablesFirstFit.getIrrupcion(), notificaciones.getText());
		}

		/*
		 * SRTF c/Prioridad
		 * 
		 */
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)") {

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(SRTFcPrioridadFijasFirstFit.getSalida(),
						SRTFcPrioridadFijasFirstFit.getArribo(), SRTFcPrioridadFijasFirstFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.Statistics(SRTFcPrioridadFijasBestFit.getSalida(),
						SRTFcPrioridadFijasBestFit.getArribo(), SRTFcPrioridadFijasBestFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(SRTFcPrioridadVariablesFirstFit.getSalida(),
						SRTFcPrioridadVariablesFirstFit.getArribo(), SRTFcPrioridadVariablesFirstFit.getIrrupcion(),
						notificaciones.getText());

			// Particiones Variables - Worst-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.Statistics(SRTFcPrioridadVariablesWorstFit.getSalida(),
						SRTFcPrioridadVariablesWorstFit.getArribo(), SRTFcPrioridadVariablesWorstFit.getIrrupcion(),
						notificaciones.getText());
		}

		/*
		 * Round-Robin
		 * 
		 */
		else if (algoritmoEjecutado == "Round-Robin") {

			// Particiones Fijas - First-Fit
			if (particionesEjecutado == "Fijas" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(RRFijasFirstFit.getSalida(), RRFijasFirstFit.getArribo(),
						RRFijasFirstFit.getIrrupcion(), notificaciones.getText() + " | Quantum = " + quantum.getText());

			// Particiones Fijas - Best-Fit
			else if (particionesEjecutado == "Fijas" && politicaEjecutado == "Best-Fit")
				EstadisticasController.Statistics(RRFijasBestFit.getSalida(), RRFijasBestFit.getArribo(),
						RRFijasBestFit.getIrrupcion(), notificaciones.getText() + " | Quantum = " + quantum.getText());

			// Particiones Variables - First-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "First-Fit")
				EstadisticasController.Statistics(RRVariablesFirstFit.getSalida(), RRVariablesFirstFit.getArribo(),
						RRVariablesFirstFit.getIrrupcion(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());

			// Particiones Variables - Worst-Fit
			else if (particionesEjecutado == "Variables" && politicaEjecutado == "Worst-Fit")
				EstadisticasController.Statistics(RRVariablesWorstFit.getSalida(), RRVariablesWorstFit.getArribo(),
						RRVariablesWorstFit.getIrrupcion(),
						notificaciones.getText() + " | Quantum = " + quantum.getText());
		}

		else
			System.out.println(algoritmos.getValue() + " not yet.");

	}

	/*
	 * ----------------------------- EVENTOS CHOICEBOX -----------------------------
	 * 
	 */

	// EVENTO CHOICEBOX PARTICION FIJA/VARIABLE
	@FXML
	public void particionFijaVariable(ActionEvent event) {
		if (particiones.getValue().equals("Fijas")) {
			particionesFijas.setDisable(false);
			cantidadParticionesFijas.setDisable(false);
			politicas.setItems(listaPoliticasFijas);
		} else {
			particionesFijas.setDisable(true);
			cantidadParticionesFijas.setDisable(true);
			politicas.setItems(listaPoliticasVariables);
		}
		politicas.setValue("First-Fit");
		// :v
		if (!tablaParticion.getItems().isEmpty())
			cantidadParticionesFijas.setDisable(true);
	}

	// EVENTO CHOICEBOX ALGORITMO PESTANIA CONDICIONES INICIALES
	@FXML
	public void algoritmoCondicionesIniciales(ActionEvent event) {
		if (algoritmos.getValue() == "Round-Robin")
			quantum.setDisable(false);
		else
			quantum.setDisable(true);

		if (algoritmos.getValue() == "SRTF (c/Prioridad)")
			prioridadNuevoProceso.setDisable(false);
		else
			prioridadNuevoProceso.setDisable(true);
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
	 * ------------------------------ RESTRICCIONES ------------------------------
	 * 
	 */

	// VENTANA ERROR ENTRADA DE DATOS
	public void alerta(String mensaje) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error - Simulador");
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

//	// RESTRICCION CAMPO LIMITE DE MEMORIA
//	@FXML
//	public void restriccionMemoria(ActionEvent event) {
//		try {
//			int limiteMemoria = Integer.parseInt(this.limiteMemoria.getText().trim());
//			if (limiteMemoria < 100 || limiteMemoria > 1000) {
//				alerta("Ingresar un entero entre 100 y 1000");
//			}
//		} catch (Exception e) {
//			System.out.println("Error en ingreso de datos en campo 'Límite Memoria'. ERROR: " + e.getMessage());
//			alerta("Ingresar un entero para el campo 'Tamaño de memoria'.");
//		}
//	}
//
//	// RESTRICCION CAMPO CANTIDAD PARTICIONES FIJAS
//	@FXML
//	public void restriccionCantidadParticionesFijas(ActionEvent event) {
//		try {
//			int cantidadParticionesFijas = Integer.parseInt(this.cantidadParticionesFijas.getText().trim());
//			if (cantidadParticionesFijas < 3 || cantidadParticionesFijas > 10) {
//				alerta("Ingresar un entero entre 3 y 10.");
//			}
//		} catch (Exception e) {
//			System.out.println("Error en ingreso de datos en campo 'Cantidad'. ERROR: " + e.getMessage());
//			alerta("Ingresar un entero para el campo 'Cantidad'.");
//		}
//	}
//
//	// RESTRICCION CAMPO QUANTUM
//	@FXML
//	public void restriccionQuantum(ActionEvent event) {
//		try {
//			int quantum = Integer.parseInt(this.quantum.getText().trim());
//			if (quantum < 1 || quantum > 8) {
//				alerta("Ingresar un entero entre 1 y 8.");
//			}
//		} catch (Exception e) {
//			System.out.println("Error en ingreso de datos en campo 'Cantidad'. ERROR: " + e.getMessage());
//			alerta("Ingresar un entero para el campo 'Quantum'.");
//		}
//	}

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

	// BOTONES AGREGAR/ELIMINAR PARTICION
	@FXML
	private Button agregarParticion;

	// CAMPO TAMANIO NUEVA PARTICION
	@FXML
	private TextField tamanioNuevaParticion;

	// INICIALIZAR COLUMNAS TABLA PARTICIONES
	private void inicializarColumnasTablaParticiones() {
		idParticion.setCellValueFactory(new PropertyValueFactory<>("id"));
		tamanioParticion.setCellValueFactory(new PropertyValueFactory<>("tamanio"));
		dirInicio.setCellValueFactory(new PropertyValueFactory<>("dirInicio"));
		dirFin.setCellValueFactory(new PropertyValueFactory<>("dirFin"));
	}

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
		limiteMemoria.setDisable(true); // Bloqueo los campos de la pestania condiciones iniciales
		cantidadParticionesFijas.setDisable(true);

		try {
			int tamanioNuevaParticion = Integer.parseInt(this.tamanioNuevaParticion.getText().trim());
			int cantidadRestante = Integer.parseInt(cantidadParticionesFijas.getText()) - idParticionFija + 1;

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
		
		if (nombreCT.getText().isEmpty()) {
			alerta("Ingresar un nombre para la carga de trabajo.");
			return;
		}
		String nombre = nombreCT.getText();
		
		for (ElementoTablaProceso p: elementosTablaProcesos) {
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
			
			// INSERT
			if (!bd.insert(query)) return;
		}
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Carga de trabajo guardada.");
        alert.showAndWait();
        
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
	
	// BOTON AGREGAR PROCESO
	@FXML
	private Button agregarProceso;


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
