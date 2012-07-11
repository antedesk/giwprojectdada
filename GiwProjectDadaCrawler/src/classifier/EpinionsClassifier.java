package classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;


public class EpinionsClassifier implements PageClassifier{

	private final String BREADCRUMB = "breadcrumb";
	private final String HREF = "a";
	private final String RKR = "rkr";
	private final String RGR = "rgr";
	
	public EpinionsClassifier(){ }
	
	public String classifyPage(String html){
		
		Source source= new Source(html);
		List<Element> elements = source.getAllElementsByClass(BREADCRUMB);
		List<String> breadcrumbCategory = new ArrayList<String>();
		if(elements.size()>0){
			Element breadcrumb = elements.get(0);
			if(breadcrumb!=null){
				//System.out.println(breadcrumb.toString());
				
				elements = breadcrumb.getAllElementsByClass(RKR);
				for(Element el : elements){
					List<Element> links = el.getAllElements(HREF);
					if(links.size()>0){
						Element category = links.get(0);
						breadcrumbCategory.add(category.getContent().toString());
					} else{
						// E' utile? forse c' sempre solo  '&gt;&nbsp;'
						//System.out.println(el.getContent());
					}
				}
				
				elements = breadcrumb.getAllElementsByClass(RGR);
				for(Element category : elements){
					breadcrumbCategory.add(category.getContent().toString());
				}
			}
		}
		
		System.out.print("Breadcrumb: ");
		for(String cat : breadcrumbCategory){
			System.out.print(cat + " -> ");
		}
		System.out.println();
			
		String category = "";
		if(breadcrumbCategory.size()>=1){
			category = breadcrumbCategory.get(1);
		}
		
		return category;
	}

	public static void main(String args[]) throws IOException{
		EpinionsClassifier t=new EpinionsClassifier();
		List<String> listaFile=Utility.listFiles("./epinionsExamplePages");
		
		List<String> uncategorized = new ArrayList<String>();
		for (String url : listaFile) {
			if(!url.contains("/.svn/") && !url.contains("/.DS_Store")){
				System.out.println("********************************************************");
				System.out.println("URL: "+url);
				String category = t.classifyPage(Utility.fileToString(url));
				if(!category.equals(""))
					System.out.println("Categoria (breadcrumb[1]) = " + category);
				else
					uncategorized.add(url);
				System.out.println("********************************************************\n");
			}
		}
		
		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : uncategorized)
			System.out.println(url);
		System.out.println("********************************************************");
	}
}
