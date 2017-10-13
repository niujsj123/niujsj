package io.chat.server.protocol;

import org.msgpack.annotation.Message;

/**
 *  自定义协议消息体
 * @author niujsj
 *
 */
@Message
public final class IMMessage {

	private String addr; //ip,port
	private String cmd;  //命令类型，如[SYSTEM],[CHAT]
	private long time;  //发送时间
	private String sender; //发送人
	private String receiver; //接收人
	private String content; //消息内容
	private int online; //在线人数
	
	
	public IMMessage() {
		
	}
	public IMMessage(String cmd, long time, String sender) {
		this.cmd = cmd;
		this.time= time;
		this.sender=sender;
	}
	public IMMessage(String cmd, long time, String sender, String content) {
		this.cmd = cmd;
		this.time= time;
		this.sender=sender;
		this.content = content;
	}
	
	public IMMessage(String cmd, long time, String content, int online) {
		this.cmd = cmd;
		this.time = time;
		this.content = content;
		this.online = online;
	}
	public IMMessage(String cmd, long time, String sender, String receiver, String content) {
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
	}
	public IMMessage(String cmd, long time, int online, String content) {
		this.cmd = cmd;
		this.time = time;
		this.online = online;
		this.content = content;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
	
	
}
