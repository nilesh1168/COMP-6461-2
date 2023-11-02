package com.gcs.cn.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class HttpRequestHandler implements Runnable {

	private SocketChannel connection;
	private Object lock;

	public HttpRequestHandler(SocketChannel connection, Object lock) {
        this.connection = connection;
        this.lock = lock;
    }
	
	@Override
	public void run() {
		System.out.println("--------------------------------");
        try {
            System.out.println("New client from" + connection.getLocalAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("--------------------------------");
        
        try {
			System.out.println(ServerUtil.getRequest(connection));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
		}
        
	}

}
