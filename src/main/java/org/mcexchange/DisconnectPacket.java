package org.mcexchange;

import java.nio.channels.SocketChannel;

/**
 * Signifies disconnection.
 *
 */
public class DisconnectPacket extends Packet {
	
	public DisconnectPacket(ClientConnection connection) {
		super(connection);
	}

	public void run() {
		System.out.println("disconnect.");
		Thread.currentThread().interrupt();
	}

	@Override
	public void read(SocketChannel s) { }

	@Override
	public void write(SocketChannel s) { }
	
}
