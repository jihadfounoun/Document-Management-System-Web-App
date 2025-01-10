package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.User;
import dao.UserDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;



@WebServlet("/Login")
@MultipartConfig 
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public Login() {
        super();
    }

    
    public void init() {//setUp connessione db e engine che "processa" le pagine
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);

    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String 	username = request.getParameter("username");
		String password = request.getParameter("password");
		
			
		

		if(Utils.isEmpty(username)|| Utils.isEmpty(password)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Empty fields detected for login");			
			return;
		}
		
		
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		
		try {
			user = userDAO.findUser(username, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to log in");
			return;
		}

		
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Username or password incorrect!");			
			return;
		}
		HttpSession session = request.getSession();
		session.setAttribute("currentUser", user);
		
		
		Gson gson=new GsonBuilder().create();
		String userJson=gson.toJson(user);
		//System.out.print(userJson);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(userJson);
		response.setStatus(HttpServletResponse.SC_OK);
		
	}

    public void destroy() {
    	try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
