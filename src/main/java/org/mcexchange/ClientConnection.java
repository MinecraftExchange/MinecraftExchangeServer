package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientConnection implements Runnable {
	public static final int MAX_PACKET_SIZE = 500;
	public final RegisteredPackets packets;
	
	private final SocketChannel channel;
	private final ByteBuffer read = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
	private final ByteBuffer write = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
	
	/**
	 * Creates a new connection with the given client.
	 * @param socket The client socket.
	 * @throws IOException If there is an error opening a connection to
	 * the client.
	 */
	public ClientConnection(SocketChannel channel) throws IOException {
		packets = new RegisteredPackets(this);
		
		this.channel = channel;
		System.out.println(channel);
	}
	
	/**
	 * Reads a Packet from the stream.
	 * @return The sent Packet.
	 */
	public Packet readPacket() {
		try {
			NioUtil.read(channel, read, 1);
			Packet p = packets.getPacket(read.get());
			NioUtil.read(channel, read, 2);
			short length = read.getShort();
			NioUtil.read(channel, read, length);
			p.read(read);
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
			write.clear();
			write.put(packets.getId(p));
			write.position(3);
			p.write(write);
			write.flip();
			write.position(1);
			write.putShort((short) ( write.limit()-3));
			NioUtil.writeFull(channel,write);
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
		sendPacket(packets.getDisconnect());
		disconnect();
	}
}
