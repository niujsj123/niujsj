package io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BioClient {

	public static void main(String[] args) {
		Socket socket =null;
		OutputStream stream =null;
		try {
			socket = new Socket("127.0.0.1", 8088);
			stream = socket.getOutputStream();
			String msg  = "报个到";
			stream.write(msg.getBytes("utf-8"));
			System.out.println("send msg:"+msg);
		} catch (UnknownHostException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}

	}

}
