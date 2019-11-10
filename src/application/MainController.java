package application;

import application.algorithms.FCFS;
import application.gantt.GanttController;
import application.memory_map.MemoryMapController;
import application.model.Particion;
import application.model.Proceso;
import application.statistics.StatisticsController;
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
	@FXML
	private MenuItem menuItemAbrir;
	@FXML
	private MenuItem menuItemGuardar;

	// PESTANIAS
	@FXML
	private Tab particionesFijas;

	// BOTONES
	@FXML
	private Button executeButton;
	@FXML
	private Button memoryMapButton;
	@FXML
	private Button ganttButton;
	@FXML
	private Button statisticsButton;

	// CAMPOS
	@FXML
	private TextField limiteMemoria;
	@FXML
	private TextField cantidadParticionesFijas;
	@FXML
	private TextField quantum;

	// CHOICEBOX
	private ObservableList<String> listaParticiones = FXCollections.observableArrayList("Fijas", "Variables");
	@FXML
	private ChoiceBox<String> particiones;

	private ObservableList<String> listaPoliticasFijas = FXCollections.observableArrayList("First-Fit", "Best-Fit");
	private ObservableList<String> listaPoliticasVariables = FXCollections.observableArrayList("First-Fit",
			"Worst-fit");
	@FXML
	private ChoiceBox<String> politicas;

	private ObservableList<String> listaAlgoritmos = FXCollections.observableArrayList("FCFS", "SJF", "SRTF",
			"Round-Robin", "Colas Multinivel");
	@FXML
	private ChoiceBox<String> algoritmos;

	// BARRA NOTIFICACIONES
	@FXML
	private Label notificaciones;

	// VARIABLES AUXILIARES

	private int idParticionFija = 1; // ID auto-generado particiones fijas
	private int memoriaDisponible; // Memoria disponible para particiones fijas
	private int direccionInicio; // Direccion inicio tabla particiones fijas
	private int direccionFin = 0; // Direccion fin tabla particiones fijas
	private int mayorTamanio = 0; // Tamanio mayor particion

	private int idProcesoNuevo = 1; // ID auto-generado procesos

	@FXML
	public void initialize() {

		// INICIALIZAMOS LOS CHOICEBOX
		particiones.setValue("Fijas");
		particiones.setItems(listaParticiones);

		algoritmos.setValue("FCFS");
		algoritmos.setItems(listaAlgoritmos);

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

	// EVENTO MENU ABRIR -> VENTANA ABRIR ARCHIVO
	@FXML
	public void menuItemAbrir(ActionEvent event) {
		loadWindow("/application/save_load/Load.fxml", "Abrir archivo");
	}

	// EVENTO MENU GUARDAR -> VENTANA GUARDAR ARCHIVO
	@FXML
	public void menuItemGuardar(ActionEvent event) {
		loadWindow("/application/save_load/Save.fxml", "Guardar archivo");
	}

	/*
	 * ------------------------------ EVENTOS BOTONES ------------------------------
	 * 
	 */

	// METODO PARA CARGAR LAS VISTAS
	public void loadWindow(String location, String title) {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource(location));
			Scene scene = new Scene(parent);

			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			System.out.println("Error cargando vista: '" + title + "'. ERROR: " + e.getMessage());
		}
	}

	// EVENTO BOTON EJECUTAR
	@FXML
	public void execute(ActionEvent event) {
		if (algoritmos.getValue() == "FCFS")
			FCFS.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
		else
			System.out.println(algoritmos.getValue() + " not yet.");
	}

	// EVENTO BOTON MAPA DE MEMORIA
	@FXML
	public void loadMemoryMap(ActionEvent event) {
//		loadWindow("/application/memory_map/MemoryMap.fxml", "Mapa de memoria");
		MemoryMapController.MemoryMap();
	}

	// EVENTO BOTON GANTT
	@FXML
	public void loadGantt(ActionEvent event) {
//		loadWindow("/application/gantt/Gantt.fxml", "Diagrama de Gantt");
		GanttController.GanttDiagram();
	}

	// EVENTO BOTON ESTADISTICAS
	@FXML
	public void loadStatistics(ActionEvent event) {
//		loadWindow("/application/statistics/Statistics.fxml", "Estadísticas");
		StatisticsController.Statistics();
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
	}

	// EVENTO CHOICEBOX ALGORITMO PESTANIA CONDICIONES INICIALES
	@FXML
	public void algoritmoCondicionesIniciales(ActionEvent event) {
		if (algoritmos.getValue() == "Round-Robin")
			quantum.setDisable(false);
		else
			quantum.setDisable(true);
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

	// RESTRICCION CAMPO LIMITE DE MEMORIA
	@FXML
	public void restriccionMemoria(ActionEvent event) {
		try {
			int limiteMemoria = Integer.parseInt(this.limiteMemoria.getText().trim());
			if (limiteMemoria < 100 || limiteMemoria > 1000) {
				alerta("Ingresar un entero entre 100 y 1000");
			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Límite Memoria'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Tamaño de memoria'.");
		}
	}

	// RESTRICCION CAMPO CANTIDAD PARTICIONES FIJAS
	@FXML
	public void restriccionCantidadParticionesFijas(ActionEvent event) {
		try {
			int cantidadParticionesFijas = Integer.parseInt(this.cantidadParticionesFijas.getText().trim());
			if (cantidadParticionesFijas < 3 || cantidadParticionesFijas > 6) {
				alerta("Ingresar un entero entre 3 y 6.");
			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Cantidad'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Cantidad'.");
		}
	}

	// RESTRICCION CAMPO QUANTUM
	@FXML
	public void restriccionQuantum(ActionEvent event) {
		try {
			int quantum = Integer.parseInt(this.quantum.getText().trim());
			if (quantum < 1 || quantum > 5) {
				alerta("Ingresar un entero entre 3 y 5.");
			}
		} catch (Exception e) {
			System.out.println("Error en ingreso de datos en campo 'Cantidad'. ERROR: " + e.getMessage());
			alerta("Ingresar un entero para el campo 'Quantum'.");
		}
	}

	/*
	 * ------------------------ PESTANIA PARTICIONES FIJAS ------------------------
	 * 
	 */

	// TABLA PARTICIONES FIJAS
	@FXML
	private TableView<Particion> tablaParticion;
	@FXML
	private TableColumn<Particion, Integer> idParticion;
	@FXML
	private TableColumn<Particion, Integer> tamanioParticion;
	@FXML
	private TableColumn<Particion, Integer> dirInicio;
	@FXML
	private TableColumn<Particion, Integer> dirFin;

	private ObservableList<Particion> elementosTablaParticionesFijas = FXCollections.observableArrayList();

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
			if (cantidadParticionesFijas < 3 || cantidadParticionesFijas > 6) {
				alerta("Ingresar un entero entre 3 y 6 para el campo 'Cantidad'.");
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
				Particion particion = new Particion(idParticionFija, tamanioNuevaParticion, direccionInicio,
						direccionFin);
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
				if (tamanioNuevaParticion > mayorTamanio)
					mayorTamanio = tamanioNuevaParticion;
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

	// TABLA PROCESOS
	@FXML
	private TableView<Proceso> tablaProceso;
	@FXML
	private TableColumn<Proceso, Integer> idProceso;
	@FXML
	private TableColumn<Proceso, Integer> tamanioProceso;
	@FXML
	private TableColumn<Proceso, Integer> prioridadProceso;
	@FXML
	private TableColumn<Proceso, Integer> cpuProceso;
	@FXML
	private TableColumn<Proceso, Integer> esProceso;
	@FXML
	private TableColumn<Proceso, Integer> tArriboProceso;

	private ObservableList<Proceso> elementosTablaProcesos = FXCollections.observableArrayList();

	// BOTONES AGREGAR/ELIMINAR PROCESO
	@FXML
	private Button agregarProceso;

	// CAMPOS NUEVO PROCESO
	@FXML
	private TextField tamanioNuevoProceso;
	@FXML
	private TextField cpuNuevoProceso;
	@FXML
	private TextField esNuevoProceso;
	@FXML
	private TextField tArriboNuevoProceso;

	// INICIALIZAR COLUMNAS TABLA PROCESOS
	private void inicializarColumnasTablaProcesos() {
		idProceso.setCellValueFactory(new PropertyValueFactory<>("id"));
		tamanioProceso.setCellValueFactory(new PropertyValueFactory<>("tamanio"));
		cpuProceso.setCellValueFactory(new PropertyValueFactory<>("cpu"));
		esProceso.setCellValueFactory(new PropertyValueFactory<>("es"));
		tArriboProceso.setCellValueFactory(new PropertyValueFactory<>("tArribo"));
	}

	// EVENTO BOTON AGREGAR NUEVO PROCESO
	@FXML
	public void botonAgregarProceso(ActionEvent event) {

		try {
			int tamanioNuevoProceso = Integer.parseInt(this.tamanioNuevoProceso.getText().trim());
			int cpuNuevoProceso = Integer.parseInt(this.cpuNuevoProceso.getText().trim());
			int esNuevoProceso = Integer.parseInt(this.esNuevoProceso.getText().trim());
			int tArriboNuevoProceso = Integer.parseInt(this.tArriboNuevoProceso.getText().trim());

			if (idProcesoNuevo <= 10) {

				if (tamanioNuevoProceso > mayorTamanio) {
					alerta("El tamaño del proceso debe ser menor a igual a la máxima partición: " + mayorTamanio
							+ " u.m.");
					return;
				}

				Proceso proceso = new Proceso(idProcesoNuevo, tamanioNuevoProceso, cpuNuevoProceso, esNuevoProceso,
						tArriboNuevoProceso);
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
