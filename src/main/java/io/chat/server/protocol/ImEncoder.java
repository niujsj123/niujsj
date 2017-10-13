package io.chat.server.protocol;

import org.apache.commons.lang.StringUtils;
import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * IM协议编码器，服务端往客户端输出，要编码
 * @author niujsj
 *
 */
public class ImEncoder extends MessageToByteEncoder<IMMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
		out.writeBytes(new MessagePack().write(msg));
		
	}
	/**
	 * 把IMMessage对象解析成我们IMP协议字符串，方便输出到客户端
	 * 显得低调奢华有内涵
	 * @param message
	 * @return
	 */
	public String encode(IMMessage msg){
		if (msg == null) {return null;}
		String cmd = msg.getCmd();
		String content = msg.getContent();
		String pre = "["+cmd+"]["+msg.getTime()+"]";
		if (IMP.LOGIN.getName().equals(cmd)||IMP.LOGOUT.getName().equals(cmd)
				||IMP.FLOWER.getName().equals(cmd)||IMP.CHAT.getName().equals(cmd)){
			pre+=("["+msg.getSender()+"]");
		} else if(IMP.SYSTEM.getName().equals(cmd)){
			pre+=("["+msg.getOnline()+"]");
		}
		if (StringUtils.isNotEmpty(content)){
			pre+=(" - "+content);
		}
		return pre;
		
	}

}
