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

import beans.Folder;
import beans.User;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;

@WebServlet("/GoToContentManagementPage")
public class GoToContentManagementPage extends HttpServlet {
	private Connection connection;
	private TemplateEngine engine;

    public void init() {
        ServletContext context=getServletContext();
        this.connection = ConnectionSetup.getConnection(context);
        this.engine=TemplateSetup.getEngine(context, ".html");
    }
    public GoToContentManagementPage() {
        super();
        
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		List<Folder> folders = null;
		FolderDAO dao = new FolderDAO(this.connection);
		String error1=request.getParameter("error_rootItems");
		String error2=request.getParameter("error_childItems");
		try {
			folders = dao.getFolderByUserId(user.getId());
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error during content management page loading");
			return;
		}
		List<String> pages=(ArrayList<String>)session.getAttribute("pages");
		String currentPage=request.getServletPath();
		String queryString=request.getQueryString();
		if(queryString!=null) {
			currentPage+="?"+queryString;
		}
		pages.add(currentPage);
		if(error1!=null)
			request.setAttribute("error_rootItems", error1);
		else if(error2!=null) {
			request.setAttribute("error_childItems", error2);
		}
		request.setAttribute("folders", folders);
		forward(request,response,"/WEB-INF/content-management.html");
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		//processa il template
		//path
		//ctx che contiene tutto il contesto
		//e il buffer di uscita
		engine.process(path, ctx, response.getWriter());
		
	}

}
