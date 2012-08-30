package classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class EpinionsClassifier extends PageClassifier{
	List<String> pagine;
	List<String> uncategorized;
	private static final String SPAN = "span";
	private static final String CLASS = "class";
	private static final String HOME = "Home";
	private final String BREADCRUMB = "breadcrumb";
	private final String HREF = "a";
	private final String RKR = "rkr";
	private final String RGR = "rgr";
	private final String MAGGIORE = " &gt;&nbsp;";

	public EpinionsClassifier(List<String> pagine){ 
		this.pagine=pagine;
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
				System.out.println("BREADCRUMB" + breadcrumb.toString());

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
								System.out.println("IF: "+category.getContent());
							}
						} else{
							if(!MAGGIORE.equals(el.getContent().toString())){
								breadcrumbCategory.add(el.getContent().toString());
								System.out.println("ELSE: "+el.getContent());
							}
						}
					}
				}

				/*elements = breadcrumb.getAllElementsByClass(RKR);
				for(Element el : elements){
					List<Element> links = el.getAllElements(HREF);
					if(links.size()>0){
						Element category = links.get(0);
						breadcrumbCategory.add(category.getContent().toString());
					} else{
						// E' utile? forse c'è sempre solo  '&gt;&nbsp;'
						////System.out.println(el.getContent());
					}
				}

				elements = breadcrumb.getAllElementsByClass(RGR);
				for(Element category : elements){
					breadcrumbCategory.add(category.getContent().toString());
				}*/
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
										System.out.println("HOME: "+category.getContent());
										parentHome = el.getParentElement();
										System.out.println("PARENT: "+parentHome);
									}
								
									breadcrumbCategory.add(category.getContent().toString());
									System.out.println("IF: "+category.getContent());
								}
							}
						} else{
							if(!MAGGIORE.equals(el.getContent().toString())){
								if(el.getParentElement()!=null && el.getParentElement().equals(parentHome) || el.getContent().toString().equalsIgnoreCase(HOME)){
									if(parentHome==null){
										parentHome = el.getParentElement();
										System.out.println("PARENT: "+parentHome);
									}
									breadcrumbCategory.add(el.getContent().toString());
									System.out.println("ELSE: "+el.getContent());
								}
							}
						}
					//}
				}
			}
		}

		System.out.print("Breadcrumb: " + Utility.listToBreadcrumb(breadcrumbCategory));
		System.out.println();

		String category = "";
		if(breadcrumbCategory.size()>=2){
			category = breadcrumbCategory.get(1);
		}

		return category;
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
				
				Source source = new Source(Utility.fileToString(url));
				source.fullSequentialParse();
				
				String category = classifyPage(source);
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
