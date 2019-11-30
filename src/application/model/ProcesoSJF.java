package application.model;

public class ProcesoSJF extends Proceso {

	private boolean estaEjecutando;

	// CONSTRUCTOR
	public ProcesoSJF(int id, int tamanio, int tArribo, int cpu1, int es1, int cpu2, int es2, int cpu3, int prioridad) {
		super(id, tamanio, tArribo, cpu1, es1, cpu2, es2, cpu3, prioridad);
		this.estaEjecutando = false;
	}

	// GETTERS SETTERS
	public boolean getEstaEjecutando() {
		return estaEjecutando;
	}

	public void setEstaEjecutando(boolean estaEjecutando) {
		this.estaEjecutando = estaEjecutando;
	}

}
