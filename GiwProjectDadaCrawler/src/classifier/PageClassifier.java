package classifier;

public abstract class PageClassifier extends Thread{
	
	public abstract String classifyPage(String html);
}
