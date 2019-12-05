package app.algoritmos.fcfs;

import java.util.ArrayList;
import java.util.Collections;

import app.algoritmos.util.OrdenarPorTArribo;
import app.modelo.ElementoTablaParticion;
import app.modelo.ElementoTablaProceso;
import app.modelo.Particion;
import app.modelo.Proceso;
import javafx.collections.ObservableList;

public class FCFSFijasFirstFit2ES {

	private static ArrayList<ArrayList<Particion>> mapaMemoria;

	private static ArrayList<Integer> ganttCpu;
	private static ArrayList<Integer> ganttEs;

	private static int[] salida;
	private static int[] arribo;
	private static int[] irrupcion;

	/*
	 * Devuelve el estado de las particiones para armar el mapa
	 */
	public static ArrayList<ArrayList<Particion>> getMapaMemoria() {
		return mapaMemoria;
	}

	/*
	 * Devuelve los procesos para armar el Gantt
	 */

	public static ArrayList<Integer> getGanttCpu1ES() { // nuevo
		return ganttCpu;
	}

	public static ArrayList<Integer> getGantt1ES() { // nuevo
		return ganttEs;
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
	public static void ejecutar(ObservableList<ElementoTablaParticion> tablaParticiones,
			ObservableList<ElementoTablaProceso> tablaProcesos) {

		System.out.println("FCFS 1E/S - Particiones Fijas - FirstFit\n");

		ArrayList<Particion> particiones = new ArrayList<Particion>();
		ArrayList<Proceso> procesos = new ArrayList<Proceso>();

		ArrayList<Proceso> nuevos = new ArrayList<Proceso>();
		ArrayList<Proceso> listos = new ArrayList<Proceso>();

		ArrayList<Proceso> ejecutandoCpu = new ArrayList<Proceso>();
		ArrayList<Proceso> ejecutandoEs = new ArrayList<Proceso>();

		// Inicializamos mapaMemoria
		mapaMemoria = new ArrayList<ArrayList<Particion>>();

		// Inicializamos los Gantt
		ganttCpu = new ArrayList<Integer>();
		ganttEs = new ArrayList<Integer>();

		/*
		 * Cargamos la lista de Particiones
		 */
		for (ElementoTablaParticion p : tablaParticiones) {
			Particion particion = new Particion(p.getId(), p.getTamanio(), true);
			particiones.add(particion);
		}

		/*
		 * Cargamos la lista de Procesos
		 */
		int tIrrupcion = 0; // Para controlar el bucle principal

		for (ElementoTablaProceso p : tablaProcesos) {
			Proceso proceso = new Proceso(p.getId(), p.getTamanio(), p.getTArribo(), p.getCpu1(), p.getEs1(),
					p.getCpu2(), p.getEs2(), p.getCpu3(), p.getPrioridad());
			procesos.add(proceso);
			tIrrupcion += proceso.getCpu1() + proceso.getCpu2() + proceso.getCpu3();
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
			irrupcion[procesos.get(i).getId()] = procesos.get(i).getCpu1() + procesos.get(i).getCpu2() + procesos.get(i).getCpu3();
		}

		/*
		 * ARRANCA EL ALGORITMO
		 * 
		 */

		int t = 0; // Reloj
		int tOcioso = 0;
		boolean llegoElUltimo = false;

		while (t <= tIrrupcion + tOcioso) { // tIrrupcion + tOcioso
			
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
			 * COLA EJECUTANDO ES
			 * 
			 */
			if (!ejecutandoEs.isEmpty()) {
				ejecutarES(particiones, procesos, ejecutandoCpu, ejecutandoEs, tablaProcesos, t);
			}

			/*
			 * COLA EJECUTANDO CPU
			 * 
			 */
			if (!ejecutandoCpu.isEmpty()) {
				ejecutarCPU(particiones, procesos, ejecutandoCpu, ejecutandoEs, tablaProcesos, t);
			}

			/*
			 * TIEMPO OCIOSO
			 * 
			 */

			/*
			 * Si las colas de nuevos y ejecutandoCpu estan vacias pero no llego el ultimo
			 * proceso --> hay tiempo ocioso en CPU
			 */
			if (nuevos.isEmpty() && ejecutandoCpu.isEmpty() && !llegoElUltimo) {
				tOcioso++;
			}
			
			/*
			 * Si llego el ultimo, ejecutandoCpu esta vacia pero ejecutandoEs no --> hay
			 * tiempo ocioso
			 */
			if (llegoElUltimo && !ejecutandoEs.isEmpty() && ejecutandoCpu.isEmpty())
				tOcioso++;

			/*
			 * ARMO LA COLA DE LISTOS EN INSTANTE t
			 * 
			 */
			for (int i = 0; i < nuevos.size(); i++) {
				Proceso pNuevo = nuevos.get(i);
				for (Particion particion : particiones) {
					if (particion.isLibre() && pNuevo.getTamanio() <= particion.getTamanio()) {
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
			 * TIEMPO OCIOSO EN GANTT ES
			 * 
			 */
			if (ejecutandoEs.isEmpty())
				ganttEs.add(0);

			/*
			 * TIEMPO OCIOSO EN GANTT CPU
			 * 
			 */
			if (ejecutandoCpu.isEmpty())
				ganttCpu.add(0);

			/*
			 * GUARDO EL ESTADO DE LAS PARTICIONES
			 * 
			 */
			mapaMemoria.add(t, new ArrayList<Particion>());
			for (Particion p : particiones) {
				Particion particion = new Particion(p.getId(), p.getTamanio(), p.getProceso(), p.isLibre());
				mapaMemoria.get(t).add(particion);
			}

			t++;

		} // Fin while

		salida[0] = tOcioso;

	} // Fin FCFS

	/*
	 * EJECUTANDO CPU
	 * 
	 */
	private static void ejecutarCPU(ArrayList<Particion> particiones, ArrayList<Proceso> procesos,
			ArrayList<Proceso> ejecutandoCpu, ArrayList<Proceso> ejecutandoEs,
			ObservableList<ElementoTablaProceso> tablaProcesos, int t) {

		Proceso procesoActual = ejecutandoCpu.get(0);

		if (procesoActual.getCpu1() > 0) { // Trato CPU1

			int cpu = procesoActual.getCpu1();
			cpu--;

			// Actualizo Gantt
			ganttCpu.add(procesoActual.getId());

			// Actualizo el valor de CPU1
			procesoActual.setCpu1(cpu);
			ejecutandoCpu.remove(0);
			ejecutandoCpu.add(0, procesoActual);

			if (cpu == 0) {

				// Lo saco y lo paso a ES
				ejecutandoEs.add(ejecutandoCpu.get(0));
				ejecutandoCpu.remove(0);

			}

		} else if (procesoActual.getCpu2() > 0 && procesoActual.getEs1() == 0) { // Trato CPU2

			int cpu = procesoActual.getCpu2();
			cpu--;

			// Actualizo Gantt
			ganttCpu.add(procesoActual.getId());

			// Actualizo el valor de CPU2
			procesoActual.setCpu2(cpu);
			ejecutandoCpu.remove(0);
			ejecutandoCpu.add(0, procesoActual);

			if (cpu == 0) {

				// Lo saco y lo paso a ES
				ejecutandoEs.add(ejecutandoCpu.get(0));
				ejecutandoCpu.remove(0);

			}

		} else if (procesoActual.getCpu3() > 0 && procesoActual.getEs2() == 0) { // Trato CPU3

			int cpu = procesoActual.getCpu3();
			cpu--;

			// Actualizo Gantt
			ganttCpu.add(procesoActual.getId());

			// Actualizo el valor de CPU3
			procesoActual.setCpu3(cpu);
			ejecutandoCpu.remove(0);
			ejecutandoCpu.add(0, procesoActual);

			if (cpu == 0) {

				// Libero la particion
				for (Particion particion : particiones) {
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

	/*
	 * EJECUTANDO ES
	 * 
	 */
	private static void ejecutarES(ArrayList<Particion> particiones, ArrayList<Proceso> procesos,
			ArrayList<Proceso> ejecutandoCpu, ArrayList<Proceso> ejecutandoEs,
			ObservableList<ElementoTablaProceso> tablaProcesos, int t) {

		Proceso procesoActual = ejecutandoEs.get(0);
		
		if (procesoActual.getEs1() > 0) { // Trato ES1

			int es = procesoActual.getEs1();
			es--;

			// Actualizo Gantt
			ganttEs.add(procesoActual.getId());

			// Actualizo el valor de ES1
			procesoActual.setEs1(es);
			ejecutandoEs.remove(0);
			ejecutandoEs.add(0, procesoActual);

			if (es == 0) {

				// Lo saco y lo paso a CPU
				ejecutandoCpu.add(ejecutandoEs.get(0));
				ejecutandoEs.remove(0);

			}

		} else if (procesoActual.getEs2() > 0 && procesoActual.getCpu2() == 0) { // Trato ES2
			
			int es = procesoActual.getEs2();
			es--;
			
			// Actualizo Gantt
			ganttEs.add(procesoActual.getId());
			
			// Actualizo el valor de ES2
			procesoActual.setEs2(es);
			ejecutandoEs.remove(0);
			ejecutandoEs.add(0, procesoActual);
			
			if (es == 0) {
				
				// Lo saco y lo paso a CPU
				ejecutandoCpu.add(ejecutandoEs.get(0));
				ejecutandoEs.remove(0);
				
			}
			
		}

	}
	
}
