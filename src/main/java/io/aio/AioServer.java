package io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AioServer {
	AsynchronousServerSocketChannel server;
    ByteBuffer sendbuff = ByteBuffer.allocate(1024);
    ByteBuffer rbuff = ByteBuffer.allocate(1024);
	public AioServer() throws IOException {
		server = AsynchronousServerSocketChannel.open();
		server.bind(new InetSocketAddress(8081));
		System.out.println("server start");
	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		new AioServer().listener();
		System.in.read();
	}

	private void listener() throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newCachedThreadPool();
		for(int i=0;i<1;i++){
		Runnable r =()->{
			while(true){
			server.accept(null,new CompletionHandler<AsynchronousSocketChannel, Void>() {

				@Override
				public void completed(AsynchronousSocketChannel client, Void attachment) {
					server.accept(null, this);
					process(client);
					
				}

				private void process(AsynchronousSocketChannel client) {
					rbuff.clear();
					try {
						int len = client.read(rbuff).get();
						rbuff.flip();
						if (len>0){
							String msg = new String(rbuff.array(),0,len);
							System.out.println("服务器获取消息："+msg);
						}
					} catch (InterruptedException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					} catch (ExecutionException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					
				}

				@Override
				public void failed(Throwable exc, Void attachment) {
					System.out.println("异步IO失败");
					
				}
			});
			}
//			while(true){}
		};
		es.execute(r);
		}
			/*AsynchronousSocketChannel channel = future.get();
			Future<Integer> read = channel.read(buff);
			Integer len = read.get();
			if (len>0){
				String msg = new String(buff.array(),0,len);
				System.out.println("服务器获取消息："+msg);
			}*/
		}
		

}
