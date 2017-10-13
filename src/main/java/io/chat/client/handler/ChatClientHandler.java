package io.chat.client.handler;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.chat.server.protocol.IMMessage;
import io.chat.server.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChatClientHandler extends ChannelInboundHandlerAdapter{
	private String nickName;
	private ChannelHandlerContext ctx;

	public ChatClientHandler(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
		System.out.println(msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.ctx = ctx;
		//首先要登录
		IMMessage message = new IMMessage(IMP.LOGIN.toString(),System.currentTimeMillis(),this.nickName);
		send(message);
		System.out.println("成功连接至服务器，已执行登录动作");
		session();
	}

	private void session() {
//		ExecutorService es = Executors.newCachedThreadPool();
//		Runnable r = () -> {
			System.out.println(nickName + ",你好，请在控制台输入消息内容");
			IMMessage message = null;
			Scanner scan = new Scanner(System.in);
			do {
				String line = scan.nextLine();
				if ("exit".equals(line)) {
					message = new IMMessage(IMP.LOGOUT.toString(), System.currentTimeMillis(), nickName);
				} else {
					message = new IMMessage(IMP.CHAT.toString(), System.currentTimeMillis(), nickName, line);
				}
			} while (send(message));
			scan.close();
//		};
//		es.shutdown();
	}

	/**
	 * 往服务器端发送消息
	 * @param msg
	 * @return
	 */
	private boolean send(IMMessage message) {
		this.ctx.writeAndFlush(message);
		System.out.println("消息已发送至服务器,请继续输入");
		return IMP.LOGOUT.toString().equals(message.getCmd())?false:true;
	}

}
