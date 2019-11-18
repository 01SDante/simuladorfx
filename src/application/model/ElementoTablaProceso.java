package application.model;

import javafx.beans.property.SimpleIntegerProperty;

public class ElementoTablaProceso {

	private SimpleIntegerProperty id;
	private SimpleIntegerProperty tamanio;
	private SimpleIntegerProperty cpu;
	private SimpleIntegerProperty es;
	private SimpleIntegerProperty tArribo;

	public ElementoTablaProceso(int id, int tamanio, int cpu, int es, int tArribo) {
		this.id = new SimpleIntegerProperty(id);
		this.tamanio = new SimpleIntegerProperty(tamanio);
		this.cpu = new SimpleIntegerProperty(cpu);
		this.es = new SimpleIntegerProperty(es);
		this.tArribo = new SimpleIntegerProperty(tArribo);
	}

	public int getId() {
		return id.get();
	}

	public int getTamanio() {
		return tamanio.get();
	}

	public int getCpu() {
		return cpu.get();
	}

	public int getEs() {
		return es.get();
	}

	public int getTArribo() {
		return tArribo.get();
	}

}
