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
import com.google.gson.JsonObject;

import beans.Folder;
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

//servlet che aggiunge un documento
@WebServlet("/AddDocument")
@MultipartConfig
public class AddDocument extends HttpServlet {


	private static final long serialVersionUID = 1L;
	private Connection connection;
	

	public AddDocument() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() {// setUp connessione db e engine che "processa" le pagine
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		
		String name = request.getParameter("documentName");
		String description = request.getParameter("documentDescription");
		String type = request.getParameter("documentType");
		String parentFolderId = request.getParameter("parentFolderId");
		
		//System.out.print(name+" "+type+" "+description+" "+parentFolderId);
		
		if(Utils.isEmpty(name) || Utils.isEmpty(description)|| Utils.isEmpty(type) || Utils.isEmpty(parentFolderId)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing parameters for creating a document");			
			return;
		}else if(!Utils.isNumber(parentFolderId)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameters for creating a document");			
			return;
		}
		int folderId=Integer.parseInt(parentFolderId);
		FolderDAO folderDao=new FolderDAO(connection);
		DocumentDAO documentDao = new DocumentDAO(connection);
		Folder folder=null;
		
		try {
		
		if(!folderDao.isFolderOwnedByUser(folderId, user.getId())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameters for creating a document");			
			return;
		}else if(!folderDao.isNameValid(name,folderId,user.getId())||!documentDao.isNameValid(name, folderId)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The document name you have chosen is already in use within the parent folder");			
			return;
		}   
		    folder=folderDao.findFolderById(folderId);
			documentDao.createDocument(folderId, name, description, type);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to create document");
			return;
		}
		
		
		Gson gson = new GsonBuilder().create();
		
		JsonObject json=new JsonObject();
		json.addProperty("name", folder.getName());
		json.addProperty("id", folder.getId());
		json.addProperty("date", (folder.getDate()).toString());
		String jsonString=gson.toJson(json);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonString);
		
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
