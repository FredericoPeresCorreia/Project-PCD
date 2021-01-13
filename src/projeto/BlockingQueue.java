package projeto;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

	private int limit;
	private Queue<T> queue = new LinkedList<T>();
	private byte[] fileContents;

	public BlockingQueue(int i, byte[] fileContents) {
		if (i >= 0)
			this.limit = i;
		this.fileContents = fileContents;
	}

	public synchronized void offer(T e) throws InterruptedException {
		while (queue.size() >= limit)
			wait();
		queue.add(e);
		notifyAll();
	}

	public synchronized T poll() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		notifyAll();
		return queue.poll();
	}

	public synchronized int size() {
		return queue.size();
	}
	
	public synchronized byte[] getFileContents() {
		return fileContents;
	}

	public synchronized void clear() {
		queue.clear();
	}
}
