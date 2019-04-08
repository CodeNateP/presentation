package backendServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Instance extends Thread{
	
	private final Socket clientSocket;

	private String username = null;
	private final Connection server;
	private OutputStream oStream;
	
	public Instance(Connection server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			clientSocketConnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void clientSocketConnect() throws IOException, InterruptedException {
		InputStream iStream = clientSocket.getInputStream();
		this.oStream = clientSocket.getOutputStream();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
		String line;
		while ( (line = reader.readLine()) != null) {
			String[] part = StringUtils.split(line);
			if(part !=null && part.length > 0) {
				String cmd = part[0];
				if (cmd.equals("logoff")) {
					handleLogoff();
					break;
				}else if(cmd.equals("login")) {
					clientLogin(oStream, part);
				}else if(cmd.equals("msg")){
					String[] tokensMsg = StringUtils.split(line, null, 3);
					handleMessage(tokensMsg);
					}else {
					String msg = "unknown " + cmd + "\n";
					oStream.write(msg.getBytes());
				}
				String msg = "\nYou typed: " + line + "\n";
				oStream.write(msg.getBytes());
				
			}
		}
		oStream.close();
	}
	private void handleMessage(String[] part) throws IOException {
		if (part.length == 3) {
			String username = part[1];
			String body = part[2];
			List<Instance> instanceList = server.getinstanceList();
			for(Instance client : instanceList) {
						//compiles message to that user
						String outMsg = "msg " + username + " " + body + "\n";
						//sends to outMsg method
						client.send(outMsg);
			}
		}
	}
	private void handleLogoff() throws IOException{
		List<Instance> instanceList = server.getinstanceList();
		
		String onLineMsg = "Offline " + username + "\n";
		for(Instance client : instanceList) {
			if (!username.equals(client.getLogin())) {
				client.send(onLineMsg);
			}
		}
		
		System.out.print("User: " + username + " logged off!");
		oStream.close();
	}
	
	public String getLogin() {
		return username;
	}
	
	private void clientLogin(OutputStream oStream, String[] parts) throws IOException {
		if(parts.length == 3) {
			String login = parts[1];
			String password = parts[2];
			
			if ((login.equals("admin") && password.equals("password")) || (login.equals("nathan") && password.equals("password")) ||  (login.equals("nate") && password.equals("password"))) {
				String msg = "ok login";
				oStream.write(msg.getBytes());
				this.username = login;
				System.out.println("User logged in: " + login);
				
				List<Instance> instanceList = server.getinstanceList();
				
				for(Instance client : instanceList) {
					if (client.getLogin() != null) {
						if (!login.equals(client.getLogin())) {
							String msg2 = "\nOnline " + client.getLogin() + "\n";
							send(msg2);
						}
					}
				}
				String onLineMsg = "online " + login + "\n";
				for(Instance client : instanceList) {
					if (!login.equals(client.getLogin())) {
						client.send(onLineMsg);
					}
				}
			}else {
				String msg = "error login";
				oStream.write(msg.getBytes());
				System.err.println("Login failed for " + login);
			}
		}
	}
	private void send(String msg) throws IOException {
		if (username != null) {
		oStream.write(msg.getBytes());
		}
	}
}
