package classifier;

import java.util.Date;
import java.util.List;

import model.PageDetails;

public interface Services {
	//Dato un URL di una pagina restituisce la sua categoria
	public String URLToCategory(String url);
	
	//Dato un URL di una PageList restituisce una lista di Pagine di dettaglio
	public List<PageDetails>  URLToProducts(String url);
	
	//Dato un URL di una pagina di dettaglio restituisce il numero di Recensioni
	public int URLToNumReviews(String url);
	
	//Dato un URL di una pagina di dettaglio restituisce la data dell'ultima recensione 
	public Date URLToLastDateReview(String url);
	
	//Data una categoria restituisce tutte la pagine appartenenti ad essa
	public List<PageDetails> CategoryToPages(String category);
}
