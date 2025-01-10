package controllers;

import java.io.IOException;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.User;
import dao.UserDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

@WebServlet("/Register")
@MultipartConfig
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	

    public Register() {
        super();

    }
    
    public void init() {
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//ci possiamo ridurre ad email name, password
		String name=request.getParameter("name");
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		String repeatedPassword=request.getParameter("repeated_password");
		String username=request.getParameter("username");
		if(Utils.isEmpty(username)||Utils.isEmpty(name)||Utils.isEmpty(password)|| Utils.isEmpty(email) || Utils.isEmpty(repeatedPassword)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Empty fields detected for login");			
			return;
		}else if(!Utils.isEmailValid(email)){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Insert correct email!");
			return;	
		}else if(!password.equals(repeatedPassword)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Passwords do not match!");
			return;			
		}
		UserDAO dao=new UserDAO(connection);
		User user=null;
		try {
			if(dao.getUserByEmail(email)!=null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Chosen email is already in us!");
				return;
			}else if(dao.getUserByUsername(username)!=null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Chosen username is already in us!");
				return;
			}
			dao.registerUser(name,email,username, password);
			user=dao.getUserByEmail(email);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to register");
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
			e.printStackTrace();
		}
    }

}
