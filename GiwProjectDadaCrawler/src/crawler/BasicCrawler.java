package crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Pattern;

public class BasicCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith("http://"+BasicCrawlController.site);
	}

	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Depth: " + page.getWebURL().getDepth());
		System.out.println("Domain: '" + domain + "'");
		System.out.println("Sub-domain: '" + subDomain + "'");
		System.out.println("Path: '" + path + "'");
		System.out.println("Parent page: " + parentUrl);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());

			try{
				String[] dirsToCreate = path.replace("\\", "/").split("/");
				String before = BasicCrawlController.site+"/";
				for(String p : dirsToCreate){
					if(!p.equals("")){
						String dirtocreate = before+p;
						File theDir = new File(dirtocreate);

						if (!theDir.exists())
						{
							//System.out.println("creating directory: " + dirtocreate);
							boolean result = theDir.mkdir();  
							//if(result){    
							//	System.out.println("DIR created");  
							//}
						}
						before += p+"/";
					}
				}
				String pathfile = BasicCrawlController.site+"/"+path+"/index.html";
				FileWriter fstream = new FileWriter(pathfile);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(html);
				out.close();
			}
			catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}

		System.out.println("=============");
	}
}