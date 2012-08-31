package db;

public class DBQuery {
	public static final String INSERTPAGE = "INSERT INTO PAGE(url, category, iddetails) VALUES (?, ?, ?)";
	public static final String INSERTPAGEDETAILS = "INSERT INTO PAGEDETAILS(productName, numberOfReviews, numberOfReviewsList, lastDateReviews) VALUES (?, ?, ?, ?)";
	public static final String INSERTPAGELISTAGGREGATION = "INSERT INTO PAGELISTAGGREGATION VALUES (?, ?)";
}
