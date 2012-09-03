package model;

public class Page {

	protected int id;
	protected String url;
	protected String category;
	
	public Page(int id, String url,String category){
		super();
		this.id=id;
		this.category=category;
		this.url=url;
	}
	
	public Page(String url,String category){
		super();
		this.category=category;
		this.url=url;
	}
}
