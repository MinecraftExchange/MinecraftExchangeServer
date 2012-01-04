package org.mcexchange;

import java.net.Socket;

public class ClientConnection implements Runnable {
	private Socket socket = null;
	
	/**
	 * Creates a new connection with the given client.
	 * @param socket
	 */
	public ClientConnection(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * And the magic happens here.
	 */
	public void run() {
		
	}
}
