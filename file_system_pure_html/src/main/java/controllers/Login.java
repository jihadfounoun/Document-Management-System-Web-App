package controllers;

import java.io.IOException;
import java.io.PrintWriter;
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



@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;
    public Login() {
        super();
    }

    
    public void init() {//setUp connessione db e engine che "processa" le pagine
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
        this.engine=TemplateSetup.getEngine(context, ".html"); 
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username=request.getParameter("username");
		String password=request.getParameter("password");

		if(username == null || password == null|| username.trim().length()==0 || password.trim().length()==0 ) {
			request.setAttribute("error", "Data non valid");
			request.setAttribute("login_mode", true);
			forward(request, response,"login.html" );
			return;
		}
		//System.out.print(getServletContext().getContextPath()+"\n");
			
		UserDAO userDAO = new UserDAO(connection);//creo il dao
		User user = null;
		

		try {
			user = userDAO.findUser(username, password);
		} catch (SQLException e) {
			request.setAttribute("error", "internal error");
			forward(request, response,"/WEB-INF/error.html" );
			return;
		}

		
		if(user == null) {
			request.setAttribute("error", "username or password incorrect!");
			request.setAttribute("login_mode", true);
			forward(request, response,"login.html" );
			return;

		}

		HttpSession session = request.getSession();
		session.setAttribute("currentUser", user);
		
		List<String> pages=new ArrayList<>();
		pages.add("/GoToHome");
		session.setAttribute("pages", pages);
		
		response.sendRedirect(getServletContext().getContextPath() +"/GoToHome");
		
		
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());
		
	}
	//funzione per chiudere la connessione db a fine esecuzione
    public void destroy() {
    	try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
