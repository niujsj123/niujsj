package io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.Future;

public class AioClient {

	AsynchronousSocketChannel client ;
	ByteBuffer sendBuff = ByteBuffer.allocate(1024);
	
	
	public AioClient() throws IOException {
		client = AsynchronousSocketChannel.open();
		Future<Void> future = client.connect(new InetSocketAddress("127.0.0.1", 8081));
		System.out.println("client start");
	}


	public static void main(String[] args) throws IOException {
		
		new AioClient().session();
	}


	private void session() {
		System.out.println("please input:");
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()){
			String msg = scan.nextLine();
			sendBuff.clear();
			sendBuff.put(msg.getBytes());
			sendBuff.flip();
			client.write(sendBuff);
		}
		
	}

}
