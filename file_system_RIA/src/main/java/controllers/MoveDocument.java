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

import beans.Document;
import beans.Folder;
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

/**
 * Servlet implementation class MoveDocument
 */
@WebServlet("/MoveDocument")
@MultipartConfig 
public class MoveDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;



	public void init() {
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
	}

    public MoveDocument() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmp1=request.getParameter("folderId");
		String tmp2=request.getParameter("document");
		
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		
		if(Utils.isEmpty(tmp2)||Utils.isEmpty(tmp1)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing parameters for moving a document");			
			return;
		}else if(!Utils.isNumber(tmp1)||!Utils.isNumber(tmp1)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameters for moving a document");			
			return;
		}
		int destinationId = Integer.parseInt(tmp1);
		int documentId = Integer.parseInt(tmp2);
		DocumentDAO documentDao=new DocumentDAO(connection);
		FolderDAO folderDao=new FolderDAO(connection);
		Folder folder=null;
		try {
		Document doc=documentDao.getDocumentById(documentId);
		//System.out.print(doc+" "+destinationId+"\n");
		if(doc==null || !folderDao.isFolderOwnedByUser(doc.getFolderId(), user.getId()) || !folderDao.isFolderOwnedByUser(destinationId, user.getId()) ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid parameters for moving a document");			
			return;
		}else if(!folderDao.isNameValid(doc.getName(), destinationId, user.getId()) || !documentDao.isNameValid(doc.getName(), destinationId)) {
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The selected destination folder already contains a resource with the same name.");			
			return;
		}
		folder=folderDao.findFolderById(destinationId);
		documentDao.moveDocument(documentId,destinationId );
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to move document");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
