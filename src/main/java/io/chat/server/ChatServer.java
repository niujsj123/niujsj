package io.chat.server;


import io.chat.server.handler.HttpFileHandler;
import io.chat.server.handler.HttpRequestHandler;
import io.chat.server.handler.HttpServerHandler;
import io.chat.server.handler.ImHandler;
import io.chat.server.handler.WebsocketHandler;
import io.chat.server.protocol.ImDecoder;
import io.chat.server.protocol.ImEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServer {
	
	int port = 80;

	public static void main(String[] args) {
		new ChatServer().start();

	}

	private void start() {
		//创建主线程(主从模式)
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//创建子线程
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		try{
			//创建netty socket server
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
			 //默认分配1024个工作线程
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					
					//获取 工作流，流水线,pipeline
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new ImDecoder());
					pipeline.addLast(new ImEncoder());
					pipeline.addLast(new ImHandler());
					
					
					//======== 对HTTP协议的支持  ==========
//					pipeline.addLast(new HttpServerCodec());
					//主要就是将一个http请求或者响应变成一个FullHttpRequest对象
//					pipeline.addLast(new HttpObjectAggregator(64*1024));
//					pipeline.addLast(new HttpRequestDecoder());  
//                    pipeline.addLast(new HttpResponseEncoder());
//					pipeline.addLast("compressor", new HttpContentCompressor());  
					//这个是用来处理文件流
//					pipeline.addLast(new ChunkedWriteHandler());
					//处理HTTP请求的业务逻辑
//					pipeline.addLast(new HttpServerHandler());
					 pipeline.addLast(new HttpServerCodec());
				        pipeline.addLast(new HttpObjectAggregator(640*1024));
				        pipeline.addLast(new ChunkedWriteHandler());
//				        pipeline.addLast(new HttpRequestHandler());
				        pipeline.addLast(new HttpServerHandler());
					
					//上传文件
					/*pipeline.addLast(new HttpRequestDecoder());  
                    pipeline.addLast(new HttpResponseEncoder());  
                    pipeline.addLast("compressor", new HttpContentCompressor());  
                    pipeline.addLast(new HttpFileHandler());  */
					
					//======== 对WebSocket协议的支持  ==========
                	//加上这个Handler就已经能够解析WebSocket请求了
                	//相当于WebSocket解码器
                	//im是为了和http请求区分开来，以im开头的请求都有websocket来解析
					pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
					//实现处理WebSocket逻辑的Handler
					pipeline.addLast(new WebsocketHandler());
					
//					pipeline.addLast(new HttpRequestDecoder());  
//                    pipeline.addLast(new HttpResponseEncoder());  
//                    pipeline.addLast("compressor", new HttpContentCompressor());  
                    pipeline.addLast(new HttpFileHandler()); 
					
					
				}});
			//采用同步的方式监听客户端连接
            //NIO同步非阻塞
			ChannelFuture future = b.bind(port).sync();
			System.out.println("服务已启动,监听端口" + this.port);
			future.channel().closeFuture().sync();
		}catch(Exception e){
			
		}finally{
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

}
