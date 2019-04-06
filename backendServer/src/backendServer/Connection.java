package backendServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Connection extends Thread {
	
	private final int sPort;
	
	private ArrayList<Instance> instanceList = new ArrayList<>();
	
	public Connection(int sPort) {
		this.sPort = sPort;
	}
	
	public static void main(String[] args) {
		int port = 1999; 
		Connection server = new Connection(port);
		server.start();
	}
	
	public List<Instance> getinstanceList(){
		return instanceList;
	}
	
	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket cSocket = new ServerSocket(sPort);
			while(true) {
				System.out.println("Waiting for a new client connection...");
				Socket clientSocket = cSocket.accept();
				System.out.println("Accepted connection from: " + clientSocket);
				Instance client = new Instance (Connection.this, clientSocket);
				instanceList.add(client);
				client.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
