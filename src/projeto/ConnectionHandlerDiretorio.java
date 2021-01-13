package projeto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ConnectionHandlerDiretorio extends Thread{
	
	private Socket socket;										
	private List<UserData> connectedUsers;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectInputStream userIn;
	private ObjectOutputStream userOut;
	private UserData user;

	public ConnectionHandlerDiretorio(Socket socket, List<UserData> users) {
		this.socket=socket;
		this.connectedUsers = users;
	}	

	private synchronized void showList(){
		try {
			for(UserData u : connectedUsers) {
				userOut.writeObject(u);
			}
			userOut.writeObject(new UserData("",InetAddress.getByName(""),0));
		} catch(IOException e) {
			e.printStackTrace();
		}
			
	}
	
	private void handleMessage(String message) {
		switch (message) {
		case("CLT"):
			showList();
			break;
		case("DC"):
			killThread();
			break;
		}
	}

	public synchronized void addToList(UserData u) { 
		connectedUsers.add(u);
		for(UserData us : connectedUsers)
			System.out.println(us);
		if(connectedUsers.size()>Diretorio.MAX_CONNECTED_USERS) {
			killThread();
		} 
	}
	
	public synchronized void removeFromList(UserData u) {
		connectedUsers.remove(u);
	}
	
	public void killThread() {
		System.out.println("dc");
		removeFromList(user);
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		this.interrupt();
	}
	

	@Override
	public void run() {
		try {
			System.out.println("1");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);	 
			if(checkLimit()) {
				out.println("OK");	
				userIn = new ObjectInputStream(socket.getInputStream());
				userOut = new ObjectOutputStream(socket.getOutputStream());
				user = (UserData)userIn.readObject();
				System.out.println(user.getName() +"'s server port "+ user.getPort());
				addToList(user);
			} else {
				out.println("fail");
				killThread();
			}
			while(!interrupted()) {
				String message = in.readLine();
				System.out.println(message);
				handleMessage(message);
			}
		} catch (IOException e) { 
			killThread();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	private boolean checkLimit() {
		return !(connectedUsers.size()>= Diretorio.MAX_CONNECTED_USERS);
	}

}
