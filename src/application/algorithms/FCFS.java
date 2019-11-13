package application.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import application.algorithms.model.ParticionAlgoritmo;
import application.algorithms.model.ProcesoAlgoritmo;
import application.model.Particion;
import application.model.Proceso;
import javafx.collections.ObservableList;

public class FCFS {

	public static ArrayList<ProcesoAlgoritmo> ganttCpu;
	
	public static ArrayList<ArrayList<ParticionAlgoritmo>> mapaMemoria;
	
	/*
	 * Devuelve la lista de procesos para armar el Gantt
	 */
	public static ArrayList<ProcesoAlgoritmo> FCFSGantt() {
		return ganttCpu;
	}

	/*
	 * Devuelve el estado de las particiones para armar el mapa
	 */
	public static ArrayList<ArrayList<ParticionAlgoritmo>> FCFSMapaMemoria(){
		return mapaMemoria;
	}

	/*
	 * EJECUTAR
	 * 
	 */
	public static void ejecutar(ObservableList<Particion> tablaParticiones, ObservableList<Proceso> tablaProcesos) {

		System.out.println("FCFS - Particiones Fijas - FirstFit\n");

		ArrayList<ParticionAlgoritmo> particiones = new ArrayList<ParticionAlgoritmo>();
		ArrayList<ProcesoAlgoritmo> procesos = new ArrayList<ProcesoAlgoritmo>();
		ArrayList<ProcesoAlgoritmo> nuevos = new ArrayList<ProcesoAlgoritmo>();
		ArrayList<ProcesoAlgoritmo> listos = new ArrayList<ProcesoAlgoritmo>();
		ArrayList<ProcesoAlgoritmo> ejecutandoCpu1 = new ArrayList<ProcesoAlgoritmo>();

		// Inicializamos GanttCPU
		ganttCpu = new ArrayList<ProcesoAlgoritmo>();

		// Inicializamos mapaMemoria
		mapaMemoria = new ArrayList<ArrayList<ParticionAlgoritmo>>();

		// CARGAMOS LAS LISTAS PARTICIONES Y PROCESOS
		for (Particion p : tablaParticiones) {
			ParticionAlgoritmo pa = new ParticionAlgoritmo(p.getId(), p.getTamanio(), true);
			particiones.add(pa);
		}

		int tIrrupcion = 0; // Para controlar el bucle principal
		for (Proceso p : tablaProcesos) {
			ProcesoAlgoritmo pa = new ProcesoAlgoritmo(p.getId(), p.getTamanio(), p.getCpu(), p.getEs(), p.getCpu(),
					p.getTArribo());
			procesos.add(pa);
			tIrrupcion += pa.getCpu1();
		}

		// Imprimimos
//		System.out.println("Particiones:");
//		imprimirParticiones(particiones);
//		System.out.println("Procesos:");
//		imprimirProcesos(procesos);
//
//		System.out.println("**********************************************************");

		int t = 0; // Reloj
		boolean ejecutado = false;

		while (t <= tIrrupcion) {

//			System.out.println("Estado particiones en t=" + t + ":");
//			imprimirParticiones(particiones);

			/*
			 * COLA DE NUEVOS
			 * 
			 */
			for (ProcesoAlgoritmo p : procesos) {
				if (p.getTArribo() == t)
					nuevos.add(p);
			}
//			System.out.println("COLA NUEVOS");
//			System.out.println("--------------------");
//			System.out.println("Cola Nuevos en t=" + t + ": ");
//			imprimirProcesos(nuevos);
//			System.out.println("Cola Listos en t=" + t + ":");
//			imprimirProcesos(listos);
//			System.out.println("Cola ejecutando CPU1 en t=" + t + ":");
//			imprimirProcesos(ejecutandoCpu1);

			/*
			 * SI HAY QUE EJECUTAR, EJECUTO ANTES PARA LIBERAR UNA POSIBLE PARTICION
			 * 
			 */
			if (!ejecutandoCpu1.isEmpty()) {
				ejecutado = true; // Para no descontar 2 veces en un mismo t
				ProcesoAlgoritmo procesoActual = ejecutandoCpu1.get(0);
				int cpu = procesoActual.getCpu1();
				cpu--;
				if (cpu == 0) {
					// Primero libero la particion
					for (ParticionAlgoritmo particion : particiones) {
						if (particion.getProceso() == procesoActual.getId()) {
							particion.setProceso(0);
							particion.setLibre(true);
							break;
						}
					}
					// Luego saco el proceso
					ejecutandoCpu1.remove(0);
					/*
					 * Actualizo la cola GanttCPU
					 * 
					 */
					int procesoGantt = procesos.indexOf(procesoActual);
					Proceso proceso = tablaProcesos.get(procesoGantt);
					ProcesoAlgoritmo terminado = new ProcesoAlgoritmo(proceso.getId(), proceso.getTamanio(),
							proceso.getCpu(), proceso.getEs(), proceso.getCpu(), proceso.getTArribo());
					ganttCpu.add(terminado);
				} else {
					// Si no termino actualizo el valor de CPU1
					procesoActual.setCpu1(cpu);
					ejecutandoCpu1.remove(0);
					ejecutandoCpu1.add(0, procesoActual);
				}
			}

			/*
			 * COLA DE LISTOS
			 * 
			 */
			for (int i = 0; i < nuevos.size(); i++) {
				ProcesoAlgoritmo pNuevo = nuevos.get(i);
				for (ParticionAlgoritmo particion : particiones) {
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
			
//			System.out.println("COLA LISTOS");
//			System.out.println("--------------------");
//			System.out.println("Cola Nuevos en t=" + t + ": ");
//			imprimirProcesos(nuevos);
//			System.out.println("Cola Listos en t=" + t + ":");
//			imprimirProcesos(listos);
//			System.out.println("Cola ejecutando CPU1 en t=" + t + ":");
//			imprimirProcesos(ejecutandoCpu1);

			/*
			 * COLA EJECUTANDO CPU1
			 * 
			 */

			// Descuento 1 al primero de ejecucion y si termino lo saco
			if (!ejecutandoCpu1.isEmpty() && !ejecutado) {
				ProcesoAlgoritmo procesoActual = ejecutandoCpu1.get(0);
				int cpu = procesoActual.getCpu1();
				cpu--;
				if (cpu == 0) {
					// Primero libero la particion
					for (ParticionAlgoritmo particion : particiones) {
						if (particion.getProceso() == procesoActual.getId()) {
							particion.setProceso(0);
							particion.setLibre(true);
							break;
						}
					}
					// Luego saco el proceso
					ejecutandoCpu1.remove(0);
					/*
					 * Actualizo la cola GanttCPU
					 * 
					 */
					int procesoGantt = procesos.indexOf(procesoActual);
					Proceso proceso = tablaProcesos.get(procesoGantt);
					ProcesoAlgoritmo terminado = new ProcesoAlgoritmo(proceso.getId(), proceso.getTamanio(),
							proceso.getCpu(), proceso.getEs(), proceso.getCpu(), proceso.getTArribo());
					ganttCpu.add(terminado);
				} else {
					// Si no termino actualizo el valor de CPU1
					procesoActual.setCpu1(cpu);
					ejecutandoCpu1.remove(0);
					ejecutandoCpu1.add(0, procesoActual);
				}
			}

			// Añado los listos a ejecucion
			for (int i = 0; i < listos.size(); i++) {
				ProcesoAlgoritmo pListo = listos.get(i);
				ejecutandoCpu1.add(pListo);
				listos.remove(i);
				i--; // Para evitar ConcurrentModificationException
			}
			
			/*
			 * GUARDO EL ESTADO DE LAS PARTICIONES
			 * 
			 */
			mapaMemoria.add(t, new ArrayList<ParticionAlgoritmo>());
			for (ParticionAlgoritmo p: particiones) {
				ParticionAlgoritmo particion = new ParticionAlgoritmo(p.getId(), p.getTamanio(), p.getProceso(), p.getLibre());
				mapaMemoria.get(t).add(particion);
			}
			
//			System.out.println("PROCESOS EJECUTANDO");
//			System.out.println("--------------------");
//			System.out.println("Cola Nuevos en t=" + t + ": ");
//			imprimirProcesos(nuevos);
//			System.out.println("Cola Listos en t=" + t + ":");
//			imprimirProcesos(listos);
//			System.out.println("Cola ejecutando CPU1 en t=" + t + ":");
//			imprimirProcesos(ejecutandoCpu1);
//			System.out.println("Estado particiones en t=" + t + ":");
//			imprimirParticiones(particiones);

			ejecutado = false;
			t++;
//			System.out.println("**********************************************************");

		} // Fin while
		
	} // FIN FCFS

	/*
	 * METODOS AUXILIARES
	 * 
	 */
	private static void imprimirParticiones(List<ParticionAlgoritmo> particiones) {
		for (ParticionAlgoritmo p : particiones) {
			System.out.println("ID: " + p.getId() + " Tamanio: " + p.getTamanio() + " Proceso: " + p.getProceso()
					+ " Libre: " + p.getLibre());
		}
		System.out.println();
	}

	private static void imprimirProcesos(List<ProcesoAlgoritmo> procesos) {
		for (ProcesoAlgoritmo p : procesos) {
			System.out.println("ID: " + p.getId() + " Tamanio: " + p.getTamanio() + " CPU: " + p.getCpu1() + " ES: "
					+ p.getEs() + " TArribo: " + p.getTArribo());
		}
		System.out.println();
	}

}
