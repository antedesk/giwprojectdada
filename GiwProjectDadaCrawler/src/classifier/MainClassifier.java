package classifier;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.PageDetails;
import model.PageList;
import db.DAOServices;
import db.DBDatasource;

public class MainClassifier {
	public static void mainR(String[] args) throws IOException, InterruptedException{
		List<String> allPages = Utility.listFiles("./TripAdvisorExamplePages");
		//List<String> allPages = Utility.listFiles("/Volumes/LaCie/tripBackup");
		//List<String> allPages = Utility.listFiles("/Users/Geppo/Desktop/tripadvisor");
		int numThread=4;
		int PagePerThread=allPages.size()/numThread;
		TripAdvisorClassifier t1=new TripAdvisorClassifier(allPages.subList(0, 0+PagePerThread));
		TripAdvisorClassifier t2=new TripAdvisorClassifier(allPages.subList(PagePerThread, PagePerThread*2));
		TripAdvisorClassifier t3=new TripAdvisorClassifier(allPages.subList(PagePerThread*2,PagePerThread*3));
		TripAdvisorClassifier t4=new TripAdvisorClassifier(allPages.subList(PagePerThread*3,allPages.size()));

		t1.start();t2.start();t3.start();t4.start();
		t1.join();t2.join();t3.join();t4.join();

		List<String> notCat = new LinkedList<String>();
		notCat.addAll(t1.uncategorized);
		notCat.addAll(t2.uncategorized);
		notCat.addAll(t3.uncategorized);
		notCat.addAll(t4.uncategorized);

		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : notCat)
			System.out.println(url);
				System.out.println("********************************************************");
				System.out.println("tot non categorizzate: "+notCat.size());

	}
	public static void mainS(String[] args) throws IOException, InterruptedException{


		//List<String> allPages=Utility.listFiles("./EpinionsTemp");
		List<String> allPages = Utility.listFiles("./epinionsExamplePages");
		int numThread=1;
		int PagePerThread=allPages.size()/numThread;
		EpinionsClassifier t1=new EpinionsClassifier(allPages);//.subList(0, 0+PagePerThread));
		//EpinionsClassifier t2=new EpinionsClassifier(allPages.subList(PagePerThread, PagePerThread*2));
		//EpinionsClassifier t3=new EpinionsClassifier(allPages.subList(PagePerThread*2,PagePerThread*3));
		//EpinionsClassifier t4=new EpinionsClassifier(allPages.subList(PagePerThread*3,allPages.size()));

		t1.start();//t2.start();t3.start();t4.start();
		t1.join();//t2.join();t3.join();t4.join();

		List<String> notCat = new LinkedList<String>();
		notCat.addAll(t1.uncategorized);
		//notCat.addAll(t2.uncategorized);
		//notCat.addAll(t3.uncategorized);
		//notCat.addAll(t4.uncategorized);

		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : notCat)
			System.out.println(url);
				System.out.println("********************************************************");
				System.out.println("tot non categorizzate: "+notCat.size());

	}

	public static void main(String[] args) throws Exception{
		DBDatasource dbDataSource = new DBDatasource();
		Connection conn = dbDataSource.getConnection();

		DAOServices dao = new DAOServices(conn);
		System.out.println(dao.getPageDetailsFromProductName("Apple iPhone 3GS White (16 GB) Smartphone").toString());
		/*
		List<PageDetails> products = new ArrayList<PageDetails>();
		products.add(new PageDetails("url1", "electronics", "ipod", 10, new Date()));
		products.add(new PageDetails("url2", "electronics", "iphone", 42, new Date()));
		products.add(new PageDetails("url3", "electronics", "galaxy s3", 110, new Date()));
		products.add(new PageDetails("url4", "electronics", "nexus 7", 1, new Date()));
		PageList pageList = new PageList("asdasd", "lollol", products);

		dao.insertPage(pageList);*/
	}
}
