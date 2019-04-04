package backendServer;

public class Start {
	public static void main(String[] args) {
		int port = 1999; 
		Connection server = new Connection(port);
		server.start();
	}
}


