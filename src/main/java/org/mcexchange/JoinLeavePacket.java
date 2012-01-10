package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;

public class JoinLeavePacket extends Packet {
	//true for join, false for leave
	private boolean type;
	//network name
	private String network;
	
	public JoinLeavePacket(ClientConnection connection) {
		super(connection);
	}

	public void run() {
		// TODO get network from network list, add server IP
	}

	@Override
	public void read(ByteBuffer s) throws IOException {
		setType(NioUtil.readBooleans(s, 1)[0]);
		setNetwork(NioUtil.readString(s));
	}

	@Override
	public void write(ByteBuffer s) throws IOException {
		NioUtil.writeBooleans(s, getType());
		NioUtil.writeString(s, network);
	}
	
	public boolean getType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}
	
	public String getNetwork() {
		return network;
	}
	
	public void setNetwork(String network) {
		this.network = network;
	}
	
}
