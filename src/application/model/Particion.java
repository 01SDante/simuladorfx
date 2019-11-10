package application.model;

import javafx.beans.property.SimpleIntegerProperty;

public class Particion {

	private SimpleIntegerProperty id;
	private SimpleIntegerProperty tamanio;
	private SimpleIntegerProperty dirInicio;
	private SimpleIntegerProperty dirFin;

	public Particion(int id, int tamanio, int dirInicio, int dirFin) {
		this.id = new SimpleIntegerProperty(id);
		this.tamanio = new SimpleIntegerProperty(tamanio);
		this.dirInicio = new SimpleIntegerProperty(dirInicio);
		this.dirFin = new SimpleIntegerProperty(dirFin);
	}

	public int getId() {
		return id.get();
	}

	public int getTamanio() {
		return tamanio.get();
	}

	public int getDirInicio() {
		return dirInicio.get();
	}

	public int getDirFin() {
		return dirFin.get();
	}

}
