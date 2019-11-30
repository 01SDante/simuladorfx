package application.algoritmos.util;

import java.util.Comparator;

import application.model.Proceso;

public class OrdenarPorPrioridad implements Comparator<Proceso> {

	@Override
	public int compare(Proceso p1, Proceso p2) { // Ordena de menor a mayor
		if (p1.getPrioridad() == p2.getPrioridad())
			return 0;
		else if (p1.getPrioridad() > p2.getPrioridad())
			return 1;
		else
			return -1;
	}

}
