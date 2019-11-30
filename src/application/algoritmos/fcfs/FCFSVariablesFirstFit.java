package application.algoritmos.fcfs;

import java.util.ArrayList;
import java.util.Collections;

import application.algoritmos.util.OrdenarPorTArribo;
import application.model.ElementoTablaProceso;
import application.model.ParticionVariable;
import application.model.Proceso;
import javafx.collections.ObservableList;

public class FCFSVariablesFirstFit {

	public static ArrayList<ArrayList<ParticionVariable>> mapaMemoria;

	public static ArrayList<Integer> ganttCpu;

	public static int[] salida;
	public static int[] arribo;
	public static int[] irrupcion;

	/*
	 * Devuelve el estado de las particiones para armar el mapa de memoria
	 */
	public static ArrayList<ArrayList<ParticionVariable>> getMapaMemoria() {
		return mapaMemoria;
	}

	/*
	 * Devuelve los procesos para armar el gantt
	 */
	public static ArrayList<Integer> getGanttCpu() {
		return ganttCpu;
	}

	/*
	 * Devuelve los tiempos de salida, arribo e irrupcion para calcular las
	 * estadisticas.
	 * 
	 * En la posicion 0 de salida guardo el t ocioso.
	 * 
	 */
	public static int[] getSalida() {
		return salida;
	}

	public static int[] getArribo() {
		return arribo;
	}

	public static int[] getIrrupcion() {
		return irrupcion;
	}

	/*
	 * EJECUTAR
	 * 
	 */
	public static void ejecutar(int memoriaDisponible, ObservableList<ElementoTablaProceso> tablaProcesos) {

		System.out.println("FCFS - Particiones Variables - FirstFit\n");

		ArrayList<ParticionVariable> particiones = new ArrayList<ParticionVariable>();
		ArrayList<Proceso> procesos = new ArrayList<Proceso>();

		ArrayList<Proceso> nuevos = new ArrayList<Proceso>();
		ArrayList<Proceso> listos = new ArrayList<Proceso>();

		ArrayList<Proceso> ejecutandoCpu = new ArrayList<Proceso>();

		// Inicializamos mapaMemoria
		mapaMemoria = new ArrayList<ArrayList<ParticionVariable>>();

		// Inicializamos ganttCpu
		ganttCpu = new ArrayList<Integer>();

		/*
		 * Inicializamos la lista de Particiones
		 */
			ParticionVariable particionInicial = new ParticionVariable(1, memoriaDisponible, true);
			particiones.add(particionInicial);

		/*
		 * Cargamos la lista de Procesos
		 */
		int tIrrupcion = 0; // Para controlar el bucle principal

		for (ElementoTablaProceso p : tablaProcesos) {
			Proceso proceso = new Proceso(p.getId(), p.getTamanio(), p.getTArribo(), p.getCpu1(), p.getEs1(),
					p.getCpu2(), p.getEs2(), p.getCpu3(), p.getPrioridad());
			procesos.add(proceso);
			tIrrupcion += proceso.getCpu1();
		}

		Collections.sort(procesos, new OrdenarPorTArribo());
		int idUltimoProceso = procesos.get(procesos.size() - 1).getId();

		// Inicializamos el arreglo de los t de salida
		salida = new int[(procesos.size() + 1)];

		// Cargamos el arreglo de arribos e irrupcion
		arribo = new int[(procesos.size() + 1)];
		irrupcion = new int[(procesos.size() + 1)];

		for (int i = 0; i < procesos.size(); i++) {
			arribo[procesos.get(i).getId()] = procesos.get(i).getTArribo();
			irrupcion[procesos.get(i).getId()] = procesos.get(i).getCpu1();
		}

		/*
		 * ARRANCA EL ALGORITMO
		 * 
		 */

		int t = 0; // Reloj
		int tOcioso = 0;
		boolean llegoElUltimo = false;

		while (t <= tIrrupcion + tOcioso) {

			/*
			 * ARMO LA COLA DE NUEVOS DEL INSTANTE t
			 * 
			 */
			for (Proceso p : procesos) {
				if (p.getTArribo() == t) {
					nuevos.add(p);
					if (p.getId() == idUltimoProceso)
						llegoElUltimo = true;
				}
			}

			/*
			 * EJECUTO CPU
			 * 
			 */
			if (!ejecutandoCpu.isEmpty()) {
				ejecutarCpu(particiones, procesos, ejecutandoCpu, tablaProcesos, t);
			}

			/*
			 * TIEMPO OCIOSO
			 * 
			 */
			if (nuevos.isEmpty() && ejecutandoCpu.isEmpty() && !llegoElUltimo) {
				tOcioso++;
			}

			/*
			 * ARMO LA COLA DE LISTOS EN INSTANTE t
			 * 
			 */
			for (int i = 0; i < nuevos.size(); i++) {
				Proceso pNuevo = nuevos.get(i);
				for (ParticionVariable particion : particiones) {
					int tamanio = particion.getDirFin() - particion.getDirInicio();
					if (particion.isLibre() && pNuevo.getTamanio() <= tamanio) {
						listos.add(pNuevo);
						particion.setProceso(pNuevo.getId());
						particion.setLibre(false);
						nuevos.remove(i);
						i--;// Para evitar ConcurrentModificationException
						break;
					}
				}
			}

			/*
			 * AGREGO LOS LISTOS A EJECUCION
			 * 
			 */
			for (int i = 0; i < listos.size(); i++) {
				Proceso pListo = listos.get(i);
				ejecutandoCpu.add(pListo);
				listos.remove(i);
				i--; // Para evitar ConcurrentModificationException
			}

			/*
			 * TIEMPO OCIOSO EN GANTT
			 * 
			 */
			if (ejecutandoCpu.isEmpty())
				ganttCpu.add(0);

			/*
			 * GUARDO EL ESTADO DE LAS PARTICIONES
			 * 
			 */
			mapaMemoria.add(t, new ArrayList<ParticionVariable>());
			for (ParticionVariable p : particiones) {
				ParticionVariable particion = new ParticionVariable(p.getDirInicio(), p.getDirFin(), p.getProceso(), p.isLibre());
				mapaMemoria.get(t).add(particion);
			}

			t++;

		} // Fin While

		salida[0] = tOcioso;

	} // Fin FCFS

	/*
	 * METODO EJECUTAR CPU
	 * 
	 */
	private static void ejecutarCpu(ArrayList<ParticionVariable> particiones, ArrayList<Proceso> procesos,
			ArrayList<Proceso> ejecutandoCpu, ObservableList<ElementoTablaProceso> tablaProcesos, int t) {

		Proceso procesoActual = ejecutandoCpu.get(0);
		int cpu = procesoActual.getCpu1();
		cpu--;

		// Actualizo Gantt
		ganttCpu.add(procesoActual.getId());

		// Actualizo el valor de CPU1
		procesoActual.setCpu1(cpu);
		ejecutandoCpu.remove(0);
		ejecutandoCpu.add(0, procesoActual);

		if (cpu == 0) {

			// Libero la particion
			for (ParticionVariable particion : particiones) {
				if (particion.getProceso() == procesoActual.getId()) {
					particion.setProceso(0);
					particion.setLibre(true);
					break;
				}
			}

			// Guardo el t de salida
			salida[procesoActual.getId()] = t;

			// Luego saco el proceso
			ejecutandoCpu.remove(0);

		}

	}
}
