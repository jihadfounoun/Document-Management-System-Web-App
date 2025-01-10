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

import utilities.ConnectionSetup;
import utilities.TemplateSetup;

/**
 * Servlet implementation class GoBack
 */
@WebServlet("/GoBack")
public class GoBack extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine engine;

    
    
    public void init() {
    	ServletContext context= getServletContext();
        this.engine=TemplateSetup.getEngine(context, ".html");
    }
    public GoBack() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		List<String> pages=(ArrayList<String>)session.getAttribute("pages");
		System.out.print(pages);
		if(pages==null || pages.size()<2) {
			response.sendRedirect(getServletContext().getContextPath() +"/GoToHome");
			return;
		}
		System.out.print(pages);
		String previusPage=pages.get(pages.size()-2);
		pages.remove(pages.size()-1);
		pages.remove(pages.size()-1);
		//System.out.print(pages);
		response.sendRedirect(getServletContext().getContextPath() +previusPage);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

}
