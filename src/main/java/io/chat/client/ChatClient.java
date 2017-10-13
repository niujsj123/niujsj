package io.chat.client;

import io.chat.client.handler.ChatClientHandler;
import io.chat.server.protocol.ImDecoder;
import io.chat.server.protocol.ImEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChatClient {
	int port;
	   String host;
		String nickName;
		ChatClientHandler clientHandler;
		public ChatClient(String host,int port,String nickName) {
			this.host = host;
			this.port = port;
			this.nickName = nickName;
			clientHandler = new ChatClientHandler(nickName);
		}

		public static void main(String[] args) throws InterruptedException {
			new ChatClient("127.0.0.1", 80,"laowang").connect();
		}

		private void connect() throws InterruptedException {
			EventLoopGroup workGroup = new NioEventLoopGroup();
			try{
				Bootstrap b = new Bootstrap();
				b.group(workGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						//MyEclipse里面的纯Socket请求
	                	//既不可能发送HTTP请求，也不可能发送WebScoket请求
	                	//这里我们只发自定义协议相关的请求
						pipeline.addLast(new ImEncoder());
						pipeline.addLast(new ImDecoder());
						pipeline.addLast(clientHandler);
						
					}
				});
				ChannelFuture future = b.connect(host, port).sync();
//				future.channel().writeAndFlush("test");
				future.channel().closeFuture().sync();
			}finally{
				workGroup.shutdownGracefully();
			}
			
		}
}
