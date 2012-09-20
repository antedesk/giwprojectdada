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
	public static void mainT(String[] args) throws Exception{
		List<String> allPages = Utility.listFiles("./TripAdvisorExamplePages");
		//List<String> allPages = Utility.listFiles("/Volumes/LaCie/tripBackup");
		//List<String> allPages = Utility.listFiles("/Users/Geppo/Desktop/tripadvisor");
		int numThread=4;
		int PagePerThread=allPages.size()/numThread;
		DBDatasource dbDataSource;
		DAOServices dao;
		Connection conn;

		dbDataSource = new DBDatasource();
		conn = dbDataSource.getConnection();
		dao = new DAOServices(conn);
	/*	TripAdvisorClassifier t1=new TripAdvisorClassifier(dao,allPages);
		

		t1.start();
		t1.join();

		List<String> notCat = new LinkedList<String>();
		notCat.addAll(t1.uncategorized);


		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : notCat)
			System.out.println(url);
				System.out.println("********************************************************");
				System.out.println("tot non categorizzate: "+notCat.size());
*/
	}
	public static void main(String[] args) throws Exception{
		DBDatasource dbDataSource;
		DAOServices dao;
		Connection conn;

		dbDataSource = new DBDatasource();
		conn = dbDataSource.getConnection();
		dao = new DAOServices(conn);
		
		String rootFile = "./EpinionsTemp/";
		//String rootFile = "./epinionsExamplePages";
		//String rootFile = "/Users/dokkis/Downloads/www.epinions.com";
		
		List<String> allPages=Utility.listFiles(rootFile);
		int numThread=1;
		int PagePerThread=allPages.size()/numThread;
		EpinionsClassifier t1=new EpinionsClassifier(dao, rootFile, allPages);//.subList(0, 0+PagePerThread));
		//EpinionsClassifier t2=new EpinionsClassifier(allPages.subList(PagePerThread, PagePerThread*2));
		//EpinionsClassifier t3=new EpinionsClassifier(allPages.subList(PagePerThread*2,PagePerThread*3));
		//EpinionsClassifier t4=new EpinionsClassifier(allPages.subList(PagePerThread*3,allPages.size()));

		t1.start();//t2.start();t3.start();t4.start();
		t1.join();//t2.join();t3.join();t4.join();
		
		dbDataSource.closeConnection(conn);

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

	public static void mainTestDAO(String[] args) throws Exception{
		DBDatasource dbDataSource = new DBDatasource();
		Connection conn = dbDataSource.getConnection();

		DAOServices dao = new DAOServices(conn);

		List<PageDetails> products = new ArrayList<PageDetails>();
		products.add(new PageDetails("url1", "electronics", "ipod", 10, 10, new Date()));
		products.add(new PageDetails("url2", "electronics", "iphone", 42, 42, new Date()));
		products.add(new PageDetails("url3", "electronics", "galaxy s3", 110, 110, new Date()));
		products.add(new PageDetails("url4", "electronics", "nexus 7", 1, 1, new Date()));
		PageList pageList = new PageList("asdasd", "lollol", products);

		dao.saveOrUpdatePageList(pageList);
	}
}
