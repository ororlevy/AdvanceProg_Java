package test_server;

import server_side.CacheManager;
import server_side.FileCacheManager;
import server_side.MyClientHandler;
import server_side.MySerialServer;
import server_side.Server;

public class TestSetter {
	

	static Server s;
	
	public static void runServer(int port) {
		// put the code here that runs your server
		s=new MySerialServer(); // initialize
		CacheManager cm=new FileCacheManager();
		MyClientHandler ch=new MyClientHandler(cm);
		s.open(port,ch);
	}

	public static void stopServer() {
		s.stop();
	}
	

}
