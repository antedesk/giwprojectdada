package db;

public class DBQuery {
	public static final String INSERTPAGE = "INSERT INTO PAGE(url, category, iddetails) VALUES (?, ?, ?)";
	public static final String INSERTPAGEDETAILS = "INSERT INTO PAGEDETAILS(productName, numberOfReviews, numberOfReviewsList, lastDateReviews) VALUES (?, ?, ?, ?)";
	public static final String INSERTPAGELISTAGGREGATION = "INSERT INTO PAGELISTAGGREGATION VALUES (?, ?)";

	public static final String SELECTPAGEDETAILSFROMPRODUCTNAME = "SELECT url, category, productName, numberOfReviews, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE productName=?";
	public static final String SELECTPAGELIST = "SELECT id FROM page WHERE url=?";
	public static final String DELETEPAGE = "DELETE FROM page WHERE id=?";
	public static final String DELETEAGGRPAGE = "DELETE FROM pagelistaggregation WHERE idPageList=?";
}
