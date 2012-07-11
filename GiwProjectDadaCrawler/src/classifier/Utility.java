package classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utility {
	public static String fileToString(String filePath) {
		BufferedReader reader = null;
		StringBuffer fileData = null;
		try {
			fileData = new StringBuffer(1000);
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			return fileData.toString();
		} catch (IOException ex) {
			System.out.println("Eccezione in readFileAsString: " + ex.toString());
			return null;
		}
	}
}
