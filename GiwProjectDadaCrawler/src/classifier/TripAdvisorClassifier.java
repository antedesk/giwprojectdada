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
	private final String[] REVIEWNUM_CLASS = new String[]{"fkLnk hvrIE6","taLnk hvrIE6"};
	private final String HAC_RESULTS = "HAC_RESULTS";
	private final String LISTING = "listing";
	private final String RS_RATING = "rs rating";
	private final String QUALITYWRAP = "quality wrap";
	private final String ENTRYWRAP = "entry wrap";
	private final String WRAP = "wrap";
	private final String MORE = "more";



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
		for (String url : this.pagine) {
			if(!url.contains("/.svn/") && !url.contains("/.DS_Store")){
				//RICHIESTA HTTP PER LAST MODIFY

				//toPrint+=(i+"/"+size+", uncategorized: "+uncategorized.size()+"\n");
				System.out.println("********************************************************\n");
				System.out.println("URL: "+url+"\n");
				String category;
				if(url.contains("/TravelersChoice"))
					category ="TravelChoise";
				else if(url.contains("/members/"))
					category ="members";
				else if(url.contains("/members-photos/"))
					category ="members photos";
				else if(url.contains("/members-forums/"))
					category ="members forums";
				else if(url.contains("/Attractions"))
					category = "Lista attrazioni";
				else if(url.contains("/Hotels"))
					category = "Lista hotel";
				else if(url.contains("/Restaurants"))
					category = "Lista ristoranti";

				else  {
					Source source= new Source(Utility.fileToString(url));
					category = this.classifyPage(source);
					createPageList(source, url, category);
					System.out.println(category);
				}
				if(category.equals(""))
					uncategorized.add(url);
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

			List<Element> products = el.getAllElementsByClass(LISTING);
			for(Element productrow : products){
				PageDetails pageDetails = this.getPageDetailsFromRow(productrow, category);
				pageDetailsProducts.add(pageDetails);
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
		int review = 0;
		if(category.equals("Ristoranti"))
			review = this.getResturantReviewsNumber(productrow);
		else
			review = this.getReviewsNumber(productrow);
		
		String productName = null;
		String url = null;

		List<Element> infos = productrow.getAllElementsByClass(QUALITYWRAP);
		if(infos.size()>0){
			Element info = infos.get(0);
			List<Element> hrefs = info.getAllElements(HREF);
			if(hrefs.size()>0){
				Element e = hrefs.get(0);
				url = e.getAttributeValue(ATTRHREF);
				//url = normalizeURL(url);
				url= ROOTSITE+url;
				productName = e.getContent().toString();
			}
		}

		System.out.println(productName +" - "+ url + " - "+ review);

		// La data della lastReview non � ricavabile dalle pagine di dettaglio quindi viene settata a null
		PageDetails pageDetails = new PageDetails(url, category, productName, 0, review, null);
		return pageDetails;
	}


	// reupera il numero di review dei ristoranti
	private int getResturantReviewsNumber(Element productrow) {
		List<Element> reviews = productrow.getAllElementsByClass(MORE);
		int review = 0;
		if(reviews.size()>0){
			Element prodRev = reviews.get(0);
			List<Element> hrefs = prodRev.getAllElements(HREF);
			if(hrefs.size()>0){
				Element e = hrefs.get(0);
				String rev = e.getContent().toString();
				rev = rev.substring(0, rev.indexOf(" "));
				review = Integer.parseInt(rev);
			}
		}
		return review;
	}

	// Recupera il numero di review per hotel e le attrazioni
	private int getReviewsNumber(Element productrow) {
		int review = 0;
		List<Element> more = productrow.getAllElementsByClass(MORE);
		if(more.size()>0)
		{
			Element el_reviews = more.get(0);
			List<Element> reviews = el_reviews.getAllElements();
			System.out.print("**********\n"+reviews.size()+"\n**********");			
			if(reviews.size()>0)
			{
				/* NON CAPISCO PERCHE' CI SONO DUE ELEMENTI NELLA LISTA 
				   DOVE IL PRIMO E' TUTTO <SPAN>#RECENSIONI</SPAN> E IL SECONDO E' #RECENSIONI 
				   Mi illuminate voi? :D
				*/
				Element e = reviews.get(1);
				String rev = e.getContent().toString();
				//System.out.println("**********************************************************\n\n"+rev+"\n\n**********************************************************");
				//il substring parte da 1 perchè a o c'è uno spazio
				rev = rev.substring(1, rev.indexOf(" "));
				//System.out.println("**********************************************************\n\n"+rev+"\n\n**********************************************************");
				review = Integer.parseInt(rev);
			}							

		}

		return review;
	}


	//		private String normalizeURL(String url){
	//			url = url.replace("../", "");
	//			url = url.replace("/index.html", "");
	//			url = url.replace(rootFile, "");
	//
	//			if(!url.startsWith("http"))
	//				url = ROOTSITE + "/"+url;
	//			return url;
	//		}

















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

