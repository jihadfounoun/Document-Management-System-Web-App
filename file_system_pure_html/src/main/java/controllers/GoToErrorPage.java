package controllers;

import java.io.IOException;
import java.sql.Connection;
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

import utilities.ConnectionSetup;
import utilities.TemplateSetup;

/**
 * Servlet implementation class GoToErrorPage
 */
@WebServlet("/GoToErrorPage")
public class GoToErrorPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine engine;



	public void init() {
		ServletContext context = getServletContext();
		this.engine = TemplateSetup.getEngine(context, ".html");
	}
    public GoToErrorPage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String error=request.getParameter("error");
		HttpSession session = request.getSession(false);
		List<String> pages=(ArrayList)session.getAttribute("pages");
		String currentPage=request.getServletPath();
		String queryString=request.getQueryString();
		if(queryString!=null) {
			currentPage+="?"+queryString;
		}
		pages.add(currentPage);
		if(error!=null)
			request.setAttribute("error",error);
		forward(request,response,"/WEB-INF/error.html");
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

}
