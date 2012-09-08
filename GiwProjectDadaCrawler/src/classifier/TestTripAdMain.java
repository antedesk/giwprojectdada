package classifier;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TestTripAdMain {
	
	
	public static void main(String[] args) throws IOException, InterruptedException{
		List<String> allPages = Utility.listFiles("./TripPageTest");
		int numThread=4;
		int PagePerThread=allPages.size()/numThread;
		TripAdvisorClassifier t1=new TripAdvisorClassifier(allPages);
		

		t1.start();
		t1.join();

		List<String> notCat = new LinkedList<String>();
		notCat.addAll(t1.uncategorized);


		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : notCat)
			System.out.println(url);
				System.out.println("********************************************************");
				System.out.println("tot non categorizzate: "+notCat.size());

	}
}
