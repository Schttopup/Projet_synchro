package polytech.tours.di.parallel.tsp.example;

import java.util.ArrayList;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;

public class MyCalculator {

	private ArrayList<Integer> s;
	private double[][] distMatrix;
	
	public MyCalculator() {
		
	}
	
	/**
	 * Computes the objective function of a TSP tour
	 * @param instance the instance data
	 * @param s the solution
	 * @return the objective function of solution <code>s</code>
	 */
	public double calcOF(Instance instance, Solution s){
		distMatrix=instance.getDistanceMatrix();
		this.s=s;
		return calc();
	}
	/**
	 * static access to the calculator
	 * @param matrix the distance matrix
	 * @param s the TSP solution (permutation)
	 * @return the cost of <code>s</code>
	 */
	public double calcOF(double[][] matrix, ArrayList<Integer> s){
		distMatrix=matrix;
		this.s=s;
		return calc();
	}
	/**
	 * internal implementation of the calculator
	 * @return the cost of a TSP solution
	 */
	private double calc(){
		double cost=0;
		for(int i=1;i<s.size();i++){
			cost=cost+distMatrix[s.get(i-1)][s.get(i)];
		}
		cost=cost+distMatrix[s.get(s.size()-1)][s.get(0)];
		return cost;
	}
}
