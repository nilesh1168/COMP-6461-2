package com.gcs.cn.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpServer implements Runnable{
    String host;
    int port;
    boolean verbose;
    String directory;
    Object lock = new Object();


    private ServerSocketChannel socket;
    public HttpServer(String url, int port, boolean verbose, String directory) {
        this.port = port;
        this.verbose = verbose;
        this.directory = directory;
        parseURL(url);
    }


    private void parseURL(String url) {
		try {
			URL strURL = new URL(url);
			this.host = strURL.getHost();
			this.port = strURL.getPort();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}


	@Override
    public void run() {
        try {
            socket = ServerSocketChannel.open();
            System.out.println("Starting HTTPServer at localhost:" + port);
            socket.bind(new InetSocketAddress(port));
            while(true){
                SocketChannel connection = socket.accept();
                Thread t = new Thread(new HttpRequestHandler(connection, lock));
                t.start();
            }
        }catch (IOException exception){
            System.out.println(exception.getMessage());
        }

    }
}
