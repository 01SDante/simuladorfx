package app.algoritmos.util;

import java.util.Comparator;

import app.modelo.Proceso;

public class OrdenarPorCPU1CPU2 implements Comparator<Proceso> {
	
	@Override
	public int compare(Proceso p1, Proceso p2) {
		
		int cpuP1 = 0, cpuP2 = 0;
		
		if (p1.getCpu1() > 0)
			cpuP1 = p1.getCpu1();
		else if (p1.getCpu2() > 0)
			cpuP1 = p1.getCpu2();
		
		if (p2.getCpu1() > 0)
			cpuP2 = p2.getCpu1();
		else if (p2.getCpu2() > 0)
			cpuP2 = p2.getCpu2();
		
		if (cpuP1 == cpuP2)
			return 0;
		else if (cpuP1 > cpuP2)
			return 1;
		else
			return -1;
	}

}
