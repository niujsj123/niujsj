package io.chat.processor;

import com.alibaba.fastjson.JSONObject;

import io.chat.server.protocol.IMMessage;
import io.chat.server.protocol.IMP;
import io.chat.server.protocol.ImDecoder;
import io.chat.server.protocol.ImEncoder;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 专门处理消息相关逻辑（消息分发）
 * 
 * @author niujsj
 *
 */
public final class MsgProcessor {

	// 记录在线用户
	private static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 定义一些自定义属性
	private final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
	private final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
	private final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");

	ImDecoder decoder = new ImDecoder();
	ImEncoder encoder = new ImEncoder();

	// 把消息分发到每一个连接到服务器上的客户端
	/**
	 * 将消息分发到所有的在线用户
	 * 
	 * @param client
	 * @param msg
	 */
	public void sendMsg(Channel client, IMMessage msg) {
		// 分发消息主要是分发到我们的网页里面去
		if (null == msg) {
			return;
		}
		// 开始分发了吧,声明一个容器，来保存所有的在线用户
		if (IMP.LOGIN.getName().equals(msg.getCmd())) {
			// 我们自己加的一些东西放入到自定的扩展属性里面去
			client.attr(NICK_NAME).getAndSet(msg.getSender());
			client.attr(IP_ADDR).getAndSet(getIpAddr(client));
			onlineUsers.add(client);
			// 扫描所有的在线用户，通知某某上线了
			for (Channel ch : onlineUsers) {
				if (ch == client) {
					msg = new IMMessage(IMP.SYSTEM.toString(), System.currentTimeMillis(), "已与服务器建立连接",
							onlineUsers.size());
				} else {
					msg = new IMMessage(IMP.SYSTEM.toString(), System.currentTimeMillis(), getNickName(client) + "加入",
							onlineUsers.size());
				}
				String content = encoder.encode(msg);
				client.writeAndFlush(new TextWebSocketFrame(content));
			}
		} else if (IMP.CHAT.getName().equals(msg.getCmd())){
			for (Channel ch : onlineUsers) {
				if (client != ch){
					msg.setSender(getNickName(client));
				} else {
					msg.setSender("you");
				}
				msg.setTime(System.currentTimeMillis());
				String content = encoder.encode(msg);
				ch.writeAndFlush(new TextWebSocketFrame(content));
			}
		} else if(IMP.FLOWER.getName().equals(msg.getCmd())){
			//非正常的情况下，就频繁刷花，导致整个屏幕一直是鲜花特效
			//影响聊天效果
			//这时候，我们就要加上一个限制，规定1分钟之内，每个人只能刷一次鲜花
			JSONObject attrs = getAttrs(client);
			//如果为空，就表示这个人从来没有送过鲜花
			if (null !=attrs){
				//就开始判断上次送花时间
				long lastFlowerTime = attrs.getLongValue("lastFlowerTime");
				int seconds = 60; //60秒之内不能重复送花
				long sub = System.currentTimeMillis() - lastFlowerTime;
				if (sub < 1000 * seconds){
					msg.setSender("you");
					msg.setCmd(IMP.SYSTEM.getName());
					msg.setOnline(onlineUsers.size());
					msg.setContent("您送鲜花太频繁,请"+ (seconds - (sub/1000)) +"秒后再试");
					String content = encoder.encode(msg);
					client.writeAndFlush(new TextWebSocketFrame(content));
					return;
				}
			}
			//正常的送花流程
			for (Channel ch : onlineUsers) {
				if (ch != client){
					msg.setSender(getNickName(client));
					msg.setContent(getNickName(client) + "送来一波鲜花");
				} else {
					msg.setSender("you");
					msg.setContent("你给大家送了一波鲜花");
					setAttrs(client, "lastFlowerTime", System.currentTimeMillis());
				}
				msg.setTime( System.currentTimeMillis());
				String content = encoder.encode(msg);
				ch.writeAndFlush(new TextWebSocketFrame(content));
			}
		}
	}
	/**
	 * 往扩展属性添加自定义key
	 * @param client
	 * @param key
	 * @param value
	 */
	private void setAttrs(Channel client, String key, long currentTimeMillis) {
		JSONObject jsonObject = getAttrs(client);
		if(jsonObject == null){
			jsonObject = new JSONObject();	
		}
		jsonObject.put(key, currentTimeMillis);
		client.attr(ATTRS).set(jsonObject);
		
	}
	/**
	 * 获得扩展属性
	 * @param client
	 * @return
	 */
	private JSONObject getAttrs(Channel client) {
		return client.attr(ATTRS).get();
	}

	private String getNickName(Channel client) {
		return client.attr(NICK_NAME).get();
	}

	private String getIpAddr(Channel client) {
		return client.remoteAddress().toString().replaceFirst("/", "");
	}

	/**
	 * 将消息分发到所有的在线用户(重载+1)
	 * 
	 * @param client
	 * @param msg
	 */
	public void sendMsg(Channel client, String msg) {
		sendMsg(client, decoder.decode(msg));
	}
	
	/**
	 * 如果有用户退出的话，就从容器中将这个用户去掉，并且将在线人数减1
	 * @param client
	 */
	public void logout(Channel client) {
		if(getNickName(client) == null){ return; }
		onlineUsers.forEach(online->{
			IMMessage msg = new IMMessage(IMP.SYSTEM.toString(), System.currentTimeMillis(), onlineUsers.size(),getNickName(client) + "退出"
					);
			String content = encoder.encode(msg);
			online.writeAndFlush(new TextWebSocketFrame(content));
		});
		onlineUsers.remove(client);
	}
}
