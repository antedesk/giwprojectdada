package classifier;
import java.io.EOFException;
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

	private final String SPAN = "span";

	private final String ROOTSITE = "http://www.tripadvisor.it/";
	private final String HREF = "a";
	private final String ATTRHREF = "href";
	private final String HAC_RESULTS = "HAC_RESULTS";
	private final String LISTING = "listing";
	private final String RS_RATING = "rs rating";
	private final String QUALITYWRAP = "quality wrap";
	private final String MORE = "more";
	private final String H1 = "h1";



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
				Source source= new Source(Utility.fileToString(url));

				//toPrint+=(i+"/"+size+", uncategorized: "+uncategorized.size()+"\n");
				System.out.println("********************************************************\n");
				System.out.println("URL: "+url+"\n");
				String category;
				if(url.contains("TravelersChoice"))
					category ="TravelChoise";
				else if(url.contains("members/"))
					category ="members";
				else if(url.contains("members-photos/"))
					category ="members photos";
				else if(url.contains("members-forums/"))
					category ="members forums";
				else if(url.contains("Attractions"))
					category = "Lista attrazioni";
				else if(url.contains("Hotels"))
					category = "Lista hotel";
				else if(url.contains("Restaurants"))
					category = "Lista ristoranti";

				else  {
					category = this.classifyPage(source);
					System.out.println();
					System.out.println(category);
				}
				if(category.contains("lista"))
				{
					createPageList(source, url, category);
					System.out.println(category);	
				}

				if(category.contains("istanza"))
					try {
						createPageDetails(source, category, url);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
			review = this.getResturantReviewsNumberFromResultsList(productrow);
		else
			review = this.getReviewsNumberFromResultsList(productrow);

		String productName = null;
		String url = null;

		List<Element> infos = productrow.getAllElementsByClass(QUALITYWRAP);
		if(infos.size()>0){
			Element info = infos.get(0);
			List<Element> hrefs = info.getAllElements(HREF);
			if(hrefs.size()>0){
				Element e = hrefs.get(0);
				url = e.getAttributeValue(ATTRHREF);
				//ricostruzione dell'url unendo la root e il path
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
	private int getResturantReviewsNumberFromResultsList(Element productrow) {
		List<Element> reviews = productrow.getAllElementsByClass(MORE);
		int review = 0;
		if(reviews.size()>0){
			Element prodRev = reviews.get(0);
			List<Element> hrefs = prodRev.getAllElements(HREF);
			if(hrefs.size()>0){
				//recupero l'elemento, il suo contenuto da cui è ricavato il numero di review e infine lo converto in intero
				Element e = hrefs.get(0);
				String rev = e.getContent().toString();
				rev = rev.substring(0, rev.indexOf(" "));
				review = Integer.parseInt(rev);
			}
		}
		return review;
	}

	// Recupera il numero di review per hotel e le attrazioni
	private int getReviewsNumberFromResultsList(Element productrow) {
		int review = 0;
		List<Element> more = productrow.getAllElementsByClass(MORE);
		if(more.size()>0)
		{
			Element el_reviews = more.get(0);
			List<Element> reviews = el_reviews.getAllElements();
			System.out.print("**********\n"+reviews.size()+"\n**********");			
			if(reviews.size()>0)
			{

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



	public PageDetails createPageDetails(Source source, String category, String url) throws ParseException{

		/* 
		 * il nome dell'albergo è contenuto in <h1></h1> è sempre il secondo elemento della lista, 
		 * il primo è compreso di località e si trova in altro sotto il logo tripadvisor.
		 */
		List<Element> elementsTitle = source.getAllElements(H1);
		String productName="";
		if(elementsTitle.size()>0){
			productName = elementsTitle.get(1).getContent().toString().substring(1);
			System.out.println("NOME: "+productName);
		}

		int num_rev=0;
		// mi faccio restituire la lista di tutti gli span che sono in rs rating (per l'esattezza tre)
		List<Element> ratingElements = source.getAllElementsByClass(RS_RATING).get(0).getAllElements(SPAN);
		//mi faccio restituire il terzo elemento della lista che contiene il numero delle recensioni e ne prendo il contenuto
		Element e = ratingElements.get(2);
		String rev = e.getContent().toString();
		num_rev = Integer.parseInt(rev);
		System.out.println(rev);

		//ottengo la data dell'ultima recensione.
		Date lastdate = this.getDateFromPageDetails(source);

		PageDetails pageDatails=new PageDetails(url, category, productName, num_rev, 0, lastdate);

		return pageDatails;
	}
	
	//recupero la data dell'ultima recensione presente nella pagina
	public Date getDateFromPageDetails(Source source) throws ParseException
	{
		List<Element> allReviewsDate = source.getAllElementsByClass("ratingDate");
		System.out.println(allReviewsDate.size()+"\t"+allReviewsDate.get(0).getContent().toString());
		Element recentDate = allReviewsDate.get(0);
		String stringDate = recentDate.getContent().toString();
		int lung = stringDate.length();
		stringDate= stringDate.substring(13, lung-1);
		System.out.println(stringDate);

		Date lastDate = null ;
		DateFormat formatter = new SimpleDateFormat("dd.MMMMMMMMM.yyyy", Locale.ITALY);
		stringDate=stringDate.replace(" ",".");
		
		//controllo per eliminare lo span inserito all'interno del numero di recensioni, non è presente in tutte le pagine.
		if(stringDate.contains("<span.class=\"new\">"))
		{
			String array[]=stringDate.split("<");
			int leng = array[0].length();
			stringDate = array[0].substring(0,leng-1); //rimuovo lo \n che c'è
		}
		System.out.println(stringDate);
		lastDate = (Date)formatter.parse(stringDate);
		
		return lastDate;
	}
}

