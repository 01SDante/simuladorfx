package application.algorithms.model;

public class ParticionAlgoritmo {

	int id;
	int tamanio;
	int proceso;
	boolean libre;

	public ParticionAlgoritmo(int id, int tamanio, boolean estado) {
		this.id = id;
		this.tamanio = tamanio;
		this.libre = estado;
	}

	public ParticionAlgoritmo(int id, int tamanio, int proceso, boolean libre) {
		this.id = id;
		this.tamanio = tamanio;
		this.proceso = proceso;
		this.libre = libre;
	}

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

	public boolean getLibre() {
		return libre;
	}

	public void setLibre(boolean estado) {
		this.libre = estado;
	}

}
