package application.model;

public class Proceso {
	private int id;
	private int tamanio;
	private int tArribo;
	private int cpu1;
	private int es1;
	private int cpu2;
	private int es2;
	private int cpu3;
	private int prioridad;

	/*
	 * CONSTRUCTOR
	 * 
	 */
	public Proceso(int id, int tamanio, int tArribo, int cpu1, int es1, int cpu2, int es2, int cpu3, int prioridad) {
		this.id = id;
		this.tamanio = tamanio;
		this.tArribo = tArribo;
		this.cpu1 = cpu1;
		this.es1 = es1;
		this.cpu2 = cpu2;
		this.es2 = es2;
		this.cpu3 = cpu3;
		this.prioridad = prioridad;
	}

	/*
	 * GETTERS SETTERS
	 * 
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTamanio() {
		return tamanio;
	}

	public void setTamanio(int tamanio) {
		this.tamanio = tamanio;
	}

	public int getTArribo() {
		return tArribo;
	}

	public void setTArribo(int tArribo) {
		this.tArribo = tArribo;
	}

	public int getCpu1() {
		return cpu1;
	}

	public void setCpu1(int cpu1) {
		this.cpu1 = cpu1;
	}

	public int getEs1() {
		return es1;
	}

	public void setEs1(int es1) {
		this.es1 = es1;
	}

	public int getCpu2() {
		return cpu2;
	}

	public void setCpu2(int cpu2) {
		this.cpu2 = cpu2;
	}
	
	public int getEs2() {
		return es2;
	}

	public void setEs2(int es2) {
		this.es2 = es2;
	}

	public int getCpu3() {
		return cpu3;
	}

	public void setCpu3(int cpu3) {
		this.cpu3 = cpu3;
	}
	
	public int getPrioridad() {
		return prioridad;
	}
	
	public void setPrioridad(int prioridad) {
		this.prioridad = prioridad;
	}

}
