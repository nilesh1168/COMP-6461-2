package com.gcs.cn.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

	public static HttpServer optionsParser(List<String> args) {
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
        
        return new HttpServer(args.get(0), port, verbose, directory);
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

}
