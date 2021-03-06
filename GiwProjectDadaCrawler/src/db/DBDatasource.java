package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
/*
* @author Antonio Gallo
* @author Daniele D'Andrea
* @author Antonio Tedeschi
* @author Daniele Malta
*/
public class DBDatasource {

	private String dbDriver; // La stringa contenente il nome del driver
	private String dbURL;    // La stringa contenente la URL del DB
	private String username; // UserID di connessione
	private String password; // Password associata alla username

	public DBDatasource() throws Exception{

		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db/config/settings.properties"));

		this.dbDriver = properties.getProperty("database.driver");
		this.dbURL = properties.getProperty("database.url");
		this.username = properties.getProperty("database.username");
		this.password = properties.getProperty("database.password");

	}

	public Connection getConnection() throws Exception {
		Class.forName(dbDriver);
		return DriverManager.getConnection(dbURL, username, password);
	}

	public void closeConnection(Connection conn) throws SQLException{
		try {
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {
			throw e;
		}
	}
}
