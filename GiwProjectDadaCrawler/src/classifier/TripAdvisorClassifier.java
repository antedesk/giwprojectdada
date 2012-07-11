package classifier;
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


public class TripAdvisorClassifier implements PageClassifier{
	List<String> files;
	public TripAdvisorClassifier(){
		this.files=new LinkedList<String>();
	}

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
	public static String pathString(List<String> lista){
		String path = "";
		for (String string : lista) {
			path= path+string+" -> ";
			//System.out.print(string+", ");
		}
		//System.out.println("");
		return path.substring(0, path.length()-1-3);
	}
	public String classifyPage(String filePath){
		String html=fileToString(filePath);
		//System.out.println(html);
		Source source= new Source(html);

		//controllo che ci sia il tag meta con le keywords
		/*	List<Element> metas=source.getAllElements("meta");
		String[] keywords=null;
		for (Element metaEl : metas) {
			if(metaEl.getAttributeValue("name")!=null&&metaEl.getAttributeValue("name").equals("keywords"))
				{
				keywords=metaEl.getAttributeValue("content").split(", ");
				for (String string : keywords) {
					if(mapCategory.containsKey(string))
						mapCategory.put(string, mapCategory.get(string)+1);
					else mapCategory.put(string, 1);
				}
				}
		}*/
		List<String> descrizioneList=new LinkedList<String>();

		List<Element> allEl = source.getAllElements();
		boolean trovatoClass=false;
		for (Element element : allEl) {

			if(element.getAttributeValue("class")!=null&&((String)element.getAttributeValue("class")).equals("crumbs "))
			{
				trovatoClass=true;
				//System.out.println("****************************");

				//System.out.println("URL: "+filePath);
				Element liEl=element.getFirstElement("li");
				if(liEl.getFirstElement("a")!=null){
					descrizioneList.add(liEl.getFirstElement("a").getTextExtractor().toString());

					if(liEl.getFirstElement("ul")!=null && liEl.getFirstElement("ul").getAllElements("li")!=null){
						List<Element> liList= liEl.getFirstElement("ul").getAllElements("li");
						for (Element element2 : liList) {
							String subPath=element2.getTextExtractor().toString();

							if(subPath.startsWith(">"))
								subPath=subPath.substring(2);
							descrizioneList.add(subPath);
						}
					}
				}

			}


		}
		if(descrizioneList.size()==0)
			return null;
		else 
		{
			int size=descrizioneList.size();
			String ultimo=descrizioneList.get(size-1);
			String penultimo = descrizioneList.get(size-2);
				if(penultimo.contains("Attrazioni:")||penultimo.contains("Vacanze")
						||penultimo.contains("Ristoranti:")
						||penultimo.contains("Idee viaggio")||penultimo.contains("Hotel")
						||penultimo.contains("Bed & breakfast e pensioni"))
				return "istanza di "+penultimo;
				else 
					if(ultimo.contains("Attrazioni:")||ultimo.contains("Vacanze")
							||ultimo.contains("Ristoranti:")
							||ultimo.contains("Idee viaggio")||ultimo.contains("Hotel")
							||ultimo.contains("Bed & breakfast e pensioni"))
					return ultimo;
					else return "informazioni su "+ultimo; 
		}
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
		TripAdvisorClassifier t=new TripAdvisorClassifier();
		t.listFile("/Users/Geppo/Desktop/tripadvisor/www.tripadvisor.it/");
		for (String string : t.files) {
			System.out.println("****************************");
			System.out.println("URL: "+string);
			System.out.println(t.classifyPage(string));
		}
	}
}
