package application.model;

import javafx.beans.property.SimpleIntegerProperty;

public class ElementoTablaProceso {

	private SimpleIntegerProperty id;
	private SimpleIntegerProperty tamanio;
	private SimpleIntegerProperty tArribo;
	private SimpleIntegerProperty cpu1;
	private SimpleIntegerProperty es1;
	private SimpleIntegerProperty cpu2;
	private SimpleIntegerProperty es2;
	private SimpleIntegerProperty cpu3;

	public ElementoTablaProceso(int id, int tamanio, int tArribo, int cpu1, int es1, int cpu2, int es2, int cpu3) {
		this.id = new SimpleIntegerProperty(id);
		this.tamanio = new SimpleIntegerProperty(tamanio);
		this.tArribo = new SimpleIntegerProperty(tArribo);
		this.cpu1 = new SimpleIntegerProperty(cpu1);
		this.es1 = new SimpleIntegerProperty(es1);
		this.cpu2 = new SimpleIntegerProperty(cpu2);
		this.es2 = new SimpleIntegerProperty(es2);
		this.cpu3 = new SimpleIntegerProperty(cpu3);
	}

	public int getId() {
		return id.get();
	}

	public int getTamanio() {
		return tamanio.get();
	}

	public int getTArribo() {
		return tArribo.get();
	}

	public int getCpu1() {
		return cpu1.get();
	}

	public int getEs1() {
		return es1.get();
	}

	public int getCpu2() {
		return cpu2.get();
	}

	public int getEs2() {
		return es2.get();
	}

	public int getCpu3() {
		return cpu3.get();
	}

}
