package application.algoritmos.fcfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.gantt.GanttChart.ExtraData;
import application.model.ElementoTablaParticion;
import application.model.ElementoTablaProceso;
import application.model.Particion;
import application.model.Proceso;
import application.model.ProcesoGantt;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class FCFS {

	public static ArrayList<ArrayList<Particion>> mapaMemoria;

	public static HashMap<Integer, Proceso> ganttCpuHM;
	
	public static HashMap<Integer, Proceso> ganttEsHM;
	
	public static ArrayList<ProcesoGantt> ganttCpu; //nuevo
	
	public static ArrayList<ProcesoGantt> ganttEs; //nuevo

	/*
	 * Devuelve el estado de las particiones para armar el mapa
	 */
	public static ArrayList<ArrayList<Particion>> FCFSMapaMemoria() {
		return mapaMemoria;
	}

	/*
	 * Devuelve la lista de procesos para armar el Gantt
	 */
	public static HashMap<Integer, Proceso> FCFSGanttCpuHM() {
		return ganttCpuHM;
	}
	
	public static HashMap<Integer, Proceso> FCFSGanttEsHM(){
		return ganttEsHM;
	}
	
	public static ArrayList<ProcesoGantt> FCFSGanttCPU(){ //nuevo
		return ganttCpu;
	}
	
	public static ArrayList<ProcesoGantt> FCFSGanttES(){ //nuevo
		return ganttEs;
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
		ArrayList<Proceso> ejecutandoEs = new ArrayList<Proceso>();

		// Inicializamos mapaMemoria
		mapaMemoria = new ArrayList<ArrayList<Particion>>();

		// Inicializamos los Gantt
		ganttCpuHM = new HashMap<Integer, Proceso>();
		ganttEsHM = new HashMap<Integer, Proceso>();
		ganttCpu = new ArrayList<ProcesoGantt>();
		ganttEs = new ArrayList<ProcesoGantt>();

		// Cargamos la lista de Particiones
		for (ElementoTablaParticion p : tablaParticiones) {
			Particion particion = new Particion(p.getId(), p.getTamanio(), true);
			particiones.add(particion);
		}

		// Cargamos la lista de Procesos
		int tIrrupcion = 0; // Para controlar el bucle principal

		for (ElementoTablaProceso p : tablaProcesos) {
			Proceso proceso = new Proceso(p.getId(), p.getTamanio(), p.getTArribo(), p.getCpu1(), p.getEs1(), p.getCpu2(),
					p.getEs2(), p.getCpu3(), p.getPrioridad());
			procesos.add(proceso);
			tIrrupcion += proceso.getCpu1() + proceso.getCpu2() + proceso.getCpu3();
		}

		//Collections.sort(procesos);
		int idUltimoProceso = procesos.get(procesos.size() - 1).getId();

		/*
		 * ARRANCA EL ALGORITMO
		 * 
		 */

		int t = 0; // Reloj
		int tOcioso = 0;
		boolean llegoElUltimo = false;

		while (t <= tIrrupcion + tOcioso) { //tIrrupcion + tOcioso

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
			} /*else {
				ganttEs.add(new ProcesoGantt(0, t));
			}*/

			/*
			 * COLA EJECUTANDO CPU
			 * 
			 */
			if (!ejecutandoCpu.isEmpty()) {
				ejecutarCPU(particiones, procesos, ejecutandoCpu, ejecutandoEs, tablaProcesos, t);
			}/* else {
				
			}*/

			/*
			 * Tiempo ocioso
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

			// Descuento 1 al primero de ejecucion y si termino lo saco
//			if (!ejecutandoCpu.isEmpty() && !ejecutado) {
//				ejecutarCPU(particiones, procesos, ejecutandoCpu, tablaProcesos, t);
//			}

			/*
			 *  AGREGO LOS LISTOS A EJECUCION
			 * 
			 */
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
				Particion particion = new Particion(p.getId(), p.getTamanio(), p.getProceso(), p.isLibre());
				mapaMemoria.get(t).add(particion);
			}

//			ejecutado = false;
			t++;

		} // Fin while

		System.out.println("Tiempo total: " + tIrrupcion);
		System.out.println("Tiempo ocioso: " + tOcioso);
		System.out.println("Gantt CPU");
		imprimirGantt(ganttCpu);
		System.out.println("GanttES");
		imprimirGantt(ganttEs);

	}

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
				
				// NUEVO actualizo gantt
				ganttCpu.add(new ProcesoGantt(procesoActual.getId(), t - 1));
				
				if (ejecutandoEs.isEmpty()) {
					ganttEs.add(new ProcesoGantt(0, t));
				}
				
				if (cpu == 0) {

					// Primero libero la particion
//					for (Particion particion : particiones) {
//						if (particion.getProceso() == procesoActual.getId()) {
//							particion.setProceso(0);
//							particion.setLibre(true);
//							break;
//						}
//					}
					
					// Actualizo cpu1 para ponerla en 0
					procesoActual.setCpu1(cpu);
					ejecutandoCpu.remove(0);
					ejecutandoCpu.add(0, procesoActual);
					
					// Luego lo saco y lo paso a ES
					ejecutandoEs.add(ejecutandoCpu.get(0));
					ejecutandoCpu.remove(0);

				} else {
					// Si no termino actualizo el valor de CPU1
					procesoActual.setCpu1(cpu);
					ejecutandoCpu.remove(0);
					ejecutandoCpu.add(0, procesoActual);
				}
				
			} else if (procesoActual.getCpu2() > 0 && procesoActual.getEs1() == 0) { // Trato CPU2
				
				int cpu = procesoActual.getCpu2();
				cpu--;
				
				// NUEVO actualizo gantt
				ganttCpu.add(new ProcesoGantt(procesoActual.getId(), t - 1));
				
				if (ejecutandoEs.isEmpty()) {
					ganttEs.add(new ProcesoGantt(0, t));
				}
				
				if (cpu == 0) {

					// Primero libero la particion
					for (Particion particion : particiones) {
						if (particion.getProceso() == procesoActual.getId()) {
							particion.setProceso(0);
							particion.setLibre(true);
							break;
						}
					}
					
					// Actualizo cpu1 para ponerla en 0
					procesoActual.setCpu2(cpu);
					ejecutandoCpu.remove(0);
					ejecutandoCpu.add(0, procesoActual);
					
					// Luego lo saco
					ejecutandoCpu.remove(0);

				} else {
					// Si no termino actualizo el valor de CPU2
					procesoActual.setCpu2(cpu);
					ejecutandoCpu.remove(0);
					ejecutandoCpu.add(0, procesoActual);
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
		int es = procesoActual.getEs1();
		es--;
		
		// NUEVO actualizo gantt
		ganttEs.add(new ProcesoGantt(procesoActual.getId(), t - 1));
		
		if (ejecutandoCpu.isEmpty()) {
			ganttEs.add(new ProcesoGantt(0, t));
		}
		
		if (ejecutandoCpu.isEmpty()) {
			ganttCpu.add(new ProcesoGantt(0, t - 1));
		}
		
		if (es == 0) {

			// Libero la particion
//			for (Particion particion : particiones) {
//				if (particion.getProceso() == procesoActual.getId()) {
//					particion.setProceso(0);
//					particion.setLibre(true);
//					break;
//				}
//			}
			
			// Actualizo es1 para ponerla en 0
			procesoActual.setEs1(es);
			ejecutandoEs.remove(0);
			ejecutandoEs.add(0, procesoActual);
			
			ejecutandoCpu.add(ejecutandoEs.get(0));
			ejecutandoEs.remove(0);

			/*
			 * Actualizo la cola GanttEs
			 * 
			 */
//			int procesoGantt = procesos.indexOf(procesoActual); // Obtengo el indice del proceso que termino
//
//			ElementoTablaProceso elementoTablaProceso = tablaProcesos.get(procesoGantt);
//
//			Proceso terminado = new Proceso(elementoTablaProceso.getId(), elementoTablaProceso.getTamanio(),
//					elementoTablaProceso.getTArribo(), elementoTablaProceso.getCpu1(), elementoTablaProceso.getEs1(),
//					elementoTablaProceso.getCpu2(), elementoTablaProceso.getEs2(), elementoTablaProceso.getCpu3());
//
//			ganttEsHM.put(t - terminado.getEs1(), terminado);

		} else {
			// Si no termino actualizo el valor de ES1
			procesoActual.setEs1(es);
			ejecutandoEs.remove(0);
			ejecutandoEs.add(0, procesoActual);
		}

	}

	/*
	 * METODOS AUXILIARES
	 * 
	 */
	private static void imprimirParticiones(List<Particion> particiones) {
		for (Particion p : particiones) {
			System.out.println("ID: " + p.getId() + " Tamanio: " + p.getTamanio() + " Proceso: " + p.getProceso()
					+ " Libre: " + p.isLibre());
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
	
	private static void imprimirGantt(ArrayList<ProcesoGantt> gantt) {
		for (int i = 0; i < gantt.size(); i++) {
			System.out.println("en " + i + " esta el proceso: " + gantt.get(i).getId());
		}
	}

}
