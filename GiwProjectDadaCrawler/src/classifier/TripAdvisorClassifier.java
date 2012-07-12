package classifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class TripAdvisorClassifier implements PageClassifier{
	List<String> keywords;
	public TripAdvisorClassifier(){
		this.keywords = new LinkedList<String>();
		keywords.add("Attrazioni");
		keywords.add("Vacanze");
		keywords.add("Ristoranti");
		keywords.add("Idee viaggio");
		keywords.add("Hotel");
		keywords.add("Bed & breakfast e pensioni");
		//OFFERTE NON HA ISTANZA
		keywords.add("Offerte");

	}



	public String classifyPage(String html){

		//System.out.println(html);
		boolean isForumListPage=false;
		Source source= new Source(html);
		if(source.toString().contains("<meta http-equiv=\"refresh\"")){
			return "REFRESH_RELOCATE";
		}
		
		List<String> descrizioneList=new LinkedList<String>();

		List<Element> allEl = source.getAllElements();

		for (Element element : allEl) {

			if(element.getAttributeValue("class")!=null&&((String)element.getAttributeValue("class")).equals("crumbs "))
			{

				if(source.getElementById("HEADING")!=null&&source.getElementById("HEADING").getTextExtractor().toString().startsWith("Forum"))
					isForumListPage=true;
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
			return "";
		else 
		{
			System.out.println("Breadcrumb: "+Utility.listToBreadcrumb(descrizioneList));
			int size=descrizioneList.size();
			String ultimo=descrizioneList.get(size-1);
			if(size>1){
				String penultimo = descrizioneList.get(size-2);
				for (String keyword : this.keywords) {
					if(penultimo.startsWith(keyword))
						return "istanza di "+keyword;
				}
				for (String keyword : this.keywords) {
					if(ultimo.startsWith(keyword))
						return keyword;
				}
				if(ultimo.startsWith("Forum"))
					return "discussione Forum";
				else {
					if(isForumListPage)
						return "Forum Topics list";
					return "Informazioni su "+ultimo; 
				}
			}
			else 
				if(isForumListPage) return "Home Forum";
				//per ora non ho idea di cosa possa esserci qui sotto
				else return "";
		}
	}



	/*public static void main(String args[]) throws IOException{
		TripAdvisorClassifier t=new TripAdvisorClassifier();
		List<String> listaFile=Utility.listFiles("./TripAdvisorExamplePages");
		for (String string : listaFile) {
			System.out.println("****************************");
			System.out.println("URL: "+string);
			System.out.println(t.classifyPage(Utility.fileToString(string)));
		}
	}*/


	public static void main(String args[]) throws IOException{
		TripAdvisorClassifier t=new TripAdvisorClassifier();
		//List<String> listaFile=Utility.listFiles("./TripAdvisorExamplePages");
		List<String> listaFile=Utility.listFiles("/Users/Geppo/Desktop/tripadvisor");

		List<String> uncategorized = new ArrayList<String>();
		int i = 1;
		int size = listaFile.size();
		for (String url : listaFile) {
			if(!url.contains("/.svn/") && !url.contains("/.DS_Store")){
				System.out.println(i+"/"+size+", uncategorized: "+uncategorized.size());
				System.out.println("********************************************************");
				System.out.println("URL: "+url);
				String category;
				if(url.contains("/TravelersChoice/"))
					category ="TravelChoise";
				else if(url.contains("/members/"))
					category ="members";
				else if(url.contains("/members-photos/"))
					category ="members photos";
				else if(url.contains("/members-forums/"))
					category ="members forums";
				else  category = t.classifyPage(Utility.fileToString(url));
				if(!category.equals(""))
					System.out.println("Categoria (breadcrumb[1]) = " + category);
				else
					uncategorized.add(url);
				System.out.println("********************************************************\n");

			}
			i++;
		}

		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : uncategorized)
			System.out.println(url);
				System.out.println("********************************************************");
	}
}
