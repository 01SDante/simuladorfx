package application.model;

public class ParticionVariable {

	private int dirInicio;
	private int dirFin;
	private int proceso;
	private boolean libre;

	// CONSTRUCTORES
	public ParticionVariable(int dirInicio, int dirFin, boolean libre) {
		this.dirInicio = dirInicio;
		this.dirFin = dirFin;
		this.libre = libre;
	}

	public ParticionVariable(int dirInicio, int dirFin, int proceso, boolean libre) {
		this.dirInicio = dirInicio;
		this.dirFin = dirFin;
		this.proceso = proceso;
		this.libre = libre;
	}

	// GETTERS SETTERS
	public int getDirInicio() {
		return dirInicio;
	}

	public void setDirInicio(int dirInicio) {
		this.dirInicio = dirInicio;
	}

	public int getDirFin() {
		return dirFin;
	}

	public void setDirFin(int dirFin) {
		this.dirFin = dirFin;
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
