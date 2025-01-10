package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import beans.Document;
import beans.Folder;
import beans.FoldersAndDocuments;
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.Utils;

/**
 * Servlet implementation class GetItems
 */
@WebServlet("/GetItems")
@MultipartConfig 
public class GetItems extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init() {
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
	}
   
    public GetItems() {
        super();
       
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		User user=(User)session.getAttribute("currentUser");
		
		String tmp=request.getParameter("folderId");
		
		int folderId=0;
		//System.out.print(tmp);
		if(Utils.isNumber(tmp)) {
			folderId=Integer.parseInt(tmp);
		}
		
		List<Folder> folders = null;
		List<Document> documents = null;
		FoldersAndDocuments items=null;
		FolderDAO folderDao = new FolderDAO(this.connection);
		DocumentDAO documentDao = new DocumentDAO(this.connection);
		try {
			if(folderId==0) {
			folders = folderDao.getFolderByUserId(user.getId());
			}else if(folderId>0 && folderDao.isFolderOwnedByUser(folderId, user.getId())) {
				folders = folderDao.getFolderByFatherFolderId(folderId);
				documents=documentDao.getDocumentByFatherFolderId(folderId);
			}else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Error : selected folder does not exists");			
				return;
			}
			items=new FoldersAndDocuments(folders,documents);
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to load home page");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);	
		Gson gson=new GsonBuilder().create();
		String itemsJson=gson.toJson(items);
		//if(folderId!=0)
		//System.out.print("\n"+itemsJson+"\n");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(itemsJson);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	public void destroy() {
		try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
