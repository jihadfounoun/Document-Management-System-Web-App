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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import beans.Document;
import beans.Folder;
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.Utils;

/**
 * Servlet implementation class DeleteDocument
 */
@WebServlet("/DeleteDocument")
@MultipartConfig 
public class DeleteDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;


    
    public void init() {//setUp connessione db e engine che "processa" le pagine
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		User user=(User)session.getAttribute("currentUser");
		String tmp=request.getParameter("documentId");
		//System.out.print(tmp);
		if(!Utils.isNumber(tmp)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unable to delete folder due to invalid parameters");
			return;
		}
		int documentId=Integer.parseInt(tmp);
		DocumentDAO documentDao= new DocumentDAO(connection);
		FolderDAO folderDao=new FolderDAO(connection);
		Folder folder=null;
		try {
			Document doc=documentDao.getDocumentById(documentId);
			//Folder folder=folderDao.findFolderById(doc.getId());
			if(doc==null || !folderDao.isFolderOwnedByUser(doc.getFolderId(),user.getId())) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Unable to delete document due to invalid parameters");
				return;
			}
			folder=folderDao.findFolderById(doc.getFolderId());
			documentDao.deleteDocument( documentId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to delete document");
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

}
