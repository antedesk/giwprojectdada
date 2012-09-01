package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.PageDetails;
import model.PageList;

import java.sql.Connection;
import java.sql.Statement;

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

	public void insertPageListAggregation(PageDetails pageDetails, int id_page) throws SQLException{
		int id_page_details = this.insertPageDetails(pageDetails, true);

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

	public int insertPageDetails(PageDetails pageDetails, boolean fromAggregationPageList) throws SQLException{
		PreparedStatement statement = null, statement2 = null;
		try {
			statement = connection.prepareStatement(DBQuery.INSERTPAGEDETAILS, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, pageDetails.getProductName());
			if(fromAggregationPageList){
				statement.setNull(2, java.sql.Types.INTEGER);
				statement.setInt(3, pageDetails.getNumberOfReviews());
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
				if(rs.getDate(5)!=null)
					date=new java.util.Date(rs.getDate(5).getTime());
				pd=new PageDetails(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),date);

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


		//PageDetails pd = new PageDetails(url, category, productName, numberOfReviews, lastDateReview);
	}
}
