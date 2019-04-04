package backendServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Connection extends Thread {
	
	private final int sPort;
	
	private ArrayList<Instance> workerList = new ArrayList<>();
	
	public Connection(int sPort) {
		this.sPort = sPort;
	}
	
	public List<Instance> getWorkerList(){
		return workerList;
	}
	
	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket cSocket = new ServerSocket(sPort);
			while(true) {
				System.out.println("Waiting for a client...");
				Socket clientSocket = cSocket.accept();
				System.out.println("Accepted connection from: " + clientSocket);
				Instance client = new Instance (Connection.this, clientSocket);
				workerList.add(client);
				client.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
