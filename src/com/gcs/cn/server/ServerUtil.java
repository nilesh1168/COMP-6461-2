package com.gcs.cn.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerUtil {

	public static void displayHelpInfo() {
		String helpInfo = "nhttpfs is a simple file server.\n" +
                "\n" +
                "    Usage:\n" +
                "        httpfs [-v] [-p PORT] [-d PATH-TO-DIR]\n" +
                "\n" +
                "    The commands are:\n" +
                "        -v      Prints debugging messages.\n" +
                "        -p      Specifies the port number that the server will listen and serve at. (Default is 8080)\n" +
                "        -d      Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application.";


        System.out.println(helpInfo);
		
	}

	public static HttpServer optionsParser(List<String> subList) {
		ArrayList<String> args = new ArrayList<>(subList);
		boolean verbose = false;
		int port = 0;
		String directory = "";
		if(args.contains("-v")){
            args.remove("-v");
            verbose = true;
        }
        if(args.contains("-p")){
            int index = args.indexOf("-p");
            args.remove("-p");
            String portString = args.get(index);
            port = Integer.parseInt(portString);
            args.remove(portString);
        }
        if(args.contains("-d")){
            int index = args.indexOf("-d");
            args.remove("-d");
            directory = args.get(index);
            args.remove(directory);
        }
        
        return new HttpServer(verbose, directory, port);
	}

	public static String getRequest(SocketChannel connection) throws IOException {
		StringBuilder b = new StringBuilder();
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int count = 0;
        for (; ; ) {
            if(count > 0)
                break;
            int nr = connection.read(buf);
            if (nr == -1)
                break;

            if (nr > 0) {
                // ByteBuffer is tricky, you have to flip when switch from read to write, or vice-versa
                buf.flip();
                b.append(utf8.decode(buf));
                count++;
                buf.clear();
            }
        }
        return b.toString();
	}

	public static String parseAndReturnResponse(String request) {
		// TODO Auto-generated method stub
		/*
	        * GET /hello.txt? HTTP/1.1
	        Host: localhost
	        User-Agent: Concordia-HTTP/1.0
	        Accept: application/xml
	        * */
	        String[] requestArr = request.split("\r\n\r\n");
	        String[] headers = requestArr[0].split("\r\n");
	        

	        //GET /hello.txt? HTTP/1.1
	        String requestTypeAndUrl = headers[0];
	        String[] requestTypeAndUrlArr = requestTypeAndUrl.split(" ");
	        String requestType = requestTypeAndUrlArr[0];
	        parseUrl(requestTypeAndUrlArr[1]);

	        if(requestArr.length > 1){
	            HttpRequestHandler.body = requestArr[1];
	        }


	        for (String header: headers) {
	            if(header.contains("application/json")){
	            	HttpRequestHandler.format = "application/json";
	                break;
	            }else if(header.contains("text/html")){
	            	HttpRequestHandler.format = "text/html";
	                break;
	            }else if(header.contains("application/xml")){
	            	HttpRequestHandler.format = "application/xml";
	                break;
	            }
	        }

	        if(requestType.equals("GET")){
	            //isGet = true;
	            return HttpRequestHandler.getHandler();
	        }else if(requestType.equals("POST")){
	           // isPost = true;
	            //return HttpRequestHandler.postHandler();
	        }

	        return "Unknown HTTP Method Specified";
	}

	private static void parseUrl(String string) {
		// TODO Auto-generated method stub
		
	}

	public static void getFiles(String directory, List<File> files, List<String> fileNames) {
		// TODO Auto-generated method stub
		File dir = new File(directory);
        File[] fileArr= dir.listFiles();
        if(fileArr != null){
            files.addAll(List.of(fileArr));
            for (File f :fileArr) {
                fileNames.add(f.getName());
            }
        }


        if (fileArr != null && fileArr.length > 0) {
            for (File file : fileArr) {
                // Check if the file is a directory
                if (file.isDirectory()) {
                    // We will not print the directory name, just use it as a new
                    // starting point to list files from
                    getFiles(file.getAbsolutePath(), files, fileNames);
                } else {
                    // We can use .length() to get the file size
                    System.out.println(file.getName() + " (size in bytes: " + file.length()+")");
                }
            }
        }
    }

	public static String toJSONString(List<String> fileNames) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String responseGenerator(int i, String body, Object object) {
		// TODO Auto-generated method stub
		return null;
	}
		
	}

}
