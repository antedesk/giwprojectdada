package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.PageDetails;
import model.PageList;

import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DAOServices {

	private Connection connection;

	public DAOServices(Connection conn){
		this.connection = conn;
	}

	public void insertPage(PageList pageList) throws SQLException{
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(DBQuery.INSERTPAGE, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, pageList.getUrl());
			statement.setString(2, pageList.getCategory());
			statement.setNull(3, java.sql.Types.INTEGER);

			statement.executeUpdate();

			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int auto_id = rs.getInt(1);

			if(pageList.getProducts()!=null && pageList.getProducts().size()>0){
				for(PageDetails pageDetails : pageList.getProducts())
					this.insertPageListAggregation(pageDetails, auto_id);
			}

			System.out.println("PAGELIST INSERTED! THE AUTO-ID IS: "+auto_id);

		} catch (SQLException e) {
			throw e;
		} finally{

			try {
				if(statement!=null)
					statement.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	private void insertPageListAggregation(PageDetails pageDetails, int id_page) throws SQLException{
		int id_page_details = this.saveOrUpdatePageDetails(pageDetails, true);

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(DBQuery.INSERTPAGELISTAGGREGATION);

			statement.setInt(1, id_page);
			statement.setInt(2, id_page_details);

			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally{

			try {
				if(statement!=null)
					statement.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public int saveOrUpdatePageDetails(PageDetails pageDetails, boolean fromAggregationPageList) throws SQLException{
		int id_page_details;
		PageDetails dbPageDetails = this.getPageDetailsFromProductName(pageDetails.getProductName());
		if(dbPageDetails==null)
			id_page_details = this.insertPageDetails(pageDetails, fromAggregationPageList);
		else{
			id_page_details = dbPageDetails.getId();
			this.updatePageDetails(pageDetails, dbPageDetails, fromAggregationPageList);
		}
		return id_page_details;
	}

	private void updatePageDetails(PageDetails pageDetails, PageDetails dbPageDetails, boolean fromAggregationPageList) throws SQLException {
		PreparedStatement statement = null, statement2 = null;
		try {
			statement = connection.prepareStatement(DBQuery.UPDATEPAGEDETAILS);

			if(fromAggregationPageList){
				statement.setInt(1, dbPageDetails.getNumberOfReviews());
				statement.setInt(2, pageDetails.getNumberOfReviewsList());
			} else {
				statement.setInt(1, pageDetails.getNumberOfReviews());
				statement.setInt(2, dbPageDetails.getNumberOfReviewsList());
			}
			if(pageDetails.getLastDateReview()!=null)
				statement.setDate(3, new java.sql.Date(pageDetails.getLastDateReview().getTime()));
			else
				statement.setNull(3, java.sql.Types.DATE);
			
			statement.setInt(4, dbPageDetails.getId());
			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(statement!=null)
					statement.close();
				if(statement2!=null)
					statement2.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	private int insertPageDetails(PageDetails pageDetails, boolean fromAggregationPageList) throws SQLException{
		PreparedStatement statement = null, statement2 = null;
		try {
			statement = connection.prepareStatement(DBQuery.INSERTPAGEDETAILS, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, pageDetails.getProductName());
			if(fromAggregationPageList){
				statement.setNull(2, java.sql.Types.INTEGER);
				statement.setInt(3, pageDetails.getNumberOfReviewsList());
			} else {
				statement.setInt(2, pageDetails.getNumberOfReviews());
				statement.setNull(3, java.sql.Types.INTEGER);
			}
			if(pageDetails.getLastDateReview()!=null)
				statement.setDate(4, new java.sql.Date(pageDetails.getLastDateReview().getTime()));
			else
				statement.setNull(4, java.sql.Types.DATE);

			statement.executeUpdate();

			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int id_page_details = rs.getInt(1);

			statement2 = connection.prepareStatement(DBQuery.INSERTPAGE, Statement.RETURN_GENERATED_KEYS);

			statement2.setString(1, pageDetails.getUrl());
			statement2.setString(2, pageDetails.getCategory());
			statement2.setInt(3, id_page_details);

			statement2.executeUpdate();

			System.out.println("PAGELIST INSERTED! THE AUTO-ID IS: "+id_page_details);

			return id_page_details;

		} catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(statement!=null)
					statement.close();
				if(statement2!=null)
					statement2.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}
	
	public PageDetails getPageDetailsFromProductName(String productName) throws SQLException{
		PreparedStatement ps=connection.prepareStatement(DBQuery.SELECTPAGEDETAILSFROMPRODUCTNAME);
		PageDetails pd=null;
		try{

			ps.setString(1, productName);
			ResultSet rs = ps.executeQuery();
			java.util.Date date=null;
			if(rs.next()){
				java.sql.Date data = rs.getDate(7);
				if(!rs.wasNull())
					date=new java.util.Date(data.getTime());
				pd=new PageDetails(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), date);
			}
		}
		catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(ps!=null)
					ps.close();
			} catch (SQLException e) {
				throw e;
			}
		}
		return pd;
	}
	
	public PageDetails getPageDetailsFromIdProduct(int id) throws SQLException{
		PreparedStatement ps=connection.prepareStatement(DBQuery.SELECTPAGEDETAILSFROMID);
		PageDetails pd=null;
		try{

			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			java.util.Date date=null;
			if(rs.next()){
				java.sql.Date data = rs.getDate(7);
				if(!rs.wasNull())
					date=new java.util.Date(data.getTime());
				pd=new PageDetails(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), date);
			}
		}
		catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(ps!=null)
					ps.close();
			} catch (SQLException e) {
				throw e;
			}
		}
		return pd;
	}
	public PageDetails getPageDetailsFromURL(String url) throws SQLException{
		PreparedStatement ps=connection.prepareStatement(DBQuery.SELECTPAGEDETAILSFROMURL);
		PageDetails pd=null;
		try{

			ps.setString(1, url);
			ResultSet rs = ps.executeQuery();
			java.util.Date date=null;
			if(rs.next()){
				java.sql.Date data = rs.getDate(7);
				if(!rs.wasNull())
					date=new java.util.Date(data.getTime());
				pd=new PageDetails(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), date);
			}
		}
		catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(ps!=null)
					ps.close();
			} catch (SQLException e) {
				throw e;
			}
		}
		return pd;
	}
	
	public void saveOrUpdatePageList(PageList pl) throws SQLException{
		
		PreparedStatement ps=connection.prepareStatement(DBQuery.SELECTPAGELIST);
		//PreparedStatement psDeletePage=connection.prepareStatement(DBQuery.DELETEPAGE);
		PreparedStatement psDeleteAggrPage=connection.prepareStatement(DBQuery.DELETEAGGRPAGE);
		try{
			ps.setString(1, pl.getUrl());
			ResultSet rs = ps.executeQuery();
			if(rs.next())
			{
				int todel = rs.getInt(1);
				psDeleteAggrPage.setInt(1, todel);
				psDeleteAggrPage.execute();
				//psDeletePage.setInt(1, todel);
				//psDeletePage.execute();
			}
			insertPage(pl);
		}catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(ps!=null)
					ps.close();
				if(psDeleteAggrPage!=null)
					psDeleteAggrPage.close();
				//if(psDeletePage!=null)
				//	psDeletePage.close();
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	public String getCategoryByURL(String url) throws SQLException{
		PreparedStatement ps=connection.prepareStatement(DBQuery.URLTOCATEGORY);
		PageDetails pd=null;
		String cat = null;
		try{

			ps.setString(1, url);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				java.sql.Date data = rs.getDate(7);
				if(!rs.wasNull())
					cat = rs.getString(1);
			}
		}
		catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(ps!=null)
					ps.close();
			} catch (SQLException e) {
				throw e;
			}
		}
		return cat;
	}
	
	//dato un url di una pageList torna gli id delle pagine di dettaglio contenute
	public List<Integer> getDeteilIDsByURL(String url) throws SQLException{
		PreparedStatement ps=connection.prepareStatement(DBQuery.URLTTOPRODUCTSID);
		List<Integer> ids = new LinkedList<Integer>();
		try{

			ps.setString(1, url);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				if(!rs.wasNull())
					ids.add( rs.getInt(1));
			}
		}
		catch (SQLException e) {
			throw e;
		} finally{
			try {
				if(ps!=null)
					ps.close();
			} catch (SQLException e) {
				throw e;
			}
		}
		return ids;
	}
}
