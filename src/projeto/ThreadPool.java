package projeto;

import java.util.LinkedList;

public class ThreadPool {	
	private LinkedList<FileBlockRequestMessage> queue;
	private static final int MAX_REQUESTS = 5;
	
	public ThreadPool() {
		queue = new LinkedList<>();
	}
	
	public synchronized void submit(FileBlockRequestMessage request) {
		try {
			while(queue.size()>MAX_REQUESTS) {
				wait();
			}
			queue.offer(request);
			notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized FileBlockRequestMessage dispatchMessage() {
		try {
			while(queue.size() == 0) {
				wait();
			}
			FileBlockRequestMessage request = queue.removeFirst();
			notifyAll();
			return request;
		} catch (InterruptedException e) {
			return dispatchMessage();
		}
	}
	
}

