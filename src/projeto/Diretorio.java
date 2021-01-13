package projeto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Diretorio extends Thread {
	public final static int MAX_CONNECTED_USERS = 10;
	private ServerSocket server;
	private ArrayList<UserData> connectedUsers;
	private boolean isFirstConnection;
	
	public Diretorio() throws IOException {
		server = new ServerSocket(8080);
		connectedUsers = new ArrayList<>();
		isFirstConnection = true;
	}


	public static void main(String[] args) { 
		System.out.println("Server");
		try{
			Diretorio d = new Diretorio();
			while (true) {
				System.out.println("Conectados: " + d.connectedUsers.size());
				System.out.println("Espa�o disponivel para novas ligacoes");
				Socket socket = d.server.accept(); 
				if (d.connectedUsers.size() == 0 && !d.isFirstConnection) {
					d.server.close();
					break;
				} else {
					ConnectionHandlerDiretorio c = new ConnectionHandlerDiretorio(socket, d.connectedUsers);
					c.start();
				}
				if(d.isFirstConnection)
					d.isFirstConnection = false;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Servidor fim");
	}
}