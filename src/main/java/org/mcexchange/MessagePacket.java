package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessagePacket extends Packet {
	private String message;

	public MessagePacket(ClientConnection connection) {
		super(connection);
	}

	public void run() {
		System.out.println(message);
	}

	@Override
	public void read(SocketChannel s) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(2);
		while(b.remaining()>0) s.read(b);
		b.flip();
		short size = b.getShort();
		b = ByteBuffer.allocate(size*2);
		while(b.remaining()>0) s.read(b);
		b.flip();
		message = b.asCharBuffer().toString();
	}

	@Override
	public void write(SocketChannel s) throws IOException {
		byte[] bytes = message.getBytes();
		short size = (short) bytes.length;
		ByteBuffer b = ByteBuffer.allocate(size + 2);
		b.putShort(size);
		b.put(bytes);
		b.flip();
		while(b.remaining()>0) s.write(b);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
