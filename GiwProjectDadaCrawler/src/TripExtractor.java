import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;


public class TripExtractor {
	List<String> files;
	public TripExtractor(){
		this.files=new LinkedList<String>();
	}


	public void listFile(String dir) throws IOException
	{


		File file = new File(dir);
		if(file.isDirectory())
		{
			File[] filesInDir = file.listFiles();
			Arrays.sort(filesInDir);
			int cont=600;

			for(File f : filesInDir)
			{
				//if(cont>0){
				//cont--;
				String prefix = "";

				if(f.isFile()){
					prefix = "[f] ";
					this.files.add(f.toString());
				}
				else if(f.isDirectory()){
					prefix = "[d] ";
					listFile(f.toString());
				}

				//System.out.println(prefix + f.toString());
				//}
			}

		}

		//Set<String> set=u.mapCategory.keySet();
		//for (String string : set) {
		//System.out.println(string+": "+u.mapCategory.get(string));
		//}
		//}


	}


	public static void main(String args[]) throws IOException{
		TripExtractor t=new TripExtractor();
		t.listFile("/Users/Geppo/Desktop/tripadvisor/www.tripadvisor.it/");
		for (String string : t.files) {
			System.out.println("****************************");
			System.out.println("URL: "+string);
			System.out.println(Utility.classifyPage(string));
		}
	}
}
