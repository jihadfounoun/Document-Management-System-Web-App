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

/**
 * Servlet implementation class GoToMoveDocument
 */
@WebServlet("/GoToMoveDocument")
public class GoToMoveDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String List = null;
	private Connection connection;
	private TemplateEngine engine;



	public void init() {
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		this.engine = TemplateSetup.getEngine(context, ".html");
	}
    public GoToMoveDocument() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//riceve solo il document di origine
		String tmp=request.getParameter("documentId");
		String error=request.getParameter("error");
		
		if(!Utils.isNumber(tmp)) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Missing or invalid parameters for going to move document page");
			return;
		}
		int documentId=Integer.parseInt(tmp);
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		
		DocumentDAO documentDao=new DocumentDAO(connection);
		FolderDAO folderDao=new FolderDAO(connection);
		Document document=null;
		Folder folder=null;
		List<Folder> folders=new ArrayList<>();
		try {
		document=documentDao.getDocumentById(documentId);
		if(document==null || !folderDao.isFolderOwnedByUser(document.getFolderId(), user.getId())) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Selected document does not exist");
			return;
		}
		folders=folderDao.getFolderByUserId(user.getId());
		folder = folderDao.findFolderById(document.getFolderId());
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error during move document page loading");
			return;
		}
		
		List<String> pages=(ArrayList)session.getAttribute("pages");
		String currentPage=request.getServletPath();
		String queryString=request.getQueryString();
		if(queryString!=null) {
			currentPage+="?"+queryString;
		}
		pages.add(currentPage);
		if(error!=null) {
			request.setAttribute("error", error);
		}
		request.setAttribute("folders", folders);
		request.setAttribute("parentFolder", document.getFolderId());
		request.setAttribute("document", document.getId());
		request.setAttribute("move_mode", true);
		request.setAttribute("move_document_mode", "Moving the "+document.getName()+" document from "+folder.getName()+" folder");
		forward(request, response, "/WEB-INF/home.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
