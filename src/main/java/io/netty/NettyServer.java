package io.netty;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    int port;
     
	public NettyServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		
		new NettyServer(8085).listener();
		System.in.read();
	}

	private void listener() throws InterruptedException, ExecutionException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 1024)
		.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new ServerHandler());
				
			}
		});
		ChannelFuture f = b.bind(this.port).sync();
		System.out.println("服务已启动,监听端口是" + this.port);
		f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

}
