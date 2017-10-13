package io.chat.server.handler;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.apache.commons.lang.StringUtils;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedNioFile;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String uri = request.getUri();
		HttpVersion version = request.getProtocolVersion();
		String resource = "/".equals(uri)?"/chat.html":uri;
		System.out.println(HttpServerHandler.class.getProtectionDomain().getCodeSource().getLocation().toString());
		System.out.println(uri+","+version);
		String path = this.getClass().getClassLoader().getResource("webroot").getPath()+resource;
		System.out.println(path);
		RandomAccessFile file;
		try {
			file = new RandomAccessFile(path,"r");
		} catch (Exception ex) {
			//继续下一次请求，服务端不报错
			ctx.fireChannelRead(request.retain());
			return;
		}
		DefaultHttpResponse httpResponse = new DefaultHttpResponse(version,HttpResponseStatus.OK);
		String contextType="text/html;";
		if (uri.endsWith(".css")){
			contextType = "text/css;";
		} else if (uri.endsWith(".js")){
			contextType="text/javascript;";
		} else if (uri.toLowerCase().matches("(jpg|png|gif|ico)$")){
			contextType="image/"+ StringUtils.substring(uri, uri.lastIndexOf("."))+ ";";
		}
		httpResponse.headers().add(HttpHeaders.Names.CONTENT_TYPE, contextType+"charset=utf8;");
		boolean keepalive = HttpHeaders.isKeepAlive(request);
		if (keepalive){
			httpResponse.headers().add(HttpHeaders.Names.CONTENT_LENGTH,file.length());
			httpResponse.headers().add(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
		}
		ctx.channel().writeAndFlush(httpResponse);
		ctx.channel().writeAndFlush(new ChunkedFile(file));
		//如果不是长连接，然后文件也全部输出完毕了，那么就关闭连接
		ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		if (!keepalive){
			future.addListener(ChannelFutureListener.CLOSE);
		}
		file.close();
		
	}

}
