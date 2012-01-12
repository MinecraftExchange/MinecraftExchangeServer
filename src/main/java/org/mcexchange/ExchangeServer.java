package org.mcexchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.mcexchange.api.Connection;
import org.mcexchange.api.MessagePacket;

public class ExchangeServer implements Runnable {
	//------------------------------//
	//    STATIC/SINGLETON STUFF    //
	//------------------------------//
	
	//the one instance of ExchangeServer
	private static final ExchangeServer es = new ExchangeServer();
	
	public static final int DEFAULT_PORT = 62924;

	/**
	 * Gets the singleton instance of ExchangeServer.
	 */
	public static ExchangeServer getInstance() {
		return es;
	}

	//------------------------------//
	//       NON-STATIC STUFF       //
	//------------------------------//
	
	private ServerProperties sp;
	private boolean listening = false;
	private int port = DEFAULT_PORT;
	private ServerSocketChannel socket = null;
	private final ArrayList<Thread> threads = new ArrayList<Thread>();
	private final ArrayList<Connection> connections = new ArrayList<Connection>();
	
	//private constructor ensuring that this is the only instance.
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
	 * Obtains a reference to the threads the client connections are running
	 * in. It is important to note that this list is NOT copied so any changes
	 * here will affect the whole server.
	 */
	public List<Thread> getThreads() {
		return threads;
	}
	
	/**
	 * Obtains a reference to the client connections. It is important to note
	 * that this list is NOT copied so any changes here will affect the whole
	 * Server.
	 */
	public List<Connection> getConnections() {
		return connections;
	}
	
	/**
	 * Binds the server to the specified port.
	 * @param bindPort
	 */
	public void bind(int bindPort) {
		try {
		    socket = ServerSocketChannel.open();
		    socket.socket().bind(new InetSocketAddress(bindPort));
			System.out.println("Successfully bound to port: "+bindPort+".");
		} catch (IOException e) {
			System.err.println("Could not bind to port: "+bindPort+".");
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
				Connection cc = new Connection(socket.accept());
				Thread t = new Thread(cc);
				t.start();
				connections.add(cc);
				threads.add(t);
				System.out.println("Succesfully connected to client " + cc);
			} catch (IOException e) {
				System.err.println("Could not connect to client.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Where all of the not-so-exciting action occurs!
	 */
	public void run() {
		try {
			port = Integer.parseInt(sp.getProperty("port"));
		} catch(NumberFormatException e) {
			//ignore...
		}
		bind(port);
		listen();
	}
	
	/**
	 * Send a message to every client.
	 */
	public void broadcastMessage(String message) {
		for(Connection c : connections) {
			MessagePacket mp = c.packets.getMessage();
			mp.setMessage(message);
			c.sendPacket(mp);
		}
	}
}
