package projeto;

import java.io.Serializable;

public class FilePart implements Serializable {


	private byte[] bytes;

	public FilePart(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}


}
	