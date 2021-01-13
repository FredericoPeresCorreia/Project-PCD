package projeto;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class PeerConnectionServer extends Thread {
	
	private ServerSocket server;
	private TheISCTEBay client;
	private Socket socket;
	private ObjectOutputStream messageOut;
	private ObjectInputStream messageIn;
	private int port;
	private ThreadPool poll;

	public PeerConnectionServer(int port,TheISCTEBay client){
		try {
			this.port = port;
			this.server = new ServerSocket(port);
			this.client = client;
			this.poll = new ThreadPool();
		} catch (IOException e) {client.transmitData("DC");}
	}

	@Override
	public  synchronized void run() {
		try {
			while(true) {	
				socket = server.accept();
				messageOut = new ObjectOutputStream(socket.getOutputStream());
				messageIn = new ObjectInputStream(socket.getInputStream());
				Object objectIn = messageIn.readObject();
				if(objectIn instanceof WordSearchMessage) {
					search(objectIn);
				} else if( objectIn instanceof FileBlockRequestMessage) {
					sendBack(objectIn);
				}
				socket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void search(Object o) throws IOException {
		WordSearchMessage message = (WordSearchMessage)o;
		FileDetails files = new FileDetails();
		File[] ownFiles = client.getFiles();
		for(File f : ownFiles) {
		if(f.getName().contains(message.getKeyword())) 
			files.addFile(f,port);		
		}
		messageOut.writeObject(files);
	}
	
	public synchronized void sendBack(Object o) throws IOException {
		poll.submit((FileBlockRequestMessage)o);
		FileBlockRequestMessage fileRequest = poll.dispatchMessage();
		for(File f: client.getFiles()) {
			if(f.getName().equals(fileRequest.getFileName())) {
				byte [] bytes = new byte[fileRequest.getLenght()];
				byte [] localFile = Files.readAllBytes(f.toPath());
				for(int i = 0; i < fileRequest.getLenght();i++)
					bytes[i] = localFile[fileRequest.getOffSet()+i];
				FilePart part = new FilePart(bytes);
				messageOut.writeObject(part);
			}
		}
	}
	
	
}
	