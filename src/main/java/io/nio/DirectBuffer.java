package io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DirectBuffer {

	public static void main(String[] args) throws IOException {
		String testfile ="e:/test.txt";
		FileInputStream in = new FileInputStream(new File(testfile));
		FileChannel fc = in.getChannel();
		
		String outFile = "e:/out.txt";
		FileOutputStream out = new FileOutputStream(new File(outFile));
		FileChannel oc = out.getChannel();
		
		
		ByteBuffer buff = ByteBuffer.allocate(1024);
		long start = System.currentTimeMillis();
		System.out.println();
		for(;;){
			buff.clear();
			int r = fc.read(buff);
			if (r==-1) break;
			buff.flip();
			oc.write(buff);
			
		}
		System.out.println("cost:"+(System.currentTimeMillis()-start));
 
	}

}
