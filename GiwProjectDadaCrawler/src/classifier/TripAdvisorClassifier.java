package classifier;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class TripAdvisorClassifier implements PageClassifier{

	public TripAdvisorClassifier(){
		
	}


	
	public String classifyPage(String html){
		
		//System.out.println(html);
		Source source= new Source(html);

		List<String> descrizioneList=new LinkedList<String>();

		List<Element> allEl = source.getAllElements();
		
		for (Element element : allEl) {

			if(element.getAttributeValue("class")!=null&&((String)element.getAttributeValue("class")).equals("crumbs "))
			{
				

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
	


	public static void main(String args[]) throws IOException{
		TripAdvisorClassifier t=new TripAdvisorClassifier();
		List<String> listaFile=Utility.listFiles("/Users/Geppo/Desktop/tripadvisor/www.tripadvisor.it/");
		for (String string : listaFile) {
			System.out.println("****************************");
			System.out.println("URL: "+string);
			System.out.println(t.classifyPage(Utility.fileToString(string)));
		}
	}
}
