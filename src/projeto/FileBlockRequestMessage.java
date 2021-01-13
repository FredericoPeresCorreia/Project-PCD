package projeto;

import java.awt.List;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class FileBlockRequestMessage implements Serializable{

		private String fileName;
		private int offSet;
		private int lenght;
		private File f;
		private UserData supplier;
		
		public FileBlockRequestMessage(UserData supplier, String fileName, int offSet, int lenght)  {
			this.supplier = supplier;
			this.fileName = fileName;
			this.offSet = offSet;
			this.lenght = lenght;
		}

		public String getFileName() {
			return fileName;
		}

		public int getOffSet() {
			return offSet;
		}

		public int getLenght() {
			return lenght;
		}

		public UserData getSupplier() {
			return supplier;
		}

		@Override
		public String toString() {
			return "FileBlockRequestMessage [fileName=" + fileName + ", offSet=" + offSet + ", lenght=" + lenght
					+ ", supplier=" + supplier + "]";
		}
		
		
		

}
