package io.chat.server.handler;

import io.chat.processor.MsgProcessor;
import io.chat.server.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理自定义协议的逻辑
 * @author niujsj
 *
 */
public class ImHandler extends SimpleChannelInboundHandler<IMMessage> {
	MsgProcessor p =new MsgProcessor();
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
		p.sendMsg(ctx.channel(),msg);
	}
	
	

}
