package org.mcexchange;

import java.util.Scanner;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExchangeServer es = ExchangeServer.getInstance();
		es.loadProperties(args);
		Thread t = new Thread(es);
		t.start();
		Scanner scan = new Scanner(System.in);
		while(true) {
			String s = scan.nextLine();
			if(s.equalsIgnoreCase("stop")) {
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
		for(Thread thread : es.connections) {
			thread.interrupt();
		}
		System.out.println("Shutting down server.");
		t.interrupt();
		System.exit(0);
	}

}
