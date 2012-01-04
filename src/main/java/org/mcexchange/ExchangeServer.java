package org.mcexchange;

public class ExchangeServer implements Runnable {
	//the one instance of ExchangeServer
	private static final ExchangeServer es = new ExchangeServer();
	
	/**
	 * Gets the singleton instance of ExchangeServer.
	 */
	public static ExchangeServer getInstance() {
		return es;
	}
	
	private ServerProperties sp;
	
	//private constructor ensuring that es is the only instance.
	private ExchangeServer() {
	}
	
	/**
	 * Loads the properties file/creates it. The given command-line arguments
	 * will be parsed for options. All properties can be specified in the c-l,
	 * they won't persist beyond that execution.
	 * @param override The properties to override.
	 */
	public void loadProperties(String[] override) {
		sp = new ServerProperties(override);
	}
	
	/**
	 * Get the server properties object.
	 */
	public ServerProperties getProperties() {
		return sp;
	}
	
	/**
	 * Where all of the not-so-exciting action occurs!
	 */
	public void run() {
		
	}
}
