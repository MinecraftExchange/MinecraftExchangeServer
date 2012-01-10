package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientConnection implements Runnable {
	public final RegisteredPackets registeredPackets;
	
	private final SocketChannel channel;
	private final ByteBuffer readId = ByteBuffer.allocateDirect(1);
	private final ByteBuffer writeId = ByteBuffer.allocateDirect(1);
	
	/**
	 * Creates a new connection with the given client.
	 * @param socket The client socket.
	 * @throws IOException If there is an error opening a connection to
	 * the client.
	 */
	public ClientConnection(SocketChannel channel) throws IOException {
		registeredPackets = new RegisteredPackets(this);
		
		this.channel = channel;
		System.out.println(channel);
	}
	
	/**
	 * Reads a Packet from the stream.
	 * @return The sent Packet.
	 */
	public Packet readPacket() {
		try {
			readId.clear();
			channel.read(readId);
			readId.flip();
			Packet p = Packet.getPacket(readId.get());
			p.read(channel);
			return p;
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			e.printStackTrace();
			System.err.println("Client " + this + " received an un-registered packet!");
			System.err.println("Kicking client...");
			Thread.currentThread().interrupt();
		}
		return null;
	}
	
	/**
	 * Closes the connection.
	 */
	public void disconnect() {
		try {
			channel.close();
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
			writeId.clear();
			writeId.put(Packet.getId(p));
			writeId.flip();
			channel.write(writeId);
			p.write(channel);
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
