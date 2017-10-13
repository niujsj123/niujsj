package io.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBuffer {
	private static int start =0;
	private static int size=1024;

	public static void main(String[] args) throws IOException {
		RandomAccessFile raf =new RandomAccessFile("e:/test.txt", "rw");
		FileChannel fc = raf.getChannel();
		MappedByteBuffer mb = fc.map(FileChannel.MapMode.READ_WRITE, start, size);
		mb.put(0,(byte) 97);
		mb.put(10,(byte) 122);
		raf.close();
	}

}
