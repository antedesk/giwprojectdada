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

import model.PageDetails;

public class Main {
	
	public static void main(String[] args) throws Exception{
		System.out.println("I servizi a disposizione sono:"
								+"\nURL To Category"
								+"\nURL To Products"
								+"\nURL To NumReviews"
								+"\nURL To LastDateReview"
								+"\nCategory To Pages"
								+"\nDate To Pages"
								+"\nRanges Date To Pages"
								+"\nRanges Date To Categories"
								+"\nDate To Back Pages"
								+"\nDate To Future Pages");
		 BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
         String service = stdin.readLine();
         ServicesImpl serv = new ServicesImpl(); 
        
         if(service.equals("URL To Category"))
         {
        	 System.out.println("Inserisci l'url della pagina di cui si vuole conoscere la categoria");
        	 String url = stdin.readLine();
        	 serv.URLToCategory(url);
         }
         else if(service.equals("URL To Products"))
         {
        	 System.out.println("Inserisci l'url della pagina di cui si vuole conoscere il prodotto");
        	 String url = stdin.readLine();
        	 serv.URLToProducts(url);
         }
         else if(service.equals("URL To NumReviews"))
         {
        	 System.out.println("Inserisci l'url della pagina di cui si vuole conoscere il numero di review");
        	 String url = stdin.readLine();
        	 serv.URLToNumReviews(url);
         }
		 else if(service.equals("URL To LastDateReview"))
         {
        	 System.out.println("Inserisci l'url della pagina di cui si vuole conoscere l'ultima data di aggiornamento");
        	 String url = stdin.readLine();
        	 serv.URLToLastDateReview(url);
         }
		 else if(service.equals("Category To Pages"))
         {
        	 System.out.println("Inserisci la categoria di prodotti di cui si vuole conoscere i prodotti in essa racchiusa");
        	 String category  = stdin.readLine();
        	 serv.CategoryToPages(category);
         }
		 else if(service.equals("Date To Pages"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a quella data");
        	 System.out.println("il formato della data richiesto è: dd/mm/yyyy");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date = (Date)formatter.parse(stringDate);
        	 serv.DateToPages(date);
         }
		 else if(service.equals("Ranges Date To Pages"))
         {
        	 System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
        	 System.out.println("il formato della data richiesto è: dd/mm/yyyy");
        	 System.out.println("Inserisci la prima data");
        	 String stringDate1 = stdin.readLine();
        	 Date date1 = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date1 = (Date)formatter.parse(stringDate1);
        	 System.out.println("Inserisci la seconda data");
        	 String stringDate2 = stdin.readLine();
        	 Date date2 = (Date)formatter.parse(stringDate2);
        	 serv.RangesDateToPages(date1, date2);
         }
		 else if(service.equals("Ranges Date To Categories"))
         {
        	 System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
        	 System.out.println("Inserisci la prima data");
        	 System.out.println("il formato della data richiesto è: dd/mm/yyyy");
        	 String stringDate1 = stdin.readLine();
        	 Date date1 = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date1 = (Date)formatter.parse(stringDate1);
        	 System.out.println("Inserisci la seconda data");
        	 String stringDate2 = stdin.readLine();
        	 Date date2 = (Date)formatter.parse(stringDate2);
        	 serv.RangesDateToCategories(date1, date2);
         }
		 else if(service.equals("Date To Back Pages"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a prima di quella data");
        	 System.out.println("il formato della data richiesto è: dd/mm/yyyy");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date = (Date)formatter.parse(stringDate);
        	 serv.DateToBackPages(date);
         }
		 else if(service.equals("Date To Future Pages"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a dopo quella data");
        	 System.out.println("il formato della data richiesto è: dd/mm/yyyy");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.ITALY);
     		 date = (Date)formatter.parse(stringDate);
        	 serv.DateToFuturePages(date);
         }
        		
	}
}
