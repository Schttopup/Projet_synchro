package polytech.tours.di.parallel.tsp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		
		List<Callable<Solution>> runnables = new ArrayList<Callable<Solution>>();
		
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
					
					Solution s=algorithm.run(config);
					return s;
				}
			});
		}
		
		ExecutorService execute = Executors.newFixedThreadPool(10);
		for(Callable<Solution> r : runnables) {
			execute.submit(r);
		}
		execute.shutdown();
		
		
		
		
		
	}
}
