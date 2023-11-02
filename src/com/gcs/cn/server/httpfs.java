package com.gcs.cn.server;

import java.util.List;

public class httpfs {

	public static void main(String[] args) {
		if(args.length == 1 && args[0].equals("help")){
            ServerUtil.displayHelpInfo();
        } else{
            HttpServer server = ServerUtil.optionsParser(List.of(args));
            server.run();
        }

	}

}
