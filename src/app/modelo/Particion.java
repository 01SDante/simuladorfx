package app.modelo;

public class Particion {

	private int id;
	private int tamanio;
	private int proceso;
	private boolean libre;

	// CONSTRUCTORES
	public Particion(int id, int tamanio, boolean libre) {
		this.id = id;
		this.tamanio = tamanio;
		this.libre = libre;
	}

	public Particion(int id, int tamanio, int proceso, boolean libre) {
		this.id = id;
		this.tamanio = tamanio;
		this.proceso = proceso;
		this.libre = libre;
	}

	// GETTERS SETTERS
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

	public int getProceso() {
		return proceso;
	}

	public void setProceso(int proceso) {
		this.proceso = proceso;
	}

	public boolean isLibre() {
		return libre;
	}

	public void setLibre(boolean libre) {
		this.libre = libre;
	}

}
