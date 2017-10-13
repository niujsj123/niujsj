package io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BufferProgram {

	public static void main(String[] args) throws IOException {
		FileInputStream in = new FileInputStream(new File("e:/test.txt"));
		FileChannel  fc = in.getChannel();
		
		ByteBuffer buff = ByteBuffer.allocate(10);
		byte[] b = new byte[10];
		ByteBuffer byteBuffer = ByteBuffer.wrap(b);
		
		output("init",buff);
		
		fc.read(buff);
		output("invoke read",buff);
		
		buff.flip();
		output("invoke flip",buff);
		
		while(buff.hasRemaining()){
			System.out.println(buff.get());
		}
		output("invoke get", buff);
		
		buff.clear();
		output("invoke clear", buff);

		in.close();
	}

	private static void output(String string, ByteBuffer buff) {
		System.out.println(string+":");
		System.out.print("capacity:"+buff.capacity()+",");
		System.out.print("limit:"+buff.limit()+",");
		System.out.println("position:"+buff.position());
		System.out.println();
	}

}
