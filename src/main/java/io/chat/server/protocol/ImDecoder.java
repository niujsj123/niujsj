package io.chat.server.protocol;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * IM协议解码器，输入，客户端请求过来以后，要解码
 * @author niujsj
 *
 */
public class ImDecoder extends ByteToMessageDecoder{
	private final Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s-\\s(.*))?"); 

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try {
			final int len = in.readableBytes();
			final byte[] array = new byte[len];
			String content = new String(array,in.readerIndex(),len);
			//如果拿到的字符串内容不是自定义协议，那么忽略
			//我只解析我自己能认识的协议
			/*if (StringUtils.isNotEmpty(content) && !IMP.isIMP(content)){
				ctx.channel().pipeline().remove(this);
				return;
			}*/
			//如果我能解析了，那么就把缓冲区中的数据清掉
			//免得下面解码继续拿过去解码，然后，解了半天还解不开
			in.getBytes(in.readerIndex(), array,0,len);
			out.add(new MessagePack().read(array,IMMessage.class));
			in.clear();
		} catch (Exception ex) {
			//告诉下面的其他解码器，我无法解析，你去解析吧
			ctx.channel().pipeline().remove(this);
		}
	}
	
	/**
	 * 把IMP协议字符串解析成一个IMMessage的对象
	 * 这样显得就有种神秘感
	 * 显得高端大气上档次
	 * @param msg
	 * @return
	 */
	public IMMessage decode(String msg){
		if (StringUtils.isEmpty(msg)){
			return null;
		}
		Matcher matcher = pattern.matcher(msg);
		String header =null,content =null;//消息头，消息体
		if (matcher.matches()){
			header = matcher.group(1);
			content = matcher.group(3);
		}
		String[] headers = header.split("\\]\\[");
		//获取消息发送时间
		long time = Long.parseLong(headers[1]);
		//获取昵称
		String nickName = headers[2];
		nickName = nickName.length()>10?nickName.substring(0, 9):nickName;
		
		String cmd = headers[0];
		IMMessage message = null;
		if (IMP.LOGIN.toString().equals(cmd) || IMP.LOGOUT.toString().equals(cmd) || IMP.FLOWER.toString().equals(cmd)){
			message = new IMMessage(cmd, time, nickName);
		} else if(IMP.SYSTEM.toString().equals(cmd) || IMP.CHAT.toString().equals(cmd)){
			message = new IMMessage(cmd, time, nickName, content);
		} 
		return message;
	}

}
