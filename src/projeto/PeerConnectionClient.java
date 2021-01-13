package projeto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class PeerConnectionClient extends Thread {
	private UserData u;
	private Socket socket;
	private TheISCTEBay client;

	
	public PeerConnectionClient(UserData u, TheISCTEBay client) {
		this.u = u;
		this.client = client;
	}


	@Override
	public synchronized void run() {
		try {
			socket = new Socket(InetAddress.getByName(u.getAddress()), u.getPort());
			ObjectOutputStream messageOut = new ObjectOutputStream(socket.getOutputStream());
			messageOut.writeObject(new WordSearchMessage(client.getKeyword()));
			ObjectInputStream messageIn = new ObjectInputStream(socket.getInputStream());
			FileDetails returnedFiles = (FileDetails) messageIn.readObject();
			System.out.println("Recieved: ");
			ArrayList<String> files = new ArrayList<>();
			for(String s : returnedFiles.getFiles()) {
				files.add(s);
				System.out.println(s);
			}
			client.addFilesReceived(files);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
