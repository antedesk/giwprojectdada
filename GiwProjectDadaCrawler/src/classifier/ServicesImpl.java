	package classifier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import db.DAOServices;
import db.DBDatasource;

import model.Page;
import model.PageDetails;

public class ServicesImpl implements Services {

	private DBDatasource dbDataSource;
	private DAOServices dao;
	private Connection conn;
	
	public ServicesImpl() throws Exception{
		dbDataSource = new DBDatasource();
		conn = dbDataSource.getConnection();
		dao = new DAOServices(conn);
	}
	@Override
	public String URLToCategory(String url) throws SQLException {
		return dao.getCategoryByURL(url);	
	}
	@Override
	public List<PageDetails> URLToProducts(String url) throws SQLException {
		List<PageDetails> pds = new LinkedList<PageDetails>();
		for (Integer integ : dao.getDetailIDsByURL(url)) {
			pds.add(dao.getPageDetailsFromIdProduct(integ.intValue()));
		}
		return pds;
	}
	@Override
	public int URLToNumReviews(String url) throws SQLException {
		return dao.getPageDetailsFromURL(url).getNumberOfReviews();
	}
	@Override
	public Date URLToLastDateReview(String url) throws SQLException {
		return dao.getPageDetailsFromURL(url).getLastDateReview();
	}
	@Override 
	public List<Page> CategoryToPages(String category) throws SQLException {
		return dao.getPagesFromCategory(category);
	}
	@Override
	public List<PageDetails> DateToPages(Date date) throws SQLException {
		return dao.getPageDetailsFromDate(date,'=');
	}
	@Override
	public List<PageDetails> DateToBackPages(Date date) throws SQLException {
		return dao.getPageDetailsFromDate(date,'<');
	}
	@Override
	public List<PageDetails> DateToFuturePages(Date date) throws SQLException {
		return dao.getPageDetailsFromDate(date,'>');
	}
	@Override
	public List<PageDetails> RangesDateToPages(Date minDate, Date maxDate)
			throws SQLException {
		return dao.getPageDetailsBetweenDates(minDate, maxDate);
	}
	@Override
	public List<String> RangesDateToCategories(Date minDate, Date maxDate)
			throws SQLException {
		return dao.getCategoriesBetweenDates(minDate, maxDate);
	}
	@Override
	public void ClearDatabase() throws SQLException {
		dao.clearAllTables();
	}
}
