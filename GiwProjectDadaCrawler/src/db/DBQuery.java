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
	
	public static final String SELECTPAGEDETAILSFROMID = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE pagedetails.id=?";
	public static final String SELECTPAGEDETAILSFROMURL = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE url=?";

	
	
	public static final String SELECTPAGEDETAILSFROMDATE = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE lastDateReviews=?";
	public static final String SELECTPAGEDETAILSFROMAFTERDATE = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE lastDateReviews>?";
	public static final String SELECTPAGEDETAILSFROMBEFOREDATE = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE lastDateReviews<?";
	public static final String SELECTPAGEDETAILSFROMBETWEENDATES = "SELECT pagedetails.id, url, category, productName, numberOfReviews, numberOfReviewsList, lastDateReviews FROM pagedetails join page on page.iddetails=pagedetails.id WHERE lastDateReviews>=? AND lastDateReviews<=?";
	
	public static final String SELECTPAGESFROMCATEGORY = "SELECT id,url,category FROM `GiwDB`.`page` WHERE category=?";
	public static final String SELECTCATEGORYBETWEENDATES = "SELECT category FROM pagedetails join page on page.iddetails=pagedetails.id WHERE lastDateReviews>=? AND lastDateReviews<=?";
	
	
	public static final String URLTOCATEGORY = "SELECT category FROM page WHERE url=?";
	public static final String URLTTOPRODUCTSID = "SELECT DISTINCT idpage FROM pagelistaggregation join page on pagelistaggregation.idpagelist=page.id WHERE page.url=?";
}
