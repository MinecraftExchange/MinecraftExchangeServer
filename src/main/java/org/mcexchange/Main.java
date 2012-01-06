package org.mcexchange;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Set up output.
		Logger log = Logger.getLogger("exchange");
		System.setOut(new PrintStream(new LoggerOutputStream(log, Level.INFO), true));
		System.setErr(new PrintStream(new LoggerOutputStream(log, Level.SEVERE), true));
		//Create server.
		ExchangeServer es = ExchangeServer.getInstance();
		es.loadProperties(args);
		Thread t = new Thread(es);
		t.start();
		//Listen for input.
		Scanner scan = new Scanner(System.in);
		while(true) {
			String s = scan.nextLine();
			if(s.equalsIgnoreCase("stop")) {//better option here one of these days. ;)
				shutdown(t, es);
			}
		}
	}
	
	/**
	 * Ends all threads and exits the program safely.
	 * @param t
	 * @param es
	 */
	public static void shutdown(Thread t, ExchangeServer es) {
		System.out.println("Disconnecting from clients.");
		for(Thread thread : es.getConnections()) {
			thread.interrupt();
		}
		System.out.println("Shutting down server.");
		t.interrupt();
		System.exit(0);
	}

}
