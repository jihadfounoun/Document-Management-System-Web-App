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

import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

@WebServlet("/AddFolder")
@MultipartConfig 
public class AddFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	


    
    public void init() {//setUp connessione db e engine che "processa" le pagine
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);
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
		String name=request.getParameter("folderName");
		String tmp=request.getParameter("parentFolderId");
		
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");

		if(Utils.isEmpty(name) ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing parameters for creating a folder");			
			return;
		}
		int folderId;
		if(tmp==null) {
			folderId=0;
		}else if(!Utils.isNumber(tmp)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameters for creating a folder");			
			return;
		}else {
		folderId=Integer.parseInt(tmp);
		}
		FolderDAO folderDao=new FolderDAO(connection);
		DocumentDAO documentDao = new DocumentDAO(connection);
		
		try {
		
		if(folderId!=0 && !folderDao.isFolderOwnedByUser(folderId, user.getId())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameters for creating a folder");			
			return;
		}else if(!folderDao.isNameValid(name,folderId,user.getId())||!documentDao.isNameValid(name, folderId)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The folder name you have chosen is already in use within the parent folder");			
			return;
		}
			folderDao.createFolder(folderId, user.getId(), name);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to create folder");
			return;
		}
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
