package io.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
   int port;
   String host;
	
	public NettyClient(String host,int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) throws InterruptedException {
		new NettyClient("127.0.0.1", 8085).connect();
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
					pipeline.addLast(new StringEncoder());
					pipeline.addLast(new StringDecoder());
					pipeline.addLast(new ClientHandler());
					
				}
			});
			ChannelFuture future = b.connect(host, port).sync();
			future.channel().writeAndFlush("test");
			future.channel().closeFuture().sync();
		}finally{
			workGroup.shutdownGracefully();
		}
		
	}

}
