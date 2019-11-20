package application.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import application.model.ElementoTablaParticion;
import application.model.ElementoTablaProceso;
import application.model.Particion;
import application.model.Proceso;
import javafx.collections.ObservableList;

public class FCFS {

	public static ArrayList<ArrayList<Particion>> mapaMemoria;

	public static HashMap<Integer, Proceso> ganttCpuHM;

	/*
	 * Devuelve el estado de las particiones para armar el mapa
	 */
	public static ArrayList<ArrayList<Particion>> FCFSMapaMemoria() {
		return mapaMemoria;
	}

	/*
	 * Devuelve la lista de procesos para armar el Gantt
	 */
	public static HashMap<Integer, Proceso> FCFSGanttHM() {
		return ganttCpuHM;
	}

	/*
	 * EJECUTAR
	 * 
	 */
	public static void ejecutar(ObservableList<ElementoTablaParticion> tablaParticiones,
			ObservableList<ElementoTablaProceso> tablaProcesos) {

		System.out.println("FCFS - Particiones Fijas - FirstFit\n");

		ArrayList<Particion> particiones = new ArrayList<Particion>();
		ArrayList<Proceso> procesos = new ArrayList<Proceso>();
		
		ArrayList<Proceso> nuevos = new ArrayList<Proceso>();
		ArrayList<Proceso> listos = new ArrayList<Proceso>();
		
		ArrayList<Proceso> ejecutandoCpu = new ArrayList<Proceso>();

		// Inicializamos mapaMemoria
		mapaMemoria = new ArrayList<ArrayList<Particion>>();

		// Inicializamos GanttCPU
		ganttCpuHM = new HashMap<Integer, Proceso>();

		// Cargamos la lista de Particiones
		for (ElementoTablaParticion p : tablaParticiones) {
			Particion particion = new Particion(p.getId(), p.getTamanio(), true);
			particiones.add(particion);
		}

		// Cargamos la lista de Procesos
		int tIrrupcion = 0; // Para controlar el bucle principal

		for (ElementoTablaProceso p : tablaProcesos) {
			Proceso proceso = new Proceso(p.getId(), p.getTamanio(), p.getTArribo(), p.getCpu1(), p.getEs1(), p.getCpu2(),
					p.getEs2(), p.getCpu3());
			procesos.add(proceso);
			tIrrupcion += proceso.getCpu1();
		}

		Collections.sort(procesos);
		int idUltimoProceso = procesos.get(procesos.size() - 1).getId();

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
			 * COLA EJECUTANDO CPU
			 * 
			 */
			if (!ejecutandoCpu.isEmpty()) {
				ejecutarCPU(particiones, procesos, ejecutandoCpu, tablaProcesos, t);
			}

			/*
			 * Tiempo ocioso
			 * 
			 */
			if (nuevos.isEmpty() && ejecutandoCpu.isEmpty() && !llegoElUltimo) {
				tOcioso++;
			}

			/*
			 * COLA DE LISTOS EN INSTANTE t
			 * 
			 */
			for (int i = 0; i < nuevos.size(); i++) {
				Proceso pNuevo = nuevos.get(i);
				for (Particion particion : particiones) {
					if (particion.getLibre() && pNuevo.getTamanio() <= particion.getTamanio()) {
						listos.add(pNuevo);
						particion.setProceso(pNuevo.getId());
						particion.setLibre(false);
						nuevos.remove(i);
						i--;// Para evitar ConcurrentModificationException
						break;
					}
				}
			}

			// Descuento 1 al primero de ejecucion y si termino lo saco
//			if (!ejecutandoCpu.isEmpty() && !ejecutado) {
//				ejecutarCPU(particiones, procesos, ejecutandoCpu, tablaProcesos, t);
//			}

			// Añado los listos a ejecucion
			for (int i = 0; i < listos.size(); i++) {
				Proceso pListo = listos.get(i);
				ejecutandoCpu.add(pListo);
				listos.remove(i);
				i--; // Para evitar ConcurrentModificationException
			}

			/*
			 * GUARDO EL ESTADO DE LAS PARTICIONES
			 * 
			 */
			mapaMemoria.add(t, new ArrayList<Particion>());
			for (Particion p : particiones) {
				Particion particion = new Particion(p.getId(), p.getTamanio(), p.getProceso(), p.getLibre());
				mapaMemoria.get(t).add(particion);
			}

			t++;

		} // Fin while

		System.out.println("Tiempo total: " + tIrrupcion);
		System.out.println("Tiempo ocioso: " + tOcioso);

	}

	/*
	 * EJECUTANDO CPU
	 * 
	 */
	private static void ejecutarCPU(ArrayList<Particion> particiones, ArrayList<Proceso> procesos,
			ArrayList<Proceso> ejecutandoCpu, ObservableList<ElementoTablaProceso> tablaProcesos, int t) {

		Proceso procesoActual = ejecutandoCpu.get(0);
		int cpu = procesoActual.getCpu1();
		cpu--;
		if (cpu == 0) {

			// Primero libero la particion
			for (Particion particion : particiones) {
				if (particion.getProceso() == procesoActual.getId()) {
					particion.setProceso(0);
					particion.setLibre(true);
					break;
				}
			}

			// Luego saco el proceso
			ejecutandoCpu.remove(0);

			/*
			 * Actualizo la cola GanttCPU
			 * 
			 */
			int procesoGantt = procesos.indexOf(procesoActual); // Obtengo el indice del proceso que termino

			ElementoTablaProceso elementoTablaProceso = tablaProcesos.get(procesoGantt);

			Proceso terminado = new Proceso(elementoTablaProceso.getId(), elementoTablaProceso.getTamanio(),
					elementoTablaProceso.getTArribo(), elementoTablaProceso.getCpu1(), elementoTablaProceso.getEs1(),
					elementoTablaProceso.getCpu2(), elementoTablaProceso.getEs2(), elementoTablaProceso.getCpu3());

			ganttCpuHM.put(t - terminado.getCpu1(), terminado);

		} else {
			// Si no termino actualizo el valor de CPU1
			procesoActual.setCpu1(cpu);
			ejecutandoCpu.remove(0);
			ejecutandoCpu.add(0, procesoActual);
		}

	}

	/*
	 * METODOS AUXILIARES
	 * 
	 */
	private static void imprimirParticiones(List<Particion> particiones) {
		for (Particion p : particiones) {
			System.out.println("ID: " + p.getId() + " Tamanio: " + p.getTamanio() + " Proceso: " + p.getProceso()
					+ " Libre: " + p.getLibre());
		}
		System.out.println();
	}

	private static void imprimirProcesos(List<Proceso> procesos) {
		for (Proceso p : procesos) {
			System.out.println("ID: " + p.getId() + " Tamanio: " + p.getTamanio() + " CPU: " + p.getCpu1() + " ES: "
					+ p.getEs1() + " TArribo: " + p.getTArribo());
		}
		System.out.println();
	}

}
