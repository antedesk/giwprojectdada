package classifier;

import net.htmlparser.jericho.Source;

public abstract class PageClassifier extends Thread{
	
	public abstract String classifyPage(Source source);
}
