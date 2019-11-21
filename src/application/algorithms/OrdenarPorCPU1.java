package application.algorithms;

import java.util.Comparator;

import application.model.Proceso;

public class OrdenarPorCPU1 implements Comparator<Proceso> {

	@Override
	public int compare(Proceso p1, Proceso p2) {
		if (p1.getCpu1() == p2.getCpu1())
			return 0;
		else if (p1.getCpu1() > p2.getCpu1())
			return 1;
		else
			return -1;
	}

}
