package io.nio;

import java.nio.ByteBuffer;

public class BufferSlice {

	public static void main(String[] args) {
		ByteBuffer buff = ByteBuffer.allocate(10);
		for (int i = 0; i < 10; i++) {
			buff.put((byte) i);
		}

		/*buff.position(3);
		buff.limit(7);*/
		
//		ByteBuffer slice = buff.slice();
		ByteBuffer readonly = buff.asReadOnlyBuffer();
		System.out.println(readonly.capacity());
		for (int i = 0; i < buff.capacity(); i++) {
			byte b = buff.get(i);
			b*=10;
			buff.put(i,b);
		}
		
		readonly.position(0);
		readonly.limit(buff.capacity());
		while(readonly.hasRemaining()){
			System.out.println(readonly.get());
		}
	}

}
