package io.nio;

import java.nio.IntBuffer;

public class TestIntBuffer {

	public static void main(String[] args) {
		IntBuffer buff = IntBuffer.allocate(8);
		System.out.println(buff.limit());
		System.out.println(buff.capacity());
		for (int i = 0; i < buff.capacity(); i++) {
			buff.put(2*(i+1));
		}
		buff.flip();
		while(buff.hasRemaining()){
			System.out.println(buff.get()+" ");
		}
	}

}
