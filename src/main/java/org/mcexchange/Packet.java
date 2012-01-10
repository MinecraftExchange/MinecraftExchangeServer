package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

public abstract class Packet implements Runnable {
	//packet map.
	private static final BidiMap packets = new DualHashBidiMap();
	private final ClientConnection cc;
	
	/**
	 * Maps a Packet to an id.
	 */
	public static void assignId(byte id, Packet packet) {
		packets.put(id,packet);
	}
	
	/**
	 * Unmaps a Packet to an id.
	 */
	public static void removeId(Packet packet) {
		packets.remove(packet);
	}
	
	/**
	 * Gets a packet's id (none, if it hasn't been mapped to
	 * one yet).
	 */
	public static byte getId(Packet packet) {
		return (Byte) packets.getKey(packet);
	}
	
	/**
	 * Gets an id's packet (none if no Packet has been mapped
	 * to this id).
	 */
	public static Packet getPacket(byte id) {
		return (Packet) packets.get(id);
	}
	
	/**
	 * Creates a Packet for the given ClientConnection.
	 */
	public Packet(ClientConnection connection) {
		cc = connection;
	}
	
	public ClientConnection getConnection() {
		return cc;
	}
	
	/**
	 * Reads the Packet's data from the buffer. The buffer is at position 0
	 * when given and is filled with the packet's data.
	 */
	public abstract void read(ByteBuffer b) throws IOException;
	
	/**
	 * Writes the Packet to the buffer. The buffer is at position 3 if no
	 * length has been specified for this packet, or 1 if one has. The first
	 * 3 bytes will be filled in once the data has been written, so don't
	 * store anything there. On a side note, each packet is limited to 500
	 * bytes including the initial 1 or 3.
	 */
	public abstract void write(ByteBuffer b) throws IOException;
}
