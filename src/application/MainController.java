package application;

import application.algorithms.FCFSFixedFirstFit;
import application.algorithms.RRFixedFirstFit;
import application.algorithms.SJFFixedFirstFit;
import application.algorithms.SRTFFixedFirstFit;
import application.algorithms.SRTFwPriorFixedFirstFit;
import application.gantt.GanttCPU;
import application.memory_map.MemoryMapController;
import application.model.ElementoTablaParticion;
import application.model.ElementoTablaProceso;
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
	private MenuItem menuItemNuevo;
	@FXML
	private MenuItem menuItemAbrir;
	@FXML
	private MenuItem menuItemGuardar;
	@FXML
	private MenuItem menuItemSalir;

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
			"SRTF (c/Prioridad)", "Round-Robin", "Colas Multinivel");
	@FXML
	private ChoiceBox<String> algoritmos;
	
	private ObservableList<String> listaEs = FXCollections.observableArrayList(" 0 ", " 1 ", " 2 ");
	
	@FXML
	private ChoiceBox<String> es;

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
		
		es.setValue(" 0 ");
		es.setItems(listaEs);

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
		 * -------- DATOS DE PRUEBA ---------
		 */
		limiteMemoria.setText("639");
		limiteMemoria.setDisable(true);
		cantidadParticionesFijas.setText("5");
		cantidadParticionesFijas.setDisable(true);

		elementosTablaParticionesFijas.add(new ElementoTablaParticion(1, 100, 1, 100));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(2, 150, 101, 250));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(3, 200, 251, 450));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(4, 75, 451, 525));
		elementosTablaParticionesFijas.add(new ElementoTablaParticion(5, 50, 526, 575));
		tablaParticion.getItems().setAll(elementosTablaParticionesFijas);
		
		// ejercicio particiones fijas
//		elementosTablaProcesos.add(new ElementoTablaProceso(1, 100, 0, 3, 0, 0, 0, 0, 0));
//		elementosTablaProcesos.add(new ElementoTablaProceso(2, 150, 2, 4, 0, 0, 0, 0, 0));
//		elementosTablaProcesos.add(new ElementoTablaProceso(3, 90, 5, 3, 0, 0, 0, 0, 0));
//		elementosTablaProcesos.add(new ElementoTablaProceso(4, 75, 6, 2, 0, 0, 0, 0, 0));
		
		// ejercicio 2
		elementosTablaProcesos.add(new ElementoTablaProceso(1, 10, 0, 10, 0, 0, 0, 0, 0));
		elementosTablaProcesos.add(new ElementoTablaProceso(2, 10, 0, 6, 0, 0, 0, 0, 0));
		elementosTablaProcesos.add(new ElementoTablaProceso(3, 10, 1, 2, 0, 0, 0, 0, 0));
		elementosTablaProcesos.add(new ElementoTablaProceso(4, 10, 2, 1, 0, 0, 0, 0, 0));
		elementosTablaProcesos.add(new ElementoTablaProceso(5, 10, 2, 8, 0, 0, 0, 0, 0));
		
		// c/prioridad
//		elementosTablaProcesos.add(new ElementoTablaProceso(1, 10, 0, 8, 0, 0, 0, 0, 5));
//		elementosTablaProcesos.add(new ElementoTablaProceso(2, 10, 3, 4, 0, 0, 0, 0, 7));
//		elementosTablaProcesos.add(new ElementoTablaProceso(3, 10, 6, 2, 0, 0, 0, 0, 9));
//		elementosTablaProcesos.add(new ElementoTablaProceso(4, 10, 10, 3, 0, 0, 0, 0, 8));
//		elementosTablaProcesos.add(new ElementoTablaProceso(5, 10, 15, 6, 0, 0, 0, 0, 1));
//		elementosTablaProcesos.add(new ElementoTablaProceso(6, 10, 24, 4, 0, 0, 0, 0, 5));

		tablaProceso.getItems().setAll(elementosTablaProcesos);
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

		mayorTamanio = 0;

		notificaciones.setText("Nuevo");
	}

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

	private String algoritmoEjecutado = "";

	@FXML
	public void execute(ActionEvent event) {
		if (algoritmos.getValue() == "FCFS")
			FCFSFixedFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

		else if (algoritmos.getValue() == "SJF")
			SJFFixedFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

		else if (algoritmos.getValue() == "SRTF")
			SRTFFixedFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);
		
		else if (algoritmos.getValue() == "SRTF (c/Prioridad)")
			SRTFwPriorFixedFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos);

		else if (algoritmos.getValue() == "Round-Robin") {
			try {
				RRFixedFirstFit.ejecutar(elementosTablaParticionesFijas, elementosTablaProcesos,
						Integer.parseInt(quantum.getText()));
			} catch (Exception e) {
				System.out.println("Error en ingreso de datos en campo 'Quantum'. ERROR: " + e.getMessage());
				alerta("Ingresar un entero para el campo 'Quantum'.");
			}
		}

		else
			System.out.println(algoritmos.getValue() + " not yet.");

		// Armo la barra de notificaciones
		notificaciones.setText("Ejecutado: Algoritmo " + algoritmos.getValue() + " | Particiones "
				+ particiones.getValue() + " | Política " + politicas.getValue());
		// Para armar los titulos de las salidas
		algoritmoEjecutado = algoritmos.getValue();
	}

	// EVENTO BOTON MAPA DE MEMORIA
	@FXML
	public void loadMemoryMap(ActionEvent event) {

		// Calculo memoria del SO
		int ram = Integer.parseInt(limiteMemoria.getText());
		for (ElementoTablaParticion e : elementosTablaParticionesFijas) {
			ram -= e.getTamanio();
		}

		// Armo los mapas
		if (algoritmoEjecutado == "FCFS")
			MemoryMapController.generarMapaMemoria(FCFSFixedFirstFit.getMapaMemoria(), ram, notificaciones.getText());

		else if (algoritmoEjecutado == "SJF")
			MemoryMapController.generarMapaMemoria(SJFFixedFirstFit.getMapaMemoria(), ram, notificaciones.getText());

		else if (algoritmoEjecutado == "SRTF")
			MemoryMapController.generarMapaMemoria(SRTFFixedFirstFit.getMapaMemoria(), ram, notificaciones.getText());
		
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)")
			MemoryMapController.generarMapaMemoria(SRTFwPriorFixedFirstFit.getMapaMemoria(), ram, notificaciones.getText());

		else if (algoritmoEjecutado == "Round-Robin")
			MemoryMapController.generarMapaMemoria(RRFixedFirstFit.getMapaMemoria(), ram,
					notificaciones.getText() + " | Quantum = " + quantum.getText());

		else
			System.out.println(algoritmos.getValue() + " not yet.");

	}

	// EVENTO BOTON GANTT
	@FXML
	public void loadGantt(ActionEvent event) {
		if (algoritmoEjecutado == "FCFS")
			GanttCPU.generarGanttCPU(FCFSFixedFirstFit.getGanttCpu(), notificaciones.getText());

		else if (algoritmoEjecutado == "SJF")
			GanttCPU.generarGanttCPU(SJFFixedFirstFit.getGanttCpu(), notificaciones.getText());

		else if (algoritmoEjecutado == "SRTF")
			GanttCPU.generarGanttCPU(SRTFFixedFirstFit.getGanttCpu(), notificaciones.getText());
		
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)")
			GanttCPU.generarGanttCPU(SRTFwPriorFixedFirstFit.getGanttCpu(), notificaciones.getText());

		else if (algoritmoEjecutado == "Round-Robin")
			GanttCPU.generarGanttCPU(RRFixedFirstFit.getGanttCpu(),
					notificaciones.getText() + " | Quantum = " + quantum.getText());

		else
			System.out.println(algoritmos.getValue() + " not yet.");
	}

	// EVENTO BOTON ESTADISTICAS
	@FXML
	public void loadStatistics(ActionEvent event) {

		if (algoritmoEjecutado == "FCFS")
			StatisticsController.Statistics(FCFSFixedFirstFit.getSalida(), FCFSFixedFirstFit.getArribo(),
					FCFSFixedFirstFit.getIrrupcion(), notificaciones.getText());

		else if (algoritmoEjecutado == "SJF")
			StatisticsController.Statistics(SJFFixedFirstFit.getSalida(), SJFFixedFirstFit.getArribo(),
					SJFFixedFirstFit.getIrrupcion(), notificaciones.getText());

		else if (algoritmoEjecutado == "SRTF")
			StatisticsController.Statistics(SRTFFixedFirstFit.getSalida(), SRTFFixedFirstFit.getArribo(),
					SRTFFixedFirstFit.getIrrupcion(), notificaciones.getText());
		
		else if (algoritmoEjecutado == "SRTF (c/Prioridad)")
			StatisticsController.Statistics(SRTFwPriorFixedFirstFit.getSalida(), SRTFwPriorFixedFirstFit.getArribo(),
					SRTFwPriorFixedFirstFit.getIrrupcion(), notificaciones.getText());

		else if (algoritmoEjecutado == "Round-Robin")
			StatisticsController.Statistics(RRFixedFirstFit.getSalida(), RRFixedFirstFit.getArribo(),
					RRFixedFirstFit.getIrrupcion(), notificaciones.getText() + " | Quantum = " + quantum.getText());

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
			if (cantidadParticionesFijas < 3 || cantidadParticionesFijas > 10) {
				alerta("Ingresar un entero entre 3 y 10.");
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
			if (quantum < 1 || quantum > 8) {
				alerta("Ingresar un entero entre 1 y 8.");
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

	// BOTONES AGREGAR/ELIMINAR PROCESO
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

				if (tamanioNuevoProceso > mayorTamanio) {
					alerta("El tamaño del proceso debe ser menor a igual a la máxima partición: " + mayorTamanio
							+ " u.m.");
					return;
				}

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
