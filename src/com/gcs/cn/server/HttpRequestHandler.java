package com.gcs.cn.server;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestHandler implements Runnable {

	private SocketChannel connection;
	private final Object lock;
	private String format = "text/plain";
	private String body;
    private String path = "/";

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
			String request = ServerUtil.getRequest(connection);
		
			String response = ServerUtil.parseAndReturnResponse(request);
			
			
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

	public static String getHandler() {
		// TODO Auto-generated method stub
		 List<File> files = new ArrayList<>();
	        List<String> fileNames = new ArrayList<>();
	        ServerUtil.getFiles(httpfs.directory, files, fileNames);

	        body= "";

	        String[] pathArr = path.split("/");
	        if(!path.isEmpty() && path.equals("/")){
	            switch (format) {
	                case "application/json":
	                    body = ServerUtil.toJSONString(fileNames);
	                    break;
	                case "application/xml": {
	                    StringBuilder b = new StringBuilder();
	                    b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	                    b.append("<files>");
	                    for (String file : fileNames) {
	                        b.append("<file>").append(file).append("</file");
	                    }
	                    b.append("</files>");
	                    body = b.toString();
	                    break;
	                }
	                case "text/html": {
	                    StringBuilder b = new StringBuilder();
	                    b.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Files on Server</title>")
	                            .append("</head><body><h2>File List on Server</h2><ul>");
	                    for (String file : fileNames) {
	                        b.append("<li>").append(file).append("</li>");
	                    }
	                    b.append("<ul></body></html>");
	                    body = b.toString();
	                    break;
	                }
	                default:
	                    body = String.join("\n", fileNames);
	                    break;
	            }
	            return ServerUtil.responseGenerator(200, body, null);
	        } else if (path.split("/").length >= 3) {
	            body = "401 Unauthorized.\n" +
	                    "The requested URL " + path + " cannot be accessed.\n" +
	                    "The requested file is located outside the working directory.";
	            return ServerUtil.responseGenerator(401, body, null);
	        }else{
	            String fileName = path.split("/")[1];
	            String contentDisposition = null;
	            if(fileNames.contains(fileName) || fileNames.contains(fileName+".txt")){
	                String fileContent;
	                if(!fileName.contains(".txt")){
	                    fileName = fileName + ".txt";
	                }
	                Path filePath = Path.of(httpfs.directory+ "/"+ fileName);
	                try {
	                    synchronized (lock){
	                        //instead of file content save it back to file
	                        body += Files.readString(filePath);
	                    }

	                    contentDisposition = "Content-Disposition: attachment; filename=" + fileName;

	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                return ServerUtil.responseGenerator(200, body, contentDisposition);
	            }else{
	                body = "404. There is an error.\n";
	                body += "The requested URL " + path + " was not found on this server.\n";
	                body += "That is all we know.";
	                return ServerUtil.responseGenerator(404, body, null);
	            }

	        }

		return null;
	}

}
