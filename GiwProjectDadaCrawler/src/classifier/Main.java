package classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 serv.DateToPages(date);
         }
		 else if(service.equals("Ranges Date To Pages"))
         {
        	 System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
        	 System.out.println("Inserisci la prima data");
        	 String stringDate1 = stdin.readLine();
        	 Date date1 = null;
        	 System.out.println("Inserisci la seconda data");
        	 String stringDate2 = stdin.readLine();
        	 Date date2 = null;
        	 serv.RangesDateToPages(date1, date2);
         }
		 else if(service.equals("Ranges Date To Categories"))
         {
        	 System.out.println("Inserisci due date di interesse da cui otterene tutte le pagine risalenti a quel dato range");
        	 System.out.println("Inserisci la prima data");
        	 String stringDate1 = stdin.readLine();
        	 Date date1 = null;
        	 System.out.println("Inserisci la seconda data");
        	 String stringDate2 = stdin.readLine();
        	 Date date2 = null;
        	 serv.RangesDateToCategories(date1, date2);
         }
		 else if(service.equals("Date To Back Pages"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a prima di quella data");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 serv.DateToBackPages(date);
         }
		 else if(service.equals("Date To Future Pages"))
         {
        	 System.out.println("Inserisci la data di interesse da cui otterene tutte le pagine risalenti a dopo quella data");
        	 String stringDate = stdin.readLine();
        	 Date date = null;
        	 serv.DateToFuturePages(date);
         }
        		
	}
}
