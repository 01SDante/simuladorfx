package application.algoritmos.rr;

import java.util.ArrayList;
import java.util.Collections;

import application.algoritmos.util.OrdenarPorDirInicio;
import application.algoritmos.util.OrdenarPorTArribo;
import application.model.ElementoTablaProceso;
import application.model.ParticionVariable;
import application.model.Proceso;
import javafx.collections.ObservableList;

public class RRVariablesWorstFit {

	private static ArrayList<ArrayList<ParticionVariable>> mapaMemoria;

	private static ArrayList<Integer> ganttCpu;

	private static int[] salida;
	private static int[] arribo;
	private static int[] irrupcion;

	/*
	 * Devuelve el estado de las aprticiones para armar el mapa de memoria
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

	// quantum
	private static int q;

	public static int getQ() {
		return q;
	}

	public static void setQ(int quantum) {
		q = quantum;
	}

	// Proceso auxiliar
	private static Proceso procesoAux;

	public static Proceso getProcesoAux() {
		return procesoAux;
	}

	public static void setProcesoAux(Proceso p) {
		procesoAux = p;
	}

	/*
	 * EJECUTAR
	 * 
	 */
	public static void ejecutar(int memoriaDisponible, ObservableList<ElementoTablaProceso> tablaProcesos,
			int quantum) {

		System.out.println("RR con quantum = " + quantum + " - Particiones Variables - FirstFit\n");

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

		q = quantum;
		procesoAux = null;

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
				ejecutarCpu(particiones, procesos, ejecutandoCpu, tablaProcesos, t, quantum);
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

				int tamanioWorstFit = Integer.MIN_VALUE; // Para guardar el tamanio del peor ajuste
				int posicionWorstFit = -1; // Para guardar la posicion de la particion con peor ajuste
				
				// Recorro la lista de particiones para encontrar el peor ajuste
				for (int j = 0; j < particiones.size(); j++) {
					ParticionVariable p = particiones.get(j);
					int tamanio = p.getDirFin() - p.getDirInicio() + 1;
					if (p.isLibre() && pNuevo.getTamanio() <= tamanio && tamanio > tamanioWorstFit) {
						tamanioWorstFit = tamanio;
						posicionWorstFit = j;
					}
				}
				
				// Si lo encuentro, le asigno el proceso nuevo
				if (posicionWorstFit != -1) {
					
					// Obtengo la particion con worst-fit
					ParticionVariable particion = particiones.get(posicionWorstFit);
					int tamanio = particion.getDirFin() - particion.getDirInicio() + 1;
					
					// Agrego el proceso a la cola de listos
					listos.add(pNuevo);
					
					// Saco la particion
					particiones.remove(posicionWorstFit);
					
					// Divido la particion y hago dos nuevas
					ParticionVariable p1 = new ParticionVariable(particion.getDirInicio(),
							particion.getDirInicio() + pNuevo.getTamanio() - 1, pNuevo.getId(), false);
					ParticionVariable p2 = new ParticionVariable(p1.getDirFin() + 1, particion.getDirFin(), true);
					particiones.add(p1);

					if (pNuevo.getTamanio() < tamanio) {
						particiones.add(p2);
					}

					// Ordeno las particiones
					Collections.sort(particiones, new OrdenarPorDirInicio());

					nuevos.remove(i);
				}

			} // Fin para nuevos

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

			if (procesoAux != null) { // Agrego el que termino el quantum
				ejecutandoCpu.add(procesoAux);
				procesoAux = null;
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
				ParticionVariable particion = new ParticionVariable(p.getDirInicio(), p.getDirFin(), p.getProceso(),
						p.isLibre());
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
			ArrayList<Proceso> ejecutandoCpu, ObservableList<ElementoTablaProceso> tablaProcesos, int t, int quantum) {

		Proceso procesoActual = ejecutandoCpu.get(0);
		int cpu = procesoActual.getCpu1();
		cpu--;
		q--; // Descuento el quantum

		// Actualizo Gantt
		ganttCpu.add(procesoActual.getId());

		// Actualizo el valor de CPU1
		procesoActual.setCpu1(cpu);
		ejecutandoCpu.remove(0);
		ejecutandoCpu.add(0, procesoActual);

		if (cpu == 0) { // Si termino lo saco

			// Libero la particion
			for (ParticionVariable particion : particiones) {
				if (particion.getProceso() == procesoActual.getId()) {
					particion.setProceso(0);
					particion.setLibre(true);
					break;
				}
			}

			// Junto las particiones libres contiguas
			if (particiones.size() > 1) {
				for (int i = 1; i < particiones.size(); i++) {
					if (particiones.get(i - 1).isLibre() && particiones.get(i).isLibre()) {
						ParticionVariable anterior = particiones.get(i - 1);
						ParticionVariable actual = particiones.get(i);
						ParticionVariable nueva = new ParticionVariable(anterior.getDirInicio(), actual.getDirFin(),
								true);
						particiones.add(nueva);
						particiones.remove(i - 1);
						particiones.remove(i - 1);
						Collections.sort(particiones, new OrdenarPorDirInicio());
						i = 0;
					}
				}
			}

			// Guardo el t de salida
			salida[procesoActual.getId()] = t;

			// Luego saco el proceso
			ejecutandoCpu.remove(0);

			// Por ultimo reinicio el quantum
			q = quantum;

		} else if (q == 0) { // Si termino el quantum

			// Reinicio el quantum
			q = quantum;

			/*
			 * Saco el proceso y lo guardo en una variable auxiliar para que no anteponga a
			 * los listos que se agregan en este instante t, luego de actualizar los listos
			 * lo agrego a ejecucion
			 */
			ejecutandoCpu.remove(0);
			procesoAux = procesoActual;

		}

	}

}
