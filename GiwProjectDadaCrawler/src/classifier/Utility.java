package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
/*
* @author Antonio Gallo
* @author Daniele D'Andrea
* @author Antonio Tedeschi
* @author Daniele Malta
*/
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
	public static List<String> listFiles(String dir) throws IOException
	{
		List<String> listaFile= new LinkedList<String>();
		File file = new File(dir);
		if(file.isDirectory())
		{
			File[] filesInDir = file.listFiles();
			Arrays.sort(filesInDir);
			for(File f : filesInDir)
			{
				if(f.isFile()){
					listaFile.add(f.toString());
				}
				else if(f.isDirectory()){
					listaFile.addAll(listFiles(f.toString()));
				}
			}
		}
		return listaFile;
	}
	//da una lista di stringhe di un path ricrea il path con ->
	public static String listToBreadcrumb(List<String> lista){
		String path = "";
		for (String string : lista) {
			path= path+string+" -> ";
		}
		if(path.length()==0)
			return path;
		
		return path.substring(0, path.length()-1-3);
	}
}
