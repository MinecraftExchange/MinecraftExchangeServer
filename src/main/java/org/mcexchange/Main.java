package org.mcexchange;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExchangeServer es = ExchangeServer.getInstance();
		es.loadProperties(args);
		new Thread(es).start();
	}

}
