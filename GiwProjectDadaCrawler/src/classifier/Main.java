package classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import services.ServicesImpl;

import model.Page;
import model.PageDetails;
import crawler.BasicCrawlController;
import db.DAOServices;
import db.DBDatasource;
/*
 * @author Antonio Gallo
 * @author Daniele D'Andrea
 * @author Antonio Tedeschi
 * @author Daniele Malta
 */
public class Main {

	public static void main(String[] args) throws Exception{
		while(true){
			try{
				runCommand();
			} catch(Exception ex){ ex.printStackTrace(); }

			System.out.println();
			Thread.sleep(2000);
		}
	}

	public static void runCommand() throws IOException, Exception,
	SQLException, ParseException {
		System.out.println("I servizi a disposizione sono:"
				+"\n1) URL To Category"
				+"\n2) URL To Products"
				+"\n3) URL To NumReviews"
				+"\n4) URL To LastDateReview"
				+"\n5) Category To Pages"
				+"\n6) Date To Pages"
				+"\n7) Ranges Date To Pages"
				+"\n8) Ranges Date To Categories"
				+"\n9) Date To Back Pages"
				+"\n10) Date To Future Pages"
				+"\n11) Processa TripAdvisor directory"
				+"\n12) Processa Epinions directory"
				+"\n13) Clear Database Tables"
				+"\n14) Lancia WebCrawler"
				+"\n15) Per terminare il programma");

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String service = stdin.readLine();
		ServicesImpl serv = new ServicesImpl(); 

		if(service.equals("1"))
		{
			System.out.println("Inserisci l'url della pagina di cui si vuole conoscere la categoria");
			String url = stdin.readLine();
			System.out.println(serv.URLToCategory(url));
		}
		else if(service.equals("2"))
		{
			System.out.println("Inserisci l'url della page List di cui si vogliono conoscere i prodotti");
			String url = stdin.readLine();
			for (PageDetails pl : serv.URLToProducts(url)) {
				System.out.println(pl.getProductName());
			}
		}
		else if(service.equals("3"))
		{
			System.out.println("Inserisci l'url della pagina di cui si vuole conoscere il numero di review");
			String url = stdin.readLine();
			System.out.println(serv.URLToNumReviews(url));
		}
		else if(service.equals("4"))
		{
			System.out.println("Inserisci l'url della pagina di cui si vuole conoscere l'ultima data di aggiornamento");
			String url = stdin.readLine();
			System.out.println(serv.URLToLastDateReview(url));
		}
		else if(service.equals("5"))
		{
			System.out.println("Inserisci la categoria di prodotti di cui si vuole conoscere i prodotti in essa racchiusa");
			String category  = stdin.readLine();
			for (Page p : serv.CategoryToPages(category)) {
				System.out.println(p.toString());
			}
		}
		else if(service.equals("6"))
		{
			System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a quella data");
			System.out.println("il formato della data richiesto �: dd/MM/yyyy");
			String stringDate = stdin.readLine();
			Date date = null;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = formatter.parse(stringDate);
			System.out.println("Size: "+serv.DateToPages(date).size());
			for (PageDetails p : serv.DateToPages(date)) {
				System.out.println(p);
			}
		}
		else if(service.equals("7"))
		{
			System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
			System.out.println("il formato della data richiesto �: dd/MM/yyyy");
			System.out.println("Inserisci la prima data");
			String stringDate1 = stdin.readLine();
			Date date1 = null;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date1 = (Date)formatter.parse(stringDate1);
			System.out.println("Inserisci la seconda data");
			String stringDate2 = stdin.readLine();
			Date date2 = (Date)formatter.parse(stringDate2);
			for (PageDetails p : serv.RangesDateToPages(date1, date2)) {
				System.out.println(p);
			}
		}
		else if(service.equals("8"))
		{
			System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
			System.out.println("Inserisci la prima data");
			System.out.println("il formato della data richiesto �: dd/MM/yyyy");
			String stringDate1 = stdin.readLine();
			Date date1 = null;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date1 = (Date)formatter.parse(stringDate1);
			System.out.println("Inserisci la seconda data");
			String stringDate2 = stdin.readLine();
			Date date2 = (Date)formatter.parse(stringDate2);
			System.out.println(serv.RangesDateToCategories(date1, date2));
		}
		else if(service.equals("9"))
		{
			System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a prima di quella data");
			System.out.println("il formato della data richiesto �: dd/MM/yyyy");
			String stringDate = stdin.readLine();
			Date date = null;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = (Date)formatter.parse(stringDate);
			for (PageDetails p : serv.DateToBackPages(date)) {
				System.out.println(p);
			}
		}
		else if(service.equals("10"))
		{
			System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a dopo quella data");
			System.out.println("il formato della data richiesto �: dd/MM/yyyy");
			String stringDate = stdin.readLine();
			Date date = null;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = (Date)formatter.parse(stringDate);
			for (PageDetails p : serv.DateToFuturePages(date)) {
				System.out.println(p);
			}
		}
		else if(service.equals("11")){
			System.out.println("Inserisci la ROOT directory delle pagine scaricate di TripAdvisor");
			String dir = stdin.readLine();
			System.out.println("Inserisci il numero di Thread per l'elaborazione");
			int thread = Integer.parseInt(stdin.readLine());
			Main.LaunchClassifier(dir, thread, 0);
		}
		else if(service.equals("12")){
			System.out.println("Inserisci la ROOT directory delle pagine scaricate di Epinions");
			String dir = stdin.readLine();
			System.out.println("Inserisci il numero di Thread per l'elaborazione");
			int thread = Integer.parseInt(stdin.readLine());
			Main.LaunchClassifier(dir, thread, 1);
		}
		else if(service.equals("13")){
			serv.ClearDatabase();
			System.out.println("Tabelle del database cancellate con successo");
		}
		else if(service.equals("14")){
			System.out.println("Avviato WebCrawler, il processo di crawling � stoppabile e riprendibile in un secondo momento dallo stesso punto in cui si era fermato.");
			System.out.println("Inserisci l'indirizzo del sito da cui far partire il crawler");
			String site = stdin.readLine();
			System.out.println("Inserisci il numero di Thread per l'elaborazione");
			int numberOfCrawlers = Integer.parseInt(stdin.readLine());
			System.out.println("Inserisci il numero di Profondit� massima per il crawler (-1 = nessuna limitazione di profondit�)");
			int depth = Integer.parseInt(stdin.readLine());
			System.out.println("Inserisci il numero di Massimo di pagine da scaricare (-1 = nessuna limitazione di profondit�)");
			int maxpage = Integer.parseInt(stdin.readLine());

			System.out.println("Partito Crawler sul sito "+site);

			BasicCrawlController basicCrawlController = new BasicCrawlController();
			basicCrawlController.startCrawler(site, numberOfCrawlers, depth, maxpage);

			System.out.println("WebCrawler Terminato");
		}
		else if(service.equals("15")){
			System.exit(0);
		}
	}

	// type = 0 TripAdvisor, type = 1 Epinions
	public static void LaunchClassifier(String root, int numThread, int type) throws Exception{
		DBDatasource dbDataSource;
		DAOServices dao;
		Connection conn;

		dbDataSource = new DBDatasource();
		conn = dbDataSource.getConnection();
		dao = new DAOServices(conn);

		List<String> allPages=Utility.listFiles(root);

		int PagesPerThread=allPages.size()/numThread;
		List<PageClassifier> classifiers = new ArrayList<PageClassifier>();
		for(int i = 0; i<numThread; i++){
			PageClassifier threadClassifier=null;

			if(type==0)
				threadClassifier = new TripAdvisorClassifier(dao, root,allPages.subList(i*PagesPerThread, (i+1)*PagesPerThread));
			else
				threadClassifier = new EpinionsClassifier(dao, root, allPages.subList(i*PagesPerThread, (i+1)*PagesPerThread));

			threadClassifier.start();
			classifiers.add(threadClassifier);
		}

		for(PageClassifier threadClassifier : classifiers){
			threadClassifier.join();
		}

		dbDataSource.closeConnection(conn);

		List<String> notCat = new LinkedList<String>();
		for(PageClassifier threadClassifier : classifiers){
			notCat.addAll(threadClassifier.getUncategorizedLink());
		}

		System.out.println("\n*************** Pagine non categorizzate ***************");
		for(String url : notCat){
			System.out.println(url);
		}
		System.out.println("********************************************************");
		System.out.println("tot non categorizzate: "+notCat.size());
	}
}
