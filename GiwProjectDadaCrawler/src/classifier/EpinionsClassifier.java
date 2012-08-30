package classifier;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import model.PageDetails;
import model.PageList;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;


public class EpinionsClassifier extends PageClassifier{
	List<String> pagine;
	List<String> uncategorized;
	private static final String SPAN = "span";
	private static final String CLASS = "class";
	private static final String HOME = "Home";
	private final String BREADCRUMB = "breadcrumb";
	private final String HREF = "a";
	private final String ATTRHREF = "href";
	private final String RKR = "rkr";
	private final String RGR = "rgr";
	private final String MAGGIORE = " &gt;&nbsp;";
	private final String TABLERESULT = "tableResult";
	private final String PRODUCTROW = "productRow";
	private final String PRODUCTROWNOBORDER = "productRow no_border";
	private final String PRODUCTREVIEWS = "productReviews";
	private final String PRODUCTINFOLEFT = "productInfo left";
	

	public EpinionsClassifier(List<String> pagine){ 
		this.pagine=pagine;
	}
	public PageDetails createPageDetails(Source source,String category,String url) throws ParseException{
		
		List<Element> elementsTitle = source.getAllElementsByClass("product_title");
		if(elementsTitle.size()>0){
			String productName=elementsTitle.get(0).getContent().toString();
			System.out.println("NOME: "+productName);
		}
			int numberOfReviews=0;
			List<Element> elementsReviewNumber = source.getAllElementsByClass("rkr reviewLinks");
			String numReview=null;
			if(elementsReviewNumber.size()==1)
				numReview = elementsReviewNumber.get(0).getFirstElement("a").getTextExtractor().toString();
			if(numReview!=null)
				numberOfReviews = Integer.parseInt(numReview.split(" ")[0]);
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
					DateFormat formatter ;
					Date dateGMT ;
					formatter = new SimpleDateFormat(" MMM dd ''yy ");
					System.out.println(stringDate);
					dateGMT = formatter.parse(stringDate);
					
					listaDate.add(dateGMT);
				}
			}
			Date[] arrayDate = (Date[]) listaDate.toArray();
			Arrays.sort(arrayDate);
			lastDateReview=arrayDate[arrayDate.length-1];
			System.out.println(lastDateReview.toString());
			
			//PageDetails pageD=new PageDetails(url, category, productName, numberOfReviews, lastDateReview);
		
		return null;
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
						// E' utile? forse c'� sempre solo  '&gt;&nbsp;'
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
		}

		return "";
	}
	
	public PageList createPageList(Source source, String url, String category){
		Element el = source.getElementById(TABLERESULT);
		
		List<PageDetails> pageDetailsProducts = new ArrayList<PageDetails>();
		List<Element> products = el.getAllElementsByClass(PRODUCTROWNOBORDER);
		for(Element productrow : products){
			PageDetails pageDetails = this.getPageDetailsFromRow(productrow, category);
			pageDetailsProducts.add(pageDetails);
		}
		
		products = el.getAllElementsByClass(PRODUCTROW);
		for(Element productrow : products){
			PageDetails pageDetails = this.getPageDetailsFromRow(productrow, category);
			pageDetailsProducts.add(pageDetails);
		}
		
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
				rev = rev.substring(0, rev.indexOf(" "));
				review = Integer.parseInt(rev);
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
				productName = e.getContent().toString();
			}
		}
		
		System.out.println(productName +" - "+ url + " - "+ review);
		
		// La data della lastReview non � ricavabile dalle pagine di dettaglio quindi viene settata a null
		PageDetails pageDetails = new PageDetails(url, category, productName, review, null);
		return pageDetails;
	}

	public  void run(){

		uncategorized = new ArrayList<String>();
		//List<String> listaFile=Utility.listFiles("./epinionsExamplePages");
		//List<String> listaFile=Utility.listFiles("./EpinionsTemp");
		//List<String> listaFile=Utility.listFiles("/Users/dokkis/Downloads/www.epinions.com");
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
				
				String category = classifyPage(source);
				
				if(source.getElementById("product_top_box")!=null){
					try {
						createPageDetails(source,category,url);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("DetailsPage created");
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
				
				
				if(source.getElementById(TABLERESULT) != null){
					createPageList(source, url, category);
					System.out.println(url + " PAGINA DI ELENCO");
				}
				
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
