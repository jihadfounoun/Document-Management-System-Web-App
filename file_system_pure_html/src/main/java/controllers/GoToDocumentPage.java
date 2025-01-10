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
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;
//riporta alla pagina che mostra un documento specificato
@WebServlet("/GoToDocumentPage")
public class GoToDocumentPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;


	public void init() {
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		this.engine = TemplateSetup.getEngine(context, ".html");
	}
    public GoToDocumentPage() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmp=request.getParameter("documentId");
		DocumentDAO documentDao=new DocumentDAO(connection);
		FolderDAO folderDao=new FolderDAO(connection);
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		Document document=null;
		if(!Utils.isNumber(tmp)) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Missing or invalid parameters for going to document page");
			return;
		}
		int documentId=Integer.parseInt(tmp);
		try {
			
			document = documentDao.getDocumentById(documentId);
			if( document==null || !folderDao.isFolderOwnedByUser(document.getFolderId(), user.getId())) {
				response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Selected document does not exist");
				return;
			}
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error during document page loading");
			return;
		}
		
		List<String> pages=(ArrayList)session.getAttribute("pages");
		String currentPage=request.getServletPath();
		String queryString=request.getQueryString();
		if(queryString!=null) {
			currentPage+="?"+queryString;
		}
		pages.add(currentPage);
		
		request.setAttribute("document", document);
		forward(request,response,"/WEB-INF/document.html");
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
