package polytech.tours.di.parallel.tsp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Launches the optimization algorithm
 * @author Jorge E. Mendoza (dev@jorge-mendoza.com)
 * @version %I%, %G%
 *
 */
public class Launcher {
	
	private static Properties config;
	
	/**
	 * 
	 * @param args[0] the file (path included) with he configuration settings
	 */
	public static void main(String[] args) {
		//read properties
		config=new Properties();
		try {
			config.loadFromXML(new FileInputStream(args[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		double max_cpu=Double.valueOf(config.getProperty("maxcpu"));

		Random rnd=new Random(Long.valueOf(config.getProperty("seed")));

		List<Callable<Solution>> runnables = new ArrayList<Callable<Solution>>();
		List<Future<Solution>> futures = new ArrayList<Future<Solution>>();
		
		for(int i = 0; i < 10; i++) {
			runnables.add(new Callable<Solution>() {
				public Solution call() {
					Algorithm algorithm=null;
					try {
						Class<?> c = Class.forName(config.getProperty("algorithm"));
						algorithm=(Algorithm)c.newInstance();
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
						e.printStackTrace();
					}
					
					Solution s=algorithm.run(config, rnd);
					return s;
				}
			});
		}
		
		System.out.println("Stating");
		ExecutorService execute = Executors.newFixedThreadPool(10);
		for(Callable<Solution> r : runnables) {
			futures.add(execute.submit(r));
		}
		execute.shutdown();
		try {
			execute.awaitTermination((long) (2*max_cpu), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
		Solution s = null, best = null;
		for(int i = 0; i < futures.size(); i++) {
			try {
				s = (Solution) futures.get(i).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println("Solution " + i + " = " + s.getOF());
			if(best == null) { 
				best = s;
			}
			else if(s.getOF() < best.getOF()) {
				best = s;
			}
		}
		System.out.println("Best solution = " + best.toString());
	}
}
