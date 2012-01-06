package org.mcexchange;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements Runnable {
	private final Socket socket;
	private final DataInputStream in;
	private final DataOutputStream out;
	
	/**
	 * Creates a new connection with the given client.
	 * @param socket The client socket.
	 * @throws IOException If there is an error opening a connection to
	 * the client.
	 */
	public ClientConnection(Socket socket) throws IOException {
		this.socket = socket;
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}
	
	/**
	 * Reads a Packet from the stream.
	 * @return The sent Packet.
	 */
	public Packet readPacket() {
		try {
			byte b = in.readByte();
			Packet p = Packet.getPacket(b);
			p.read(in);
			return p;
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.out.println("Client " + this + " received an un-registered packet!");
			System.out.println("Kicking client...");
			disconnect();
		}
		return null;
	}
	
	/**
	 * Closes the connection.
	 */
	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Unable to disconnect client:");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends the given Packet to the client.
	 */
	public void sendPacket(Packet p) {
		try {
			byte id = Packet.getId(p);
			out.writeByte(id);
			p.write(out);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.out.println("Tried to send an unregistered packet to Client " + this + "!");
			disconnect();
		}
		
	}
	
	/**
	 * And the magic happens here.
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			Packet recieved = readPacket();
			recieved.run();
		}
	}
}
