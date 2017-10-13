package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class NioClient {
	SocketChannel client = null;
	Selector selector;
	ByteBuffer sendBuff = ByteBuffer.allocate(1024);
	ByteBuffer rBuff = ByteBuffer.allocate(1024);
	
	public NioClient() throws IOException {
		client = SocketChannel.open();
		client.connect(new InetSocketAddress("127.0.0.1", 8086));
		client.configureBlocking(false);
		selector = Selector.open();
		client.register(selector, SelectionKey.OP_CONNECT);
	}


	public static void main(String[] args) throws IOException {
		new NioClient().session();

	}


	private void session() throws IOException {
		if (client.isConnected()){
			client.finishConnect();
			System.out.println("请在控制台登记姓名");
			client.register(selector, SelectionKey.OP_WRITE);
		}
		System.out.println("请输入消息：");
		Scanner scan = new Scanner(System.in);
		
		while(scan.hasNextLine()){
			String line = scan.nextLine();
			if ("".equals(line))continue;
			process(line);
		}
		
	}


	private void process(String line) throws IOException {
		boolean unfin=true;
		while(unfin){
			int i = selector.select();
			if (i==0)continue;
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				if (key.isWritable()){
					sendBuff.clear();
					sendBuff.put(line.getBytes());
					sendBuff.flip();
					try {
						client.write(sendBuff);
						client.register(selector, SelectionKey.OP_READ);
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				} else if (key.isReadable()){
					rBuff.clear();
					try {
						int ret =client.read(rBuff);
						if(ret>0){
							rBuff.flip();
							String msg = new String(rBuff.array(),0,ret);
							System.out.println("获取服务端的消息："+msg);
						}
						client.register(selector, SelectionKey.OP_WRITE);
						unfin = false;
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			}
		}
		
	}

}
