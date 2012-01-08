package org.mcexchange;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements Runnable {
	public final RegisteredPackets registeredPackets;
	
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
		registeredPackets = new RegisteredPackets(this);
		
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
			System.out.println("Recieved packet: " + b);
			return p;
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.err.println("Client " + this + " received an un-registered packet!");
			System.err.println("Kicking client...");
			sendPacket(registeredPackets.getDisconnect());
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
			System.err.println("Unable to disconnect client:");
			e.printStackTrace();
		}
		System.out.println("Successfully disconnected from client " +  this);
	}
	
	/**
	 * Sends the given Packet to the client.
	 */
	public void sendPacket(Packet p) {
		try {
			byte id = Packet.getId(p);
			out.writeByte(id);
			p.write(out);
			System.out.println("Sent packet: " + id);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.err.println("Tried to send an unregistered packet to Client " + this + "!");
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
		sendPacket(registeredPackets.getDisconnect());
		disconnect();
	}
}
