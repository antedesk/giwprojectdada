package classifier;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class TripAdvisorClassifier extends PageClassifier{
	List<String> keywords;
	List<String> pagine;
	List<String> uncategorized;
	public TripAdvisorClassifier(List<String> pagine){
		this.pagine=pagine;
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



	public String classifyPage(Source source){

		//System.out.println(html);
		boolean isForumListPage=false;
		
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
	public void run(){

		String toPrint="";
		uncategorized = new ArrayList<String>();
		//int i = 1;
		//int size = this.pagine.size();
		for (String indirizzo : this.pagine) {
			if(!indirizzo.contains("/.svn/") && !indirizzo.contains("/.DS_Store")){
				//RICHIESTA HTTP PER LAST MODIFY
				try {
					int x=indirizzo.lastIndexOf("/");
					String httpURL="http://www.tripadvisor.it"+indirizzo.substring(x);
					URL url= new URL(httpURL);
					
					
					
					System.out.print(url);
					URLConnection hpCon = url.openConnection(); 
					System.out.println("Date: " + new Date(hpCon.getDate())); 
					System.out.println("Content-Type: " + 
					hpCon.getContentType()); 
					System.out.println("Expires: " + hpCon.getExpiration()); 
					System.out.println("Last-Modified: " + 
					new Date(hpCon.getLastModified())); 
					int len = hpCon.getContentLength(); 
					System.out.println("Content-Length: " + len); 
					if (len > 0) { 
					System.out.println("=== Content ==="); 
					InputStream input = hpCon.getInputStream(); 
					int i = len; 
					int c;
					while (((c = input.read()) != -1) && (-i > 0)) { 
					System.out.print((char) c); 
					} 
					input.close(); 
					} else { 
					System.out.println("No Content Available"); 
					} 
					System.out.println("***********");

					
					
					/*
					
					URLConnection c = url.openConnection();
					c.setConnectTimeout(5000);   // 5 seconds
					System.out.println(url.toString());
					String lastMod = c.getHeaderField("Last-Modified");
					System.out.println("last mod: "+lastMod);
					
					System.out.println("***********");
					*/
					
					// List all the response headers from the server.
				    // Note: The first call to getHeaderFieldKey() will implicit send
				    // the HTTP request to the server.
				   /* for (int i=0; ; i++) {
				        String headerName = c.getHeaderFieldKey(i);
				        String headerValue = c.getHeaderField(i);

				        if (headerName == null && headerValue == null) {
				            // No more headers
				            break;
				        }
				        if (headerName!=null&&headerName.contains("Last-modified")) {
				            // The header value contains the server's HTTP version
				        	System.out.println("***********");
				        	System.out.println(headerValue);
				        }
				    }*/




				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//toPrint+=(i+"/"+size+", uncategorized: "+uncategorized.size()+"\n");
				System.out.println("********************************************************\n");
				System.out.println("URL: "+indirizzo+"\n");
				String category;
				if(indirizzo.contains("/TravelersChoice"))
					category ="TravelChoise";
				else if(indirizzo.contains("/members/"))
					category ="members";
				else if(indirizzo.contains("/members-photos/"))
					category ="members photos";
				else if(indirizzo.contains("/members-forums/"))
					category ="members forums";
				else if(indirizzo.contains("/Attractions"))
					category = "Lista attrazioni";
				else if(indirizzo.contains("/Hotels"))
					category = "Lista hotel";
				else if(indirizzo.contains("/Restaurants"))
					category = "Lista ristoranti";
				
				else  {
					Source source= new Source(Utility.fileToString(indirizzo));
					category = this.classifyPage(source);
					System.out.println(category);
				}
				if(category.equals(""))
					uncategorized.add(indirizzo);
				//toPrint+=("Categoria (breadcrumb[1]) = " + category+"\n");
				//else 
				//	uncategorized.add(indirizzo);
				//toPrint+=("********************************************************\n\n");

			}
			//i++;
		}
		//System.out.println(toPrint);
	}
}

