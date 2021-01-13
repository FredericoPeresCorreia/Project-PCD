package projeto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class TheISCTEBay extends Thread{
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private String nome;
	private int port;
	private ObjectOutputStream userOut;
	private ObjectInputStream userIn;
	private File[] fileList;
	private ArrayList<ArrayList<String>> filesReceived = new ArrayList<>();
	private String hostAddress;
	private UserData client;
	private ArrayList<UserData> otherUsers;
	private PeerConnectionServer server;
	private File path;
	private String keyword;
	private static String newline = System.getProperty("line.separator");


	public TheISCTEBay(Socket socket, int port ,String path) {
		this.port = port;
		this.path = new File(path);
		this.socket=socket;		
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	//Métodos básicos, setups, getters etc. ...
	
	public void setKeyword(String text) {
		keyword = text; 		
	}
	
	public String getKeyword(){
		return keyword;
	}
	
	public synchronized void addUsers(UserData newUser) throws UnknownHostException  {
		boolean repeatUser = false;
		if((client.getAddress().equals(newUser.getAddress()) && client.getPort() == newUser.getPort())){
			repeatUser = true;
		}
		if(!repeatUser) {
			for (UserData u : otherUsers) {
				if((u.getAddress().equals(newUser.getAddress()) && u.getPort() == newUser.getPort())) {
					repeatUser = true;
				}
			}
		}
		if(!repeatUser) {
			otherUsers.add(newUser);
		}
		
	}
	
	public synchronized void setupClient() throws IOException {
		while(nome == null) {
			nome = JOptionPane.showInputDialog(null, "Inscricao insira o seu nome");
		}
		client = new UserData(nome, InetAddress.getByName(hostAddress),port);
		userOut = new ObjectOutputStream(socket.getOutputStream());
		userOut.writeObject(client);
		userIn = new ObjectInputStream(socket.getInputStream());
		fileList = path.listFiles();
		DownloadWindow w = new DownloadWindow(this);
		w.open();
		server = new PeerConnectionServer(client.getPort(),this);
		server.start();
	}
	
	public String getNome() {
		return nome;
	}
	
	//Métodos para tratar dos sinais que se enviam

	public synchronized void transmitData(String data) {
		switch(data) {
		case("CLT"):
			fileList = path.listFiles();
			recieveUsers();
			break;
		case("SCH"):
			searchFiles();
			break;
		case("DC"):
			out.println("DC");
		}
	}

			
			
	public synchronized void  searchFiles() {
		clearFilesReceived();
		transmitData("CLT"); 
		for(UserData u : otherUsers) {
				PeerConnectionClient search = new PeerConnectionClient(u,this);
				search.start();
		}
	}
	
	public synchronized void recieveUsers() {
		otherUsers = new ArrayList<>();
		try {
			out.println("CLT");
			UserData otherUser =(UserData) userIn.readObject();
			while(!(otherUser.getPort() == 0)) {
				addUsers(otherUser);
				otherUser = (UserData) userIn.readObject();
			}
			for(UserData u : otherUsers) {
				System.out.println("CLT " + u.getName() + " " + u.getPort() + " " + u.getAddress());
			}
			System.out.println("END");
		} catch(IOException e) {
			e.printStackTrace();
		}
		 catch(ClassNotFoundException e) {
			 e.printStackTrace();
		}
	}

	
	@Override
	public void run() {	
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			if (this.path.canRead() == false) {
				System.out.println("Diretorio inexistente");
				transmitData("DC");
				JOptionPane.showMessageDialog(null, "O diret�rio(nome da pasta dos seus ficheiros) n�o existe");
			} 
			else if(in.readLine().equals("OK") && this.path.canRead() == true) 
				setupClient();
			else {
				JOptionPane.showMessageDialog(null, "Pedimos desculpa pelo incómodo mas o servidor tá cheio");
			}
		} catch (IOException e) {
			transmitData("DC");
		}
	}
		


	public static void main(String[] args) throws UnknownHostException, IOException {
		InetAddress Ip  = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);
		int clientPort = Integer.parseInt(args[2]);
		String path = args[3];
		TheISCTEBay c = new TheISCTEBay(new Socket(Ip, serverPort),clientPort, path);
		c.start();
	}

	public File[] getFiles() {
		return fileList;
	}
	
	public synchronized void addFilesReceived(ArrayList<String> f) {
			filesReceived.add(f);
	}

	public synchronized ArrayList<String> getFileNames() {
		ArrayList<String> fileNames = new ArrayList<>();
		for (File f: fileList) {
			fileNames.add(f.getName());
		}
		return fileNames;
	}	
	
	public synchronized ArrayList<String> getFilesToDisplay(){
		ArrayList<String> fileNames = new ArrayList<>();
		for(ArrayList<String> list : filesReceived) {
			for(String s : list) {
				boolean repeat = false;
				for(String str : fileNames) {
					if(str.split(" ")[0].equals(s.split(" ")[0]) && str.split(" ")[1].equals(s.split(" ")[1])) {
						repeat = true;
					}
				}
				for(File f : fileList) {
					if(f.getName().equals(s.split(" ")[0]) && f.length() == Integer.parseInt(s.split(" ")[1])) {
						repeat = true;
					}
				}
				if(!repeat) {
					fileNames.add(s);
				}
			}
		}		
			return fileNames;
	}


	public synchronized void clearFilesReceived() {
		filesReceived = new ArrayList<>();
	}
	
	public synchronized ArrayList<Integer> getUsers(String size, String name){
		ArrayList<Integer> users = new ArrayList<>();
		for(ArrayList<String> userFile : filesReceived) {
			for(String file : userFile) {
				System.out.println(file.split(" ")[0] + " " + file.split(" ")[1]);
				if(file.split(" ")[0].equals(name) && file.split(" ")[1].equals(size)){
					int userPort = Integer.valueOf(file.split(" ")[2]);
					users.add(userPort);
				}
			}
		}
		return users;
	}
	
	public synchronized UserData getRandomUser(ArrayList<Integer> availableUsers) {
		int randomUser =(int) (Math.random() * availableUsers.size());
			port = availableUsers.get(randomUser);
		
		for(UserData u : otherUsers) {
			if(u.getPort() == port)
				return u;
		}
		try {
			return new UserData("", InetAddress.getByName(""), 0);
		} catch (UnknownHostException e) {
			return getRandomUser(availableUsers);
		}
	}

	public synchronized void downloadFile(String file) { 
		Timer timer = new Timer(); 
		TimerTask task = new TimerTask() { 
			@Override public void run() { 
				interrupt(); 
			}
		}; 
		String text =""; 
		String size = file.split(" ")[1]; 
		String name = file.split(" ")[0]; 
		System.out.println(size + name); 
		byte[] fileContents = new byte[Integer.parseInt(size)]; 
		ArrayList<Integer> availableUsers = getUsers(size, name); 
		int BLOCK_SIZE = 1024; 
		int numberOfParts = Integer.parseInt(size) / BLOCK_SIZE + 1; 
		BlockingQueue<FileBlockRequestMessage> queue = new BlockingQueue<>(numberOfParts, fileContents); 
		for(int i = 0 ; i < numberOfParts; i++) { 
			try{ 
				timer.schedule(task, 5000); 
			} catch (IllegalStateException e) {} 
			if(i == numberOfParts -1) { 
				int length = Integer.parseInt(size) % BLOCK_SIZE; 
				if(length > 0) { 
					UserData u = getRandomUser(availableUsers); FileBlockRequestMessage lastBlockRequest = new FileBlockRequestMessage(u, name, BLOCK_SIZE * i, length); 
					try { 
						queue.offer(lastBlockRequest); text+=lastBlockRequest+newline; 
					} catch (InterruptedException e) { 
						JOptionPane.showMessageDialog(null,"Download Bloqueou. Foi cancelado"); 
						return; 
					}
				} 
			} else { 
				UserData u = getRandomUser(availableUsers); 
				FileBlockRequestMessage blockRequest = new FileBlockRequestMessage(u, name,BLOCK_SIZE * i, BLOCK_SIZE); 
				try { 
					queue.offer(blockRequest); 
					text+=blockRequest+newline; 
				} catch (InterruptedException e) { 
					JOptionPane.showMessageDialog(null, "Download Bloqueou. Foi cancelado"); 
					return; 
				} 
			} 
		} 
		while(queue.size()>0) { 
			DownloadThread t = new DownloadThread(queue); 
			t.start(); 
			try { 
				t.join(); 
			} catch (InterruptedException e) { 
				JOptionPane.showMessageDialog(null, "Download Bloqueou. Foi cancelado"); 
				return; 
			} 
		} try { 
			timer.cancel(); 
			Files.write(Paths.get(path.getPath()+ "/"+ name),fileContents); 
			JOptionPane.showMessageDialog(null, text); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	}
	
}

