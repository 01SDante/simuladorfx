package application.algoritmos.util;

import java.util.Comparator;

import application.model.ParticionVariable;

public class OrdenarPorDirInicio implements Comparator<ParticionVariable> {

	@Override
	public int compare(ParticionVariable p1, ParticionVariable p2) {
		if (p1.getDirInicio() == p2.getDirInicio())
			return 0;
		else if (p1.getDirInicio() > p2.getDirInicio())
			return 1;
		else
			return -1;
	}

}
