package model;

import java.util.List;
/*
* @author Antonio Gallo
* @author Daniele D'Andrea
* @author Antonio Tedeschi
* @author Daniele Malta
*/
public class PageList extends Page{
	
	
	private List<PageDetails> products;
	
	public PageList(int id, String url, String category, List<PageDetails> products) {
		super(id,url,category);

		this.products = products;
	}
	
	public PageList(String url, String category, List<PageDetails> products) {
		super(url,category);
		this.products = products;
	}
	
	public int getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<PageDetails> getProducts() {
		return products;
	}
	public void setProducts(List<PageDetails> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		String toString = "URL="+url+ "  Category="+this.category+"\n";
		toString += "Products: ";
		for(PageDetails pd : products)
			toString += pd.toString() + " | ";
		return toString;
	}
	
	
}
