package classifier;

import java.text.ParseException;
import java.util.List;

import model.PageDetails;
import model.PageList;
import net.htmlparser.jericho.Source;

public abstract class PageClassifier extends Thread{
	
	protected List<String> uncategorized;
	
	public List<String> getUncategorizedLink(){
		return uncategorized;
	}
	
	public abstract String classifyPage(Source source);
	
	public abstract PageDetails createPageDetails(Source source,String category,String url) throws ParseException;
	
	public abstract PageList createPageList(Source source, String url, String category);
}
