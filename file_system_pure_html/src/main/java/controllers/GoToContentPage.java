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

import beans.Document;
import beans.Folder;
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

@WebServlet("/GoToContentPage")
public class GoToContentPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;

    public void destroy() {
    	try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void init() {
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
        this.engine=TemplateSetup.getEngine(context, ".html");
    }
    public GoToContentPage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmp=request.getParameter("folder");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");//potrebbe servire
		//|| isNumeric(tmp)
		if(!Utils.isNumber(tmp)) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Missing or invalid parameters for going to content page");
			return;
		}
		int folderId=Integer.parseInt(tmp);
		FolderDAO folderDao=new FolderDAO(connection);
		List<Document> documents=null;
		List<Folder> folders=null;
		Folder parenteFolder=null;
		try {
		if(!folderDao.isFolderOwnedByUser(folderId, user.getId())) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Selected folder does not exist");
			return;
		}
		DocumentDAO documentDao=new DocumentDAO(connection);
	
		
		folders=folderDao.getFolderByFatherFolderId(folderId);
		documents=documentDao.getDocumentByFatherFolderId(folderId);
		
			parenteFolder=folderDao.findFolderById(folderId);
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error during content page loading");
			return;
		}
		
		List<String> pages=(ArrayList)session.getAttribute("pages");
		String currentPage=request.getServletPath();
		String queryString=request.getQueryString();
		if(queryString!=null) {
			currentPage+="?"+queryString;
		}
		pages.add(currentPage);
		
		request.setAttribute("documents", documents);
		request.setAttribute("folders", folders);
		request.setAttribute("currentFolder", parenteFolder);
		forward(request, response, "/WEB-INF/content.html");
	}
	
	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
