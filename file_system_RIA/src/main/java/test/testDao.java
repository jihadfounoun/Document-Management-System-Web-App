package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.User;
import dao.UserDAO;
import utilities.ConnectionSetup;
//classe usata per testare UserDAO
@WebServlet("/testDao")
public class testDao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
   
    public testDao() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init() {
    	ServletContext context=getServletContext();
    	this.connection=ConnectionSetup.getConnection(context);
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		PrintWriter out=response.getWriter();
		response.setContentType("text");
		System.out.println(email+" "+password);
		if(email==null || password==null || email.equals("") || password.equals("")) {
			out.print("valori non validi");
			return;
		}
		UserDAO dao=new UserDAO(connection);
		User user=null;
		//user=dao.findUser(email, password);
		if(user!=null)
			out.print(user.toString());
		else
			out.print("non esiste");
	}

}
