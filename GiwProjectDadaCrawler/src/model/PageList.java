package model;

import java.util.List;

public class PageList {
	
	private String url;
	private String category;
	private List<PageDetails> products;
	
	public PageList(String url, String category, List<PageDetails> products) {
		super();
		this.url = url;
		this.category = category;
		this.products = products;
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
}
