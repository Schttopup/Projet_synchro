package polytech.tours.di.parallel.tsp.example;

import java.util.Collections;
import java.util.Properties;
import java.util.Random;

import polytech.tours.di.parallel.tsp.Algorithm;
import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.InstanceReader;
import polytech.tours.di.parallel.tsp.Solution;

public class MyAlgorithm implements Algorithm {

	@Override
	public Solution run(Properties config) {
		MyCalculator calc = new MyCalculator();
		
		InstanceReader ir=new InstanceReader();
		ir.buildInstance(config.getProperty("instance"));
		Instance instance=ir.getInstance();
		double max_cpu=Double.valueOf(config.getProperty("maxcpu"));

		Random rnd=new Random(Long.valueOf(config.getProperty("seed")));
		Solution s=new Solution();
		Solution s2;
		Solution best=null;
		for(int i=0; i<instance.getN(); i++){
			s.add(i);
		}
		
		

		long startTime=System.currentTimeMillis();
		int iterations = 0;
		while((System.currentTimeMillis()-startTime)<=max_cpu){
			s.setOF(calc.calcOF(instance, s));
			Collections.shuffle(s,rnd);
			s2 = localSearch(s, instance);
			s2.setOF(calc.calcOF(instance, s2));
			if(best == null)
				best = s2;
			else if(s2.getOF() < best.getOF()) {
				best = s2;
			}
			iterations++;
		}
		//return the solution
		System.out.println(iterations);
		return best;
		
		
	}
	
	private static Solution localSearch(Solution s, Instance inst) {
		MyCalculator calc = new MyCalculator();
		boolean cont = true;
		Solution search = s.clone();
		Solution temp;
		while(cont){
			temp = exploreNeighborhood(search, inst);
			if(calc.calcOF(inst, temp) < calc.calcOF(inst, search))
				search = temp;
			else
				cont = false;
		}
		return search;
	}
	
	private static Solution exploreNeighborhood(Solution s, Instance inst) {
		MyCalculator calc = new MyCalculator();
		Solution temp1 = s.clone();
		Solution temp2;
		for(int i = 0; i < temp1.size(); i++) {
			for(int j = 0; j < temp1.size(); j++) {
				temp2 = temp1.clone();
				temp2.swap(i, j);
				if(calc.calcOF(inst, temp2) < calc.calcOF(inst, temp1))
					temp1 = temp2;
			}
		}
		return temp1;
	}
	
}
