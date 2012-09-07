package classifier;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import model.PageDetails;
import model.PageList;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class TripAdvisorClassifier extends PageClassifier{
	List<String> keywords;
	List<String> pagine;
	List<String> uncategorized;

	private String rootFile;
	private final String SPAN = "span";
	private final String CLASS = "class";
	private final String HOME = "Home";
	private final String BREADCRUMB = "breadcrumb";
	
	private final String ROOTSITE = "http://www.tripadvisor.it/";
	private final String HREF = "a";
	private final String ATTRHREF = "href";
	private final String REVIEWNUMCLASS = "taLnk hvrIE6";
	private final String HAC_RESULTS = "HAC_RESULTS";
	private final String[] LISTING = new String[]{"listing ", "listing first"};
	private final String RS_RATING = "rs rating";
	private final String QUALITYWRAP = "quality wrap";


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
	
	// Metodo per la creazione delle pagine di tipo lista
	public PageList createPageList(Source source, String url, String category){
		// recupero dalla pagina l'elemento passato per id, in questo caso i risultati della ricerca
		Element el = source.getElementById(HAC_RESULTS);

		List<PageDetails> pageDetailsProducts = new ArrayList<PageDetails>();
			for(String rowClass : LISTING){
			
			List<Element> products = el.getAllElementsByClass(rowClass);
			for(Element productrow : products){
				PageDetails pageDetails = this.getPageDetailsFromRow(productrow, category);
				pageDetailsProducts.add(pageDetails);
			}
		}

		/*products = el.getAllElementsByClass(PRODUCTROW);
		for(Element productrow : products){
			PageDetails pageDetails = this.getPageDetailsFromRow(productrow, category);
			pageDetailsProducts.add(pageDetails);
		}*/

		PageList pageList = new PageList(url, category, pageDetailsProducts);
		return pageList;
	}

	public PageDetails getPageDetailsFromRow(Element productrow, String category){
		List<Element> reviews = productrow.getAllElementsByClass(RS_RATING);
		int review = 0;
		String productName = null;
		String url = null;

		
		if(reviews.size()>0){
			Element prodRev = reviews.get(0);
			List<Element> spans = prodRev.getAllElementsByClass(REVIEWNUMCLASS);
			if(spans.size()>0){
				Element e = spans.get(0);
				String rev = e.getContent().toString();
				rev = rev.substring(0, rev.indexOf(" "));
				review = Integer.parseInt(rev);
			}
		}

		List<Element> infos = productrow.getAllElementsByClass(QUALITYWRAP);
		if(infos.size()>0){
			Element info = infos.get(0);
			List<Element> hrefs = info.getAllElements(HREF);
			if(hrefs.size()>0){
				Element e = hrefs.get(0);
				url = e.getAttributeValue(ATTRHREF);
				url = normalizeURL(url);
				productName = e.getContent().toString();
			}
		}

		System.out.println(productName +" - "+ url + " - "+ review);

		// La data della lastReview non � ricavabile dalle pagine di dettaglio quindi viene settata a null
		PageDetails pageDetails = new PageDetails(url, category, productName, 0, review, null);
		return pageDetails;
	}
	
	
	private String normalizeURL(String url){
		url = url.replace("../", "");
		url = url.replace("/index.html", "");
		url = url.replace(rootFile, "");
		
		if(!url.startsWith("http"))
			url = ROOTSITE + "/"+url;
		return url;
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	//--------------------------DETTAGLI PAGINA NON GUARDARE!-----------------------------//
//	public PageDetails createPageDetails(Source source,String category,String url) throws ParseException{
//
//		List<Element> elementsTitle = source.getAllElementsByClass("product_title");
//		String productName="";
//		if(elementsTitle.size()>0){
//			 productName=elementsTitle.get(0).getContent().toString();
//			System.out.println("NOME: "+productName);
//		}
//		int numberOfReviews=0;
//		List<Element> elementsReviewNumber = source.getAllElementsByClass("rkr reviewLinks");
//		String numReview=null;
//		if(elementsReviewNumber.size()==1)
//			numReview = elementsReviewNumber.get(0).getFirstElement("a").getTextExtractor().toString();
//		if(numReview!=null)
//			numberOfReviews = Integer.parseInt(numReview.split(" ")[0]);
//		System.out.println(numberOfReviews);
//
//		List<Element> e = source.getAllElementsByClass("review_info");
//		//System.out.println(e.size());
//
//		Date lastDateReview = null;
//		List<Date> listaDate=new LinkedList<Date>();
//		for (Element element : e) {
//			List<Element> cvb = element.getAllElementsByClass("rgr");
//			if(cvb.size()==1){
//				String stringDate = cvb.get(0).getTextExtractor().toString();
//				//stringDate = stringDate.replaceAll("'","");
//
//				Date dateGMT ;
//				DateFormat formatter = new SimpleDateFormat("MMM.dd.yy", Locale.US);
//				stringDate=stringDate.replace(" ",".");
//				stringDate=stringDate.replace("'","");
//				//System.out.println(stringDate);
//				dateGMT = (Date)formatter.parse(stringDate);
//
//				listaDate.add(dateGMT);
//			}
//		}
//		if(listaDate.size()>=1){
//		Object[] arrayDate = listaDate.toArray();
//		Arrays.sort(arrayDate);
//		lastDateReview=(Date) arrayDate[arrayDate.length-1];
//		System.out.println(lastDateReview.toString());
//		}
//		PageDetails pageD=new PageDetails(url, category, productName, numberOfReviews, 0, lastDateReview);
//		
//		return pageD;
//	}
}

