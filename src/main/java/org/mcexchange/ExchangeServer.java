package org.mcexchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ExchangeServer implements Runnable {
	//the one instance of ExchangeServer
	private static final ExchangeServer es = new ExchangeServer();
	//default, converted from MCXCG using phone letter/number system
	public static int port = 62924;
	ServerSocket socket = null;
	boolean listening = false;
	ArrayList<Thread> connections = new ArrayList<Thread>();
	
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
	 * Binds the server to the specified port.
	 * @param bindPort
	 */
	public void bind(int bindPort) {
		try {
			socket = new ServerSocket(bindPort);
			System.out.printf("Successfully bound to port: %d.\n", port);
		}
		catch (IOException e) {
			System.err.printf("Could not bind to port: %d.\n", port);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Makes server listen for incoming connections from clients.
	 */
	public void listen() {
		listening = true;
		System.out.println("Listening for client connections.");
		while(listening) {
			try {
				Thread t = new Thread(new ClientConnection(socket.accept()));
				t.start();
				connections.add(t);
				System.out.println("Succesfully connected to client.");
			}
			catch (IOException e) {
				System.err.println("Could not connect to client.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Where all of the not-so-exciting action occurs!
	 */
	public void run() {
		bind(port);
		listen();
	}
}
