package org.mcexchange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioUtil {
	/**
	 * Clears the given buffer and reads length bytes into it.
	 */
	public static void read(SocketChannel s, ByteBuffer b, int length) throws IOException {
		b.clear();
		b.limit(length);
		while(b.remaining()>0) s.read(b);
		b.flip();
	}
	
	/**
	 * Reads from the given channel into the given buffer from index 0 (of the buffer)
	 * to the buffer's limit. Any data in the buffer is lost. The current limit and
	 * position remasin unchanged.
	 */
	public static void readFull(SocketChannel s, ByteBuffer b) throws IOException {
		int pos = b.position();
		b.rewind();
		while(b.remaining()>0) s.read(b);
		b.position(pos);
	}
	
	/**
	 * Writes the contents of the buffer from index 0 (of the buffer) to the buffer's
	 * limit. All data is preserved. The current limit and position remain unchanged.
	 */
	public static void writeFull(SocketChannel s, ByteBuffer b) throws IOException {
		int pos = b.position();
		b.rewind();
		while(b.remaining()>0) s.write(b);
		b.position(pos);
	}
	
	/**
	 * Reads a String from the buffer (at the buffer's current position) that was written
	 * with the corresponding method writeString(). An exception might be thrown if a String
	 * was not written at the given position, written in the wrong format, or not fully
	 * written. The position is incremented to the end of the String.
	 */
	public static String readString(ByteBuffer b) {
		short s = b.getShort();
		byte[] bytes = new byte[s];
		b.get(bytes);
		return new String(bytes);
	}
	
	/**
	 * Writes a String from the buffer (at the buffer's current position) that can be read
	 * by the corresponding method writeString(). An exception might be thrown if there
	 * is not enough room left in the buffer to hold the String. The position is incremented
	 * to the end of the String. The String will not be able to be read back if its length
	 * can not be expressed in a short.
	 */
	public static void writeString(ByteBuffer b, String s) {
		b.putShort((short) s.length());
		b.put(s.getBytes());
	}
	
	
	/**
	 * Reads the specified number of booleans from the given buffer.
	 */
	public static boolean[] readBooleans(ByteBuffer b, int amount) {
		boolean[] bools = new boolean[amount];
		byte[] bytes = new byte[((amount - (amount%8))/8) + (amount%8==0 ? 0 : 1)];
		b.get(bytes);
		byte temp;
		for(int i = 0; i < bools.length; i ++) {
			int pos = ((i - (i%8))/8);
			switch(i%8) {
			case 0:
				temp = (byte) (bytes[pos]&Byte.parseByte("10000000", 2));
				temp>>=7;
				bools[i] = temp==1 ? true : false;
				break;
			case 1:
				temp = (byte) (bytes[pos]&Byte.parseByte("01000000", 2));
				temp>>=6;
				bools[i] = temp==1 ? true : false;
				break;
			case 2:
				temp = (byte) (bytes[pos]&Byte.parseByte("00100000", 2));
				temp>>=5;
				bools[i] = temp==1 ? true : false;
				break;
			case 3:
				temp = (byte) (bytes[pos]&Byte.parseByte("00010000", 2));
				temp>>=4;
				bools[i] = temp==1 ? true : false;
				break;
			case 4:
				temp = (byte) (bytes[pos]&Byte.parseByte("00001000", 2));
				temp>>=3;
				bools[i] = temp==1 ? true : false;
				break;
			case 5:
				temp = (byte) (bytes[pos]&Byte.parseByte("00000100", 2));
				temp>>=2;
				bools[i] = temp==1 ? true : false;
				break;
			case 6:
				temp = (byte) (bytes[pos]&Byte.parseByte("00000010", 2));
				temp>>=1;
				bools[i] = temp==1 ? true : false;
				break;
			case 7:
				temp = (byte) (bytes[pos]&Byte.parseByte("00000001", 2));
				bools[i] = temp==1 ? true : false;
				break;
			}
		}
		return bools;
	}
	
	/**
	 * Writes the given booleans to the buffer. Up to 8 booleans will be written in each byte.
	 * The bytes can be read back via the corresponding method in the same order as they are
	 * written.
	 */
	public static void writeBooleans(ByteBuffer b, boolean...bools) {
		byte[] bytes = new byte[((bools.length - (bools.length%8))/8) + (bools.length%8==0 ? 0 : 1)];
		for(int i = 0; i < bools.length; i ++) {
			int pos = ((i - (i%8))/8);
			String num = bools[i] ? "1" : "0";
			switch(i%8) {
			case 0:
				bytes[pos]&=Byte.parseByte("01111111", 2);
				bytes[pos]|=Byte.parseByte(num + "1111111", 2);
				break;
			case 1:
				bytes[pos]&=Byte.parseByte("10111111", 2);
				bytes[pos]|=Byte.parseByte("1" + num + "111111", 2);
				break;
			case 2:
				bytes[pos]&=Byte.parseByte("11011111", 2);
				bytes[pos]|=Byte.parseByte("11" + num + "11111", 2);
				break;
			case 3:
				bytes[pos]&=Byte.parseByte("11101111", 2);
				bytes[pos]|=Byte.parseByte("111" + num + "1111", 2);
				break;
			case 4:
				bytes[pos]&=Byte.parseByte("11110111", 2);
				bytes[pos]|=Byte.parseByte("1111" + num + "111", 2);
				break;
			case 5:
				bytes[pos]&=Byte.parseByte("11111011", 2);
				bytes[pos]|=Byte.parseByte("11111" + num + "11", 2);
				break;
			case 6:
				bytes[pos]&=Byte.parseByte("11111101", 2);
				bytes[pos]|=Byte.parseByte("111111" + num + "1", 2);
				break;
			case 7:
				bytes[pos]&=Byte.parseByte("11111110", 2);
				bytes[pos]|=Byte.parseByte("1111111" + num, 2);
				break;
			}
		}
		b.put(bytes);
	}
}
