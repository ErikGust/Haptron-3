package haptron.engine;

import java.util.Date;

public class Console {
	@SafeVarargs
	public synchronized static <T> void print(T...elements) {
		for(T element: elements) {
			System.out.print(element);
		}
	}
	@SafeVarargs
	public synchronized static <T> void println(T...elements) {
		print(elements);
		print(System.lineSeparator());
	}
	@SafeVarargs
	public synchronized static <T> void log(T...elements) {
		print("[" + new Date() + ":" + Thread.currentThread().getName() + "] ");
		println(elements);
	}
	
	public synchronized static void logProperty(String p) {
		log("System.getProperty(\"" + p + "\") = " + System.getProperty(p));
	}
	
	public synchronized static void error(Throwable e) {
		log("An error has occured!");
		e.printStackTrace();
	}
}
