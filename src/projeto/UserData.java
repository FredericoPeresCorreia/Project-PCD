package projeto;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class UserData implements Serializable{
	private String name;
	private String address;
	private int port;
	
	public UserData(String name, InetAddress address, int port) { 
		this.name = name;
		this.address = address.toString().split("/")[1];;
		this.port = port;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getAddress() {
		return address;
	}
	
	@Override
	public String toString() {
		return "UserData [name=" + name + ", address=" + getAddress() + ", port=" + getPort() + "]";
	}	
	
}