package classifier;

import java.io.IOException;
import java.sql.Connection;
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
import db.DBDatasource;


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
	private final String INSTANCEOF = "istanza di ";
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
			System.out.println("NOME: "+productName);
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
		System.out.println(numberOfReviews);

		List<Element> e = source.getAllElementsByClass("review_info");
		//System.out.println(e.size());

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
				//System.out.println(stringDate);
				dateGMT = (Date)formatter.parse(stringDate);

				listaDate.add(dateGMT);
			}
		}
		if(listaDate.size()>=1){
		Object[] arrayDate = listaDate.toArray();
		Arrays.sort(arrayDate);
		lastDateReview=(Date) arrayDate[arrayDate.length-1];
		System.out.println(lastDateReview.toString());
		}
		PageDetails pageD=new PageDetails(url, category, productName, numberOfReviews, 0, lastDateReview);
		
		return pageD;
	}
	public String classifyPage(Source source){


		// Controllo da migliorare, server per evitare pagine che hanno un refresh page 0 che punta ad un altro url (effetto redirect, non categorizzabile)
		if(source.toString().contains("<meta http-equiv=\"refresh\"")){
			return "REFRESH_RELOCATE";
		}

		//System.out.println(source.getElementById("product_top_box").toString());




		List<Element> elements = source.getAllElementsByClass(BREADCRUMB);
		List<String> breadcrumbCategory = new ArrayList<String>();
		if(elements.size()>0){
			Element breadcrumb = elements.get(0);
			if(breadcrumb!=null){
				//System.out.println("BREADCRUMB" + breadcrumb.toString());

				elements = breadcrumb.getAllElements(SPAN);
				for(Element el : elements){
					String span = el.getAttributeValue(CLASS);

					if(RKR.equals(span) || RGR.equals(span)){
						//System.out.println("EL: "+el.toString()+", class = "+span);
						List<Element> links = el.getAllElements(HREF);
						if(links.size()>0){
							Element category = links.get(0);
							if(!MAGGIORE.equals(category.getContent())){
								breadcrumbCategory.add(category.getContent().toString());
								//System.out.println("IF: "+category.getContent());
							}
						} else{
							if(!MAGGIORE.equals(el.getContent().toString())){
								breadcrumbCategory.add(el.getContent().toString());
								//System.out.println("ELSE: "+el.getContent());
							}
						}
					}
				}

				//elements = breadcrumb.getAllElementsByClass(RKR);
				//for(Element el : elements){
				//List<Element> links = el.getAllElements(HREF);
				//if(links.size()>0){
				//Element category = links.get(0);
				//	breadcrumbCategory.add(category.getContent().toString());
				//	} else{
				// E' utile? forse c'è sempre solo  '&gt;&nbsp;'
				////System.out.println(el.getContent());
				//}
				//}

				//elements = breadcrumb.getAllElementsByClass(RGR);
				//for(Element category : elements){
				//breadcrumbCategory.add(category.getContent().toString());
				//}
			} 
		} else {
			elements = source.getAllElements(SPAN);
			Element parentHome = null;
			for(Element el : elements){
				String span = el.getAttributeValue(CLASS);

				if(RKR.equals(span) || RGR.equals(span)){

					//if(el.getParentElement().equals(parentHome)){
					//System.out.println("EL: "+el.toString()+", class = "+span);
					List<Element> links = el.getAllElements(HREF);
					if(links.size()>0){
						Element category = links.get(0);
						if(!MAGGIORE.equals(category.getContent())){
							if(el.getParentElement()!=null && el.getParentElement().equals(parentHome) || category.getContent().toString().equalsIgnoreCase(HOME)){
								if(parentHome==null){
									//System.out.println("HOME: "+category.getContent());
									parentHome = el.getParentElement();
									//System.out.println("PARENT: "+parentHome);
								}

								breadcrumbCategory.add(category.getContent().toString());
								//System.out.println("IF: "+category.getContent());
							}
						}
					} else{
						if(!MAGGIORE.equals(el.getContent().toString())){
							if(el.getParentElement()!=null && el.getParentElement().equals(parentHome) || el.getContent().toString().equalsIgnoreCase(HOME)){
								if(parentHome==null){
									parentHome = el.getParentElement();
									//System.out.println("PARENT: "+parentHome);
								}
								breadcrumbCategory.add(el.getContent().toString());
								//System.out.println("ELSE: "+el.getContent());
							}
						}
					}
					//}
				}
			}
		}

		//System.out.print("Breadcrumb: " + Utility.listToBreadcrumb(breadcrumbCategory));
		//System.out.println();

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
				url = url.replace("../prices/", "../reviews/");
				url = normalizeURL(url);
				productName = e.getContent().toString();
			}
		}

		System.out.println(productName +" - "+ url + " - "+ review);

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
		
		uncategorized = new ArrayList<String>();
		
		HashMap<String, Integer> frequencyCategory = new HashMap<String, Integer>();

		//int i = 1;
		//int size = this.pagine.size();
		for (String url : this.pagine) {
			if(!url.contains("/.svn/") && !url.contains("/.DS_Store")){
				//System.out.println(i+"/"+size+", uncategorized: "+uncategorized.size());
				System.out.println("********************************************************");
				System.out.println("URL: "+url);

				String html = Utility.fileToString(url);
				Source source = new Source(html);
				source.fullSequentialParse();
				
				url = normalizeURL(url);

				String category = classifyPage(source);
				System.out.println("CATEGORIA: "+category);
				PageDetails pd=null;
				if(source.getElementById("product_top_box")!=null && source.getElementById("product_area")!=null){
					category = INSTANCEOF + category;
					try {
						pd = createPageDetails(source,category,url);
						try {
							this.dao.saveOrUpdatePageDetails(pd, false);
						} catch (SQLException e) {
							e.printStackTrace();
							//return;
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("DetailsPage created: ");
					System.out.println(pd.toString());
				} else if(source.getElementById(TABLERESULT) != null){
					category = LISTOF + category;
					PageList pageList = createPageList(source, url, category);
					try {
						this.dao.saveOrUpdatePageList(pageList);
					} catch (SQLException e) {
						e.printStackTrace();
						//return;
					}
					System.out.println(url + " PAGINA DI ELENCO");
				}

				if(!category.equals("")){
					System.out.println("Categoria (breadcrumb[1]) = " + category);

					if(frequencyCategory.containsKey(category)){
						frequencyCategory.put(category, frequencyCategory.get(category)+1);
					} else{
						frequencyCategory.put(category, 1);
					}
				}
				else
					uncategorized.add(url);


				

				System.out.println("********************************************************\n");
			}
			//i++;
		}
		/*
		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : uncategorized)
			System.out.println(url);
		System.out.println("********************************************************");

		System.out.println("\n*************** Frequenza Categorie ***************");
		for(String category : frequencyCategory.keySet())
			System.out.println("Category: "+category+": "+frequencyCategory.get(category));
		System.out.println("********************************************************");
		 */}
}
