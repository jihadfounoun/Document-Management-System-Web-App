package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.User;
import dao.UserDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;

    public Register() {
        super();

    }
    
    public void init() {
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
        this.engine=TemplateSetup.getEngine(context, ".html");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//ci possiamo ridurre ad email name, password
		String name=request.getParameter("name");
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		String repeteadPassword=request.getParameter("repeated_password");
		String username=request.getParameter("username");
		
		User user=null;
		if(email == null || password == null || name==null ||username==null || email.trim().length()==0 || password.trim().length()==0|| name.trim().length()==0
				|| username.trim().length()==0) {
			request.setAttribute("login_mode", false);
			request.setAttribute("error", "Data non valid!");
			forward(request, response,"login.html" );
			return;
		}else if(!Utils.isEmailValid(email)){
			request.setAttribute("login_mode", false);
			request.setAttribute("error", "Insert valid email!");
			forward(request, response,"login.html" );
			return;
		}else if(!password.equals(repeteadPassword)) {
			request.setAttribute("login_mode", false);
			request.setAttribute("error", "Passwords do not match!");
			forward(request, response,"login.html" );
			return;			
		}
		UserDAO dao=new UserDAO(connection);

		try {
			user=dao.getUserByEmail(email);
			if(dao.getUserByEmail(email)!=null) {
				request.setAttribute("login_mode", false);
				request.setAttribute("error", "Chosen email already exists!");
				forward(request, response,"login.html" );
				return;
			}else if(dao.getUserByUsername(username)!=null) {
				request.setAttribute("login_mode", false);
				request.setAttribute("error", "Chosen username already exists!");
				forward(request, response,"login.html" );
				return;
			}
			dao.registerUser(name,email,username, password);
			user=dao.getUserByEmail(email);
		} catch (SQLException e) {
			request.setAttribute("error", "error during registration");
			forward(request, response,"/WEB-INF/error.html" );
			return;
		}
		

		HttpSession session = request.getSession();
		request.setAttribute("name", name);
		session.setAttribute("currentUser", user);
		
		List<String> pages=new ArrayList<>();
		session.setAttribute("pages", pages);
		
		response.sendRedirect(getServletContext().getContextPath() +"/GoToHome");		
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());
		
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
