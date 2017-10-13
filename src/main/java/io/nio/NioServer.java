package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NioServer {
	ServerSocketChannel server;
	int port = 8086;
	Selector selector;
	ByteBuffer receiveBuff = ByteBuffer.allocate(1024);
	ByteBuffer sendBuff = ByteBuffer.allocate(1024);
    Map<SelectionKey,String> sessionMsg = new HashMap<>();
	public NioServer(int port) {
		this.port = port;
		try {
			server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(port));
			server.configureBlocking(false);
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("server start,port:" + port);

		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new NioServer(8086).listener();
	}

	private void listener() {
		while (true) {
			try {
				int i = selector.select();
				if (i == 0)
					continue;
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				while(iterator.hasNext()){
//				keys.forEach(key -> {
					SelectionKey key = iterator.next();
//					System.out.println("selectkey:" +key);
					try {
						process(key);
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					iterator.remove();
//				});
				}
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}
		
	}

	private void process(SelectionKey key) throws IOException {
		if (key.isAcceptable()){
			SocketChannel client = server.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
		} else if(key.isReadable()){
			receiveBuff.clear();
		    SocketChannel client = (SocketChannel) key.channel();
		    int len =client.read(receiveBuff);
		    if(len>0){
		    	String msg = new String(receiveBuff.array(),0,len);
		    	sessionMsg.put(key,msg);
		    	System.out.println("获取客户端发送来的消息：" + msg);
		    }
		    client.configureBlocking(false);
		    client.register(selector, SelectionKey.OP_WRITE);
		} else if(key.isWritable()){
			if(!sessionMsg.containsKey(key)){return;}
			sendBuff.clear();
		    SocketChannel client = (SocketChannel) key.channel();
		    String msg =sessionMsg.get(key)+",你好，你的请求已处理完成";
		    sendBuff.put(msg.getBytes());
		    sendBuff.flip();
		    client.write(sendBuff);
		    
		    client.register(selector, SelectionKey.OP_READ);
		}
		
	}

}
