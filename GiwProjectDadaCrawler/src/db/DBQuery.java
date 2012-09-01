package db;

public class DBQuery {
	public static final String INSERTPAGE = "INSERT INTO PAGE(url, category, iddetails) VALUES (?, ?, ?)";
	public static final String INSERTPAGEDETAILS = "INSERT INTO PAGEDETAILS(productName, numberOfReviews, numberOfReviewsList, lastDateReviews) VALUES (?, ?, ?, ?)";
	public static final String INSERTPAGELISTAGGREGATION = "INSERT IGNORE INTO PAGELISTAGGREGATION VALUES (?, ?)";
	public static final String UPDATEPAGEDETAILS = "UPDATE PAGEDETAILS SET numberOfReviews = ?, numberOfReviewsList = ?, lastDateReviews = ? WHERE ID = ?";
	public static final String SELECTPAGEDETAILSFROMPRODUCTNAME = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE productName=?";
	public static final String SELECTPAGELIST = "SELECT id FROM page WHERE url=?";
	public static final String DELETEPAGE = "DELETE FROM page WHERE id=?";
	public static final String DELETEAGGRPAGE = "DELETE FROM pagelistaggregation WHERE idPageList=?";
}
