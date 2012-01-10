package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MessagePacket extends Packet {
	private String message;

	public MessagePacket(ClientConnection connection) {
		super(connection);
	}

	public void run() {
		System.out.println(message);
	}

	@Override
	public void read(ByteBuffer s) throws IOException {
		message = NioUtil.readString(s);
	}

	@Override
	public void write(ByteBuffer s) throws IOException {
		NioUtil.writeString(s, message);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
