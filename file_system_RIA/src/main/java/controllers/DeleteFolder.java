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

import beans.User;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.Utils;

/**
 * Servlet implementation class DeleteFolder
 */
@WebServlet("/DeleteFolder")
@MultipartConfig 
public class DeleteFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;


    
    public void init() {//setUp connessione db e engine che "processa" le pagine
    	ServletContext context= getServletContext();
        this.connection=ConnectionSetup.getConnection(context);

    }
    public DeleteFolder() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		User user=(User)session.getAttribute("currentUser");
		
		String tmp=request.getParameter("folderId");
		//System.out.print(tmp);
		if(!Utils.isNumber(tmp)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unable to delete folder due to invalid parameters");
			return;
		}
		int folderId=Integer.parseInt(tmp);
		FolderDAO dao= new FolderDAO(connection);
		try {
			if(!dao.isFolderOwnedByUser(folderId, user.getId())) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Unable to delete folder due to invalid parameters");
				return;	
			}
			dao.deleteFolderById( folderId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error: unable to delete folder");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
