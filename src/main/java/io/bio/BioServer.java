package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

	ServerSocket server;
	
	
	
	public BioServer(int port) {
		try {
			this.server = new ServerSocket(port);
			System.out.println("Server start,port:"+port);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}



	public static void main(String[] args) {
		
       new BioServer(8088).listener();
	}



	private void listener() {
		while(true){
			InputStream inputStream = null;
			try {
				Socket socket = this.server.accept();
				inputStream = socket.getInputStream();
				byte[] bytes = new byte[1024];
				int len = inputStream.read(bytes);
				if (len>0){
					String msg = new String(bytes,0,len);
					System.out.println("receive msg："+msg);
				}
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} finally {
				if (null!=inputStream)
					try {
						inputStream.close();
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
			}
		}
		
	}

}
