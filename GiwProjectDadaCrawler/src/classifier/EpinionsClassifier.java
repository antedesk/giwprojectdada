package classifier;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import model.PageDetails;
import model.PageList;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import db.DAOServices;

/*
* @author Antonio Gallo
* @author Daniele D'Andrea
* @author Antonio Tedeschi
* @author Daniele Malta
*/

public class EpinionsClassifier extends PageClassifier{
	private DAOServices dao;
	
	private List<String> pagine;
	private String rootFile;
	private final String ROOTSITE = "http://www.epinions.com";
	private final String SPAN = "span";
	private final String CLASS = "class";
	private final String HOME = "Home";
	private final String BREADCRUMB = "breadcrumb";
	private final String HREF = "a";
	private final String ATTRHREF = "href";
	private final String RKR = "rkr";
	private final String RGR = "rgr";
	private final String MAGGIORE = " &gt;&nbsp;";
	private final String TABLERESULT = "tableResult";
	private final String[] PRODUCTROWS = new String[]{"productRow", "productRow ", "productRow firstRow", "productRow no_border"};
	private final String PRODUCTREVIEWS = "productReviews";
	private final String PRODUCTINFOLEFT = "productInfo left";
	private final String LISTOF = "Lista di ";
	private final String INSTANCEOF = "Istanza di ";
	private final String MEDIA = "Media";


	public EpinionsClassifier(DAOServices dao, String rootFile, List<String> pagine){ 
		this.dao = dao;
		this.rootFile = rootFile;
		this.pagine=pagine;
	}
	
	
	
	public PageDetails createPageDetails(Source source,String category,String url) throws ParseException{

		List<Element> elementsTitle = source.getAllElementsByClass("product_title");
		String productName="";
		if(elementsTitle.size()>0){
			 productName=elementsTitle.get(0).getContent().toString();
		}
		int numberOfReviews=0;
		List<Element> elementsReviewNumber = source.getAllElementsByClass("rkr reviewLinks");
		String numReview=null;
		if(elementsReviewNumber.size()==1)
			numReview = elementsReviewNumber.get(0).getFirstElement("a").getTextExtractor().toString();
		if(numReview!=null){
			try{
				numberOfReviews = Integer.parseInt(numReview.split(" ")[0]);
			} catch(NumberFormatException e){
				System.out.println("Errore durante il parse in numero di "+numReview.split(" ")[0]+" da "+numReview);
				
				//throw new NumberFormatException("Errore durante il parse in numero di "+numReview.split(" ")[0]+" da "+numReview);
			}
		}

		List<Element> e = source.getAllElementsByClass("review_info");

		Date lastDateReview = null;
		List<Date> listaDate=new LinkedList<Date>();
		for (Element element : e) {
			List<Element> cvb = element.getAllElementsByClass("rgr");
			if(cvb.size()==1){
				String stringDate = cvb.get(0).getTextExtractor().toString();
				//stringDate = stringDate.replaceAll("'","");

				Date dateGMT ;
				DateFormat formatter = new SimpleDateFormat("MMM.dd.yy", Locale.US);
				stringDate=stringDate.replace(" ",".");
				stringDate=stringDate.replace("'","");
				dateGMT = (Date)formatter.parse(stringDate);

				listaDate.add(dateGMT);
			}
		}
		if(listaDate.size()>=1){
		Object[] arrayDate = listaDate.toArray();
		Arrays.sort(arrayDate);
		lastDateReview=(Date) arrayDate[arrayDate.length-1];
		}
		PageDetails pageD=new PageDetails(url, category, productName, numberOfReviews, 0, lastDateReview);
		
		return pageD;
	}
	public String classifyPage(Source source){


		// Controllo da migliorare, server per evitare pagine che hanno un refresh page 0 che punta ad un altro url (effetto redirect, non categorizzabile)
		if(source.toString().contains("<meta http-equiv=\"refresh\"")){
			return "REFRESH_RELOCATE";
		}

		List<Element> elements = source.getAllElementsByClass(BREADCRUMB);
		List<String> breadcrumbCategory = new ArrayList<String>();
		if(elements.size()>0){
			Element breadcrumb = elements.get(0);
			if(breadcrumb!=null){
				elements = breadcrumb.getAllElements(SPAN);
				for(Element el : elements){
					String span = el.getAttributeValue(CLASS);

					if(RKR.equals(span) || RGR.equals(span)){
						List<Element> links = el.getAllElements(HREF);
						if(links.size()>0){
							Element category = links.get(0);
							if(!MAGGIORE.equals(category.getContent())){
								breadcrumbCategory.add(category.getContent().toString());
							}
						} else{
							if(!MAGGIORE.equals(el.getContent().toString())){
								breadcrumbCategory.add(el.getContent().toString());
							}
						}
					}
				}
			} 
		} else {
			elements = source.getAllElements(SPAN);
			Element parentHome = null;
			for(Element el : elements){
				String span = el.getAttributeValue(CLASS);

				if(RKR.equals(span) || RGR.equals(span)){
					List<Element> links = el.getAllElements(HREF);
					if(links.size()>0){
						Element category = links.get(0);
						if(!MAGGIORE.equals(category.getContent())){
							if(el.getParentElement()!=null && el.getParentElement().equals(parentHome) || category.getContent().toString().equalsIgnoreCase(HOME)){
								if(parentHome==null){
									parentHome = el.getParentElement();
								}

								breadcrumbCategory.add(category.getContent().toString());
							}
						}
					} else{
						if(!MAGGIORE.equals(el.getContent().toString())){
							if(el.getParentElement()!=null && el.getParentElement().equals(parentHome) || el.getContent().toString().equalsIgnoreCase(HOME)){
								if(parentHome==null){
									parentHome = el.getParentElement();
								}
								breadcrumbCategory.add(el.getContent().toString());
							}
						}
					}
				}
			}
		}

		String category = "";
		if(breadcrumbCategory.size()>=2){
			category = breadcrumbCategory.get(1);
			if(category.equals(MEDIA) && breadcrumbCategory.size()>=3){
				category = breadcrumbCategory.get(2);
			}
		}

		return category;
	}

	public PageList createPageList(Source source, String url, String category){
		Element el = source.getElementById(TABLERESULT);

		List<PageDetails> pageDetailsProducts = new ArrayList<PageDetails>();
			for(String rowClass : PRODUCTROWS){
			
			List<Element> products = el.getAllElementsByClass(rowClass);
			for(Element productrow : products){
				String cat = category.replace(LISTOF, INSTANCEOF);
				PageDetails pageDetails = this.getPageDetailsFromRow(productrow, cat);
				if(pageDetails.getUrl().contains(ROOTSITE))
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
		List<Element> reviews = productrow.getAllElementsByClass(PRODUCTREVIEWS);
		int review = 0;
		String productName = null;
		String url = null;

		if(reviews.size()>0){
			Element prodRev = reviews.get(0);
			List<Element> hrefs = prodRev.getAllElements(HREF);
			if(hrefs.size()>0){
				Element e = hrefs.get(0);
				String rev = e.getContent().toString();
				String tmp = e.getContent().toString();
				rev = rev.substring(0, rev.indexOf(" "));
				
				try{
					review = Integer.parseInt(rev);
				} catch(NumberFormatException ex){
					System.out.println("Errore durante il parse in numero di "+rev+" da "+tmp);
					
					//throw new NumberFormatException("Errore durante il parse in numero di "+rev+" da "+tmp);
				}
			}
		}

		List<Element> infos = productrow.getAllElementsByClass(PRODUCTINFOLEFT);
		if(infos.size()>0){
			Element info = infos.get(0);
			List<Element> hrefs = info.getAllElements(HREF);
			if(hrefs.size()>0){
				Element e = hrefs.get(0);
				url = e.getAttributeValue(ATTRHREF);
				//url = url.replace("../prices/", "../reviews/");
				url = normalizeURL(url);
				productName = e.getContent().toString();
			}
		}

		// La data della lastReview non è ricavabile dalle pagine di dettaglio quindi viene settata a null
		PageDetails pageDetails = new PageDetails(url, category, productName, 0, review, null);
		return pageDetails;
	}
	
	private String normalizeURL(String url){
		url = url.replace("../", "");
		url = url.replace("/index.html", "");
		url = url.replace(rootFile, "");
		if(!url.startsWith("http"))
			url = ROOTSITE + "/"+url;
		
		url = url.replace("//", "/");
		url = url.replace("http:/", "http://");
		return url;
	}

	public void run(){
		String toprint = "";
		uncategorized = new ArrayList<String>();
		
		HashMap<String, Integer> frequencyCategory = new HashMap<String, Integer>();

		//int i = 1;
		//int size = this.pagine.size();
		for (String url : this.pagine) {
			if(!url.contains("/.svn/") && !url.contains("/.DS_Store")){
				toprint+=("********************************************************\n");
				toprint+=("URL: "+url+"\n");

				String html = Utility.fileToString(url);
				Source source = new Source(html);
				source.fullSequentialParse();
				
				url = normalizeURL(url);

				String category = classifyPage(source);
				PageDetails pd=null;
				if(source.getElementById("product_top_box")!=null && source.getElementById("product_area")!=null){
					category = INSTANCEOF + category;
					toprint+=("CATEGORIA: "+category+"\n");
					
					try {
						pd = createPageDetails(source,category,url);
						toprint+="Tipo Pagina: Istanza, Numero Voti: "+pd.getNumberOfReviews()+ ", Numero Review: "+pd.getNumberOfReviews()+"\n";
						try {
							this.dao.saveOrUpdatePageDetails(pd, false);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if(source.getElementById(TABLERESULT) != null){
					category = LISTOF + category;
					toprint+=("CATEGORIA: "+category+"\n");
					PageList pageList = createPageList(source, url, category);
					toprint+="Tipo Pagina: Lista, Numero prodotti: "+pageList.getProducts().size()+"\n";
					
					try {
						this.dao.saveOrUpdatePageList(pageList);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				if(!category.equals("")){
					if(frequencyCategory.containsKey(category)){
						frequencyCategory.put(category, frequencyCategory.get(category)+1);
					} else{
						frequencyCategory.put(category, 1);
					}
				}
				else
					uncategorized.add(url);
			}
			
			toprint+=("********************************************************\n\n");
			System.out.println(toprint);
		}

	}
}
