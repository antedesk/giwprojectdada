package model;

import java.util.Date;

public class PageDetails {
	
	private int id;
	private String url;
	private String category;
	private String productName;
	private int numberOfReviews;
	private int numberOfReviewsList;
	private Date lastDateReview;
	
	public PageDetails(int id, String url, String category, String productName,
			int numberOfReviews, int numberOfReviewsList, Date lastDateReview) {
		super();
		this.id = id;
		this.url = url;
		this.category = category;
		this.productName = productName;
		this.numberOfReviews = numberOfReviews;
		this.numberOfReviewsList = numberOfReviewsList;
		this.lastDateReview = lastDateReview;
	}
	
	public PageDetails(String url, String category, String productName,
			int numberOfReviews, int numberOfReviewsList, Date lastDateReview) {
		super();
		this.url = url;
		this.category = category;
		this.productName = productName;
		this.numberOfReviews = numberOfReviews;
		this.numberOfReviewsList = numberOfReviewsList;
		this.lastDateReview = lastDateReview;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getNumberOfReviews() {
		return numberOfReviews;
	}

	public void setNumberOfReviews(int numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}

	public Date getLastDateReview() {
		return lastDateReview;
	}

	public void setLastDateReview(Date lastDateReview) {
		this.lastDateReview = lastDateReview;
	}
	
	public int getNumberOfReviewsList() {
		return numberOfReviewsList;
	}
	public void setNumberOfReviewsList(int numberOfReviewsList) {
		this.numberOfReviewsList = numberOfReviewsList;
	}

	public String toString(){
		return  "URL="+url+ "  Category="
	+this.category+"  ProductName="+productName+"  NumberOfReviews="
				+numberOfReviews+"  LastDateReview="+lastDateReview;
	}
}
