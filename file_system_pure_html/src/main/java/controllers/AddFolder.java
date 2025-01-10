package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

/**
 * Servlet implementation class AddFolder
 */

@WebServlet("/AddFolder")
public class AddFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;


    
    public void init() {//setUp connessione db e engine che "processa" le pagine
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
        this.engine=TemplateSetup.getEngine(context, ".html"); 
    }

    public AddFolder() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	//controllo sull'unicità nome
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name=request.getParameter("name");
		String tmp=request.getParameter("parentFolderId");
		int parentFolderId;
		if(Utils.isNumber(tmp)) {
			parentFolderId=Integer.parseInt(tmp);
		}else
			parentFolderId=0;
		if(Utils.isEmpty(name)) {
			if(parentFolderId==0) {
			response.sendRedirect(getServletContext().getContextPath() + "/GoToContentManagementPage?error_rootItems="+"empty folder name");}
			else {
				request.setAttribute("parentFolderId",parentFolderId);
				request.setAttribute("error_folder", "empty required values creating folder");
				forward(request, response, "/WEB-INF/content-management-forms.html");
			}
				
			return;
		}
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		FolderDAO dao=new FolderDAO(connection);
		DocumentDAO documentDao=new DocumentDAO(connection);
		try {
		if(parentFolderId!=0 && !dao.isFolderOwnedByUser(parentFolderId, currentUser.getId())) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Selected destination folder does not exist");
			return;
		}else if(!dao.isNameValid(name, parentFolderId,currentUser.getId()) || !documentDao.isNameValid(name, parentFolderId))
		{
			if(parentFolderId==0) {
				response.sendRedirect(getServletContext().getContextPath() +"/GoToContentManagementPage?error_rootItems="+"There is already a main folder with this name");
				return;
			}
			request.setAttribute("parentFolderId",parentFolderId);
			request.setAttribute("error_folder", "selected destination folder already contains a folder with the same name.");
			forward(request, response,"/WEB-INF/content-management-forms.html" );
			return;
			
		}
		dao.createFolder(parentFolderId, currentUser.getId(), name);
		
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error creating child folder");
			return;
		}
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
			
		}
    }

}
