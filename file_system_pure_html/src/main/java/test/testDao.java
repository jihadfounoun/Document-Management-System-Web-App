package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.Folder;
import beans.User;
import dao.FolderDAO;
import dao.UserDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
//classe usata per testare UserDAO
@WebServlet("/testDao")
public class testDao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;
       
   
    public testDao() {
        super();
        // TODO Auto-generated constructor stub
    }
	public void init() {// setUp connessione db e engine che "processa" le pagine
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		this.engine = TemplateSetup.getEngine(context, ".html");
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*FolderDAO dao=new FolderDAO(connection);
		List<Folder> folders=dao.getFolderByUserId(5);
		request.setAttribute("folders", folders);
		forward(request, response,"NewFile.html");
		System.out.print(folders);*/


	}
	
	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());

	}
	public void destroy() {
		try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
