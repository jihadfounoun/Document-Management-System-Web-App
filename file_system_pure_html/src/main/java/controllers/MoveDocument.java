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
public class MoveDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;



	public void init() {
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		this.engine = TemplateSetup.getEngine(context, ".html");
	}

    public MoveDocument() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmp1=request.getParameter("folderId");
		String tmp2=request.getParameter("document");
		
		if(!Utils.isNumber(tmp2) || !Utils.isNumber(tmp1)) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Missing or invalid parameters trying to move document");
			return;
		}
		
		int destinationId = Integer.parseInt(tmp1);
		int documentId = Integer.parseInt(tmp2);
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");

		try {
		DocumentDAO documentDao=new DocumentDAO(connection);
		FolderDAO folderDao=new FolderDAO(connection);
		Document document=documentDao.getDocumentById(documentId);
		Folder parentFolder=null;
		if(document==null || !folderDao.isFolderOwnedByUser(document.getFolderId(), user.getId())) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Selected document does not exist");
			return;
		}else if(!folderDao.isFolderOwnedByUser(destinationId, user.getId())) {
				response.sendRedirect(getServletContext().getContextPath() +"/GoToMoveDocument?documentId="+documentId
						+"&error="+"selected destination folder does not exists");
				return;
			}else if(document.getFolderId()==destinationId) {
				response.sendRedirect(getServletContext().getContextPath() +"/GoToMoveDocument?documentId="+documentId
						+"&error="+"you can't move the document to the origine folder");
			return;
		}else if(!documentDao.isNameValid(document.getName(), destinationId) || !folderDao.isNameValid(document.getName(), destinationId) ) {
			response.sendRedirect(getServletContext().getContextPath() +"/GoToMoveDocument?documentId="+documentId
					+"&error="+"you can't move the document in the chosen folder because there is already a resource with the same name");
		     return;
		}
		documentDao.moveDocument(documentId,destinationId );
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error trying to move document");
			return;
		}
		response.sendRedirect(getServletContext().getContextPath() +"/GoToContentPage?folder=" + destinationId) ;
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());// processa il template

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
