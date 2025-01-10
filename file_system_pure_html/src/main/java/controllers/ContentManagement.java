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
import utilities.Utils;

/**
 * Servlet implementation class ContentManagement
 */
@WebServlet("/ContentManagement")
public class ContentManagement extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;

    public void destroy() {
    	try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void init() {
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
        this.engine=TemplateSetup.getEngine(context, ".html");
    }
    public ContentManagement() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String tmp=request.getParameter("parentFolderId");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		FolderDAO dao=new FolderDAO(connection);
		int parentFolderId=-1;
		try {
		
		if(!Utils.isNumber(tmp)) {
			
			response.sendRedirect(getServletContext().getContextPath() +"/GoToContentManagementPage?error_childItems="+"please select correct destination folder");
			return;
		}
		parentFolderId=Integer.parseInt(tmp);
		if(!dao.isFolderOwnedByUser(parentFolderId,user.getId() )) {
			response.sendRedirect(getServletContext().getContextPath() +"/GoToContentManagementPage?error_childItems="+"please select correct destination folder");
			return;
		}
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
		request.setAttribute("parentFolderId",parentFolderId );
		forward(request,response,"/WEB-INF/content-management-forms.html");
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());
	}

}
