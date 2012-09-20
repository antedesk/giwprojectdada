package classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.Page;
import model.PageDetails;
import model.PageList;

public class Main {
	
	public static void main(String[] args) throws Exception{
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
								+"\n10) Date To Future Pages");
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
         //CONTROLLARE
		 else if(service.equals("6"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a quella data");
        	 System.out.println("il formato della data richiesto �: dd/mm/yyyy");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date = formatter.parse(stringDate);
     		for (PageDetails p : serv.DateToPages(date)) {
 				System.out.println(p.getProductName());
        	 }
         }
		 else if(service.equals("7"))
         {
        	 System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
        	 System.out.println("il formato della data richiesto �: dd/mm/yyyy");
        	 System.out.println("Inserisci la prima data");
        	 String stringDate1 = stdin.readLine();
        	 Date date1 = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date1 = (Date)formatter.parse(stringDate1);
        	 System.out.println("Inserisci la seconda data");
        	 String stringDate2 = stdin.readLine();
        	 Date date2 = (Date)formatter.parse(stringDate2);
        	 System.out.println(serv.RangesDateToPages(date1, date2));
         }
		 else if(service.equals("8"))
         {
        	 System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
        	 System.out.println("Inserisci la prima data");
        	 System.out.println("il formato della data richiesto �: dd/mm/yyyy");
        	 String stringDate1 = stdin.readLine();
        	 Date date1 = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date1 = (Date)formatter.parse(stringDate1);
        	 System.out.println("Inserisci la seconda data");
        	 String stringDate2 = stdin.readLine();
        	 Date date2 = (Date)formatter.parse(stringDate2);
        	 System.out.println(serv.RangesDateToCategories(date1, date2));
         }
		 else if(service.equals("9"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a prima di quella data");
        	 System.out.println("il formato della data richiesto �: dd/mm/yyyy");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date = (Date)formatter.parse(stringDate);
        	 System.out.println(serv.DateToBackPages(date));
         }
		 else if(service.equals("10"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a dopo quella data");
        	 System.out.println("il formato della data richiesto �: dd/mm/yyyy");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date = (Date)formatter.parse(stringDate);
        	 System.out.println(serv.DateToFuturePages(date));
         }
        		
	}
}
