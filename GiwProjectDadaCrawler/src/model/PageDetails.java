package model;

import java.util.Date;

public class PageDetails {
	
	private String url;
	private String category;
	private String productName;
	private int numberOfReviews;
	private Date lastDateReview;
	
	public PageDetails(String url, String category, String productName,
			int numberOfReviews, Date lastDateReview) {
		super();
		this.url = url;
		this.category = category;
		this.productName = productName;
		this.numberOfReviews = numberOfReviews;
		this.lastDateReview = lastDateReview;
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
}
