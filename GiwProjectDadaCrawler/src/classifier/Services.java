package classifier;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import model.Page;
import model.PageDetails;

public interface Services {

	//Dato un URL di una pagina restituisce la sua categoria
	public String URLToCategory(String url) throws SQLException;
	
	//Dato un URL di una PageList restituisce una lista di Pagine di dettaglio
	public List<PageDetails>  URLToProducts(String url) throws SQLException;
	
	//Dato un URL di una pagina di dettaglio restituisce il numero di Recensioni
	public int URLToNumReviews(String url) throws SQLException;
	
	//Dato un URL di una pagina di dettaglio restituisce la data dell'ultima recensione 
	public Date URLToLastDateReview(String url) throws SQLException;
	
	//Data una categoria restituisce tutte la pagine appartenenti ad essa
	public List<Page> CategoryToPages(String category) throws SQLException;
	
	//Data una data restituisce tutte le pagine con quella data
	public List<PageDetails> DateToPages(Date date) throws SQLException;

	//Data una date restituisce tutte le pagine prima di quella data
	public List<PageDetails> DateToBackPages(Date Date) throws SQLException;

	//Data una date restituisce tutte le pagine dopo di quella data
	public List<PageDetails> DateToFuturePages(Date Date) throws SQLException;
	
	//Date due date restituisce tutte le pagine relative a quel periodo, estremi inclusi
	public List<PageDetails> RangesDateToPages(Date minDate,Date maxDate) throws SQLException;

	//Date due date restituisce tutte le categorie delle pagine relative a quel periodo, estremi inclusi
	public List<String> RangesDateToCategories(Date minDate,Date maxDate) throws SQLException;


}
