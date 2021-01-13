package projeto;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class FileDetails implements Serializable {
	
	private ArrayList<String> files;
	
	public FileDetails() {
		this.files = new ArrayList<>();
	}
	
	public synchronized void addFile(File f, int port) {
		files.add(f.getName() + " " + f.length() + " " + port);
	}
	
	public ArrayList<String> getFiles() {
		return files;
	}

	
	
}
