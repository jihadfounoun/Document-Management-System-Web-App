package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import beans.Folder;
import beans.User;
import dao.DocumentDAO;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;
import utilities.Utils;

//servlet che aggiunge un documento
@WebServlet("/AddDocument")
public class AddDocument extends HttpServlet {


	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;

	public AddDocument() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() {// setUp connessione db e engine che "processa" le pagine
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		this.engine = TemplateSetup.getEngine(context, ".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	//controllo sull'unicità nome
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		String type = request.getParameter("type");
		String tmp = request.getParameter("parentFolderId");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		FolderDAO dao=new FolderDAO(connection);
		int parentFolderId=-1;
		try {
			if(!Utils.isNumber(tmp)) {
				response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Missing or invalid parameters creating document");
				return;
			}
			parentFolderId=Integer.parseInt(tmp);
		    List<Folder> folders=dao.getFolderByUserId(user.getId());
		
		if (Utils.isEmpty(name) ||Utils.isEmpty(description) || Utils.isEmpty(type)) {// required per il campo name
			request.setAttribute("parentFolderId",parentFolderId);
			request.setAttribute("error_document", "empty required values creating document");
			forward(request, response, "/WEB-INF/content-management-forms.html");
			return;
		}

		
		if (!dao.isFolderOwnedByUser(parentFolderId, user.getId())) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Selected destination folder does not exist");
			return;
		}
		DocumentDAO documentDao = new DocumentDAO(connection);
		
		if(!documentDao.isNameValid(name, parentFolderId) || !dao.isNameValid(name, parentFolderId,user.getId()))
		{
			request.setAttribute("parentFolderId",parentFolderId);
			request.setAttribute("error_document", "selected destination folder already contains a resource with the same name.");
			forward(request, response,"/WEB-INF/content-management-forms.html" );
			return;
		}
		
			documentDao.createDocument(parentFolderId, name, description, type);
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error creating document");
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/GoToContentPage?folder="+parentFolderId);
		
	}

	private void forward(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		engine.process(path, ctx, response.getWriter());

	}
	public void destroy() {
		try {
			ConnectionSetup.endConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
