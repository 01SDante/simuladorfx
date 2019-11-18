package application.model;

public class Proceso implements Comparable<Proceso> {
	int id;
	int tamanio;
	int cpu1;
	int es;
	int cpu2;
	int tArribo;

	public Proceso(int id, int tamanio, int cpu1, int es, int cpu2, int tArribo) {
		this.id = id;
		this.tamanio = tamanio;
		this.cpu1 = cpu1;
		this.es = es;
		this.cpu2 = cpu2;
		this.tArribo = tArribo;
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

	public int getCpu1() {
		return cpu1;
	}

	public void setCpu1(int cpu1) {
		this.cpu1 = cpu1;
	}

	public int getEs() {
		return es;
	}

	public void setEs(int es) {
		this.es = es;
	}

	public int getCpu2() {
		return cpu2;
	}

	public void setCpu2(int cpu2) {
		this.cpu2 = cpu2;
	}

	public int getTArribo() {
		return tArribo;
	}

	public void setTArribo(int tArribo) {
		this.tArribo = tArribo;
	}

	@Override
	public int compareTo(Proceso o) {
		if (tArribo == o.tArribo)
			return 0;
		else if (tArribo > o.tArribo)
			return 1;
		else return -1;
	}

}
