package projeto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadThread extends Thread {
	
	private int n;
	FileBlockRequestMessage f;
	String path;
	byte[] bytes;
	BlockingQueue<FileBlockRequestMessage> queue;
	
	public DownloadThread(BlockingQueue<FileBlockRequestMessage> queue) {
		try {
			this.queue = queue;
			f = queue.poll();
			bytes = queue.getFileContents();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void run() {
		try {
			Timer time = new Timer();	
			time.schedule(new TimerTask() {
				
				@Override
				public void run() {
					interrupt();
					try {
						queue.offer(f);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			},4000);
			Socket socket = new Socket(InetAddress.getByName(f.getSupplier().getAddress()),f.getSupplier().getPort());
			ObjectOutputStream outpu = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inpu = new ObjectInputStream(socket.getInputStream());
			outpu.writeObject(f);
			try {
				FilePart parte = (FilePart) inpu.readObject();
				for(int i = 0; i < parte.getBytes().length; i++) {
					bytes[f.getOffSet()+i] = parte.getBytes()[i];
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileBlockRequestMessage getF() {
		return f;
	}

}
