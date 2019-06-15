package test_server;

import server_side.*;

public class TestSetter {
	

	static Server s;
	
	public static void runServer(int port) {
		// put the code here that runs your server
		s=new MySerialServer(); // initialize
		CacheManager cm=new FileCacheManager();
		MyClientHandler ch=new MyClientHandler(cm);
		s.open(port,new ClientHandlerPath(ch));
	}

	public static void stopServer() {
		s.stop();
	}
	

}
