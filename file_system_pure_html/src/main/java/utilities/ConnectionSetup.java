package utilities;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//classe che gestisce l'inizializzaione della connessione con il database
@WebServlet("/ConnectionSetup")
public class ConnectionSetup {

    //si potrebbe usare web.xml per i parametri
    public static Connection getConnection(ServletContext context) {
    	Connection connection=null;
    	//String driver="com.mysql.cj.jdbc.Driver";
    	//String user="root";//personali
    	//String pwd="brodi";//personali
    	//String url="jdbc:mysql://localhost:3306/document_management_system";//dovrebbe essere lo stesso(forse la porta è diversa)
    	String driver=context.getInitParameter("driverDB");
    	String url=context.getInitParameter("urlDB");
    	String user=context.getInitParameter("userDB");
    	String pwd=context.getInitParameter("pwdDB");
    	
    	try {
			Class.forName(driver);
			connection=DriverManager.getConnection(url,user,pwd);
			//errore nella load driver"
    	}catch (SQLException e) {				
				e.printStackTrace();
		}
		 catch (ClassNotFoundException e) {
			 //connessione con il db non riuscita
			e.printStackTrace();
		}
    	
    	return connection;
    }
    
    public static void endConnection(Connection connection) throws SQLException {
    	if(connection!=null)
    		connection.close();
    }


}
