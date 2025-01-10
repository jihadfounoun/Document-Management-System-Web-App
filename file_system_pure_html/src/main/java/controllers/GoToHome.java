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

import beans.Folder;
import beans.User;
import dao.FolderDAO;
import utilities.ConnectionSetup;
import utilities.TemplateSetup;

//non completa
@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine engine;

	public GoToHome() {
		super();
	}

	public void init() {
		ServletContext context = getServletContext();
		this.connection = ConnectionSetup.getConnection(context);
		this.engine = TemplateSetup.getEngine(context, ".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("currentUser");
		
		//List<String> pages=new ArrayList<>();
		List<String> pages=(ArrayList)session.getAttribute("pages");
		if(pages!=null) {
			pages.clear();
			pages.add("/GoToHome");
		}else {
			pages=new ArrayList<>();
			pages.add("/GoToHome");
			session.setAttribute("pages", pages);
		}
		List<Folder> folders = null;
		FolderDAO dao = new FolderDAO(this.connection);

		
		try {
			folders = dao.getFolderByUserId(user.getId());
		} catch (SQLException e) {
			response.sendRedirect(getServletContext().getContextPath()+"/GoToErrorPage?error="+"Server error during home page loading");
			return;
		}

		request.setAttribute("folders", folders);
		forward(request, response, "/WEB-INF/home.html");
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
			e.printStackTrace();
		}
	}

}
