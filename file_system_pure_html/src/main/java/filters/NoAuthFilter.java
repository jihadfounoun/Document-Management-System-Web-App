package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.User;
import utilities.TemplateSetup;


/**
 * Servlet Filter implementation class NoAuthFilter
 */
@WebFilter("/NoAuthFilter")
public class NoAuthFilter extends HttpFilter implements Filter {

    /**
     * @see HttpFilter#HttpFilter()
     */
    public NoAuthFilter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		HttpSession session=req.getSession(false);
		if(session!=null) {
			User user=(User)session.getAttribute("currentUser");
			if(user!=null) {
				res.sendRedirect(request.getServletContext().getContextPath() + "/GoToHome");
				return;
			}
		}
		chain.doFilter(request, response);
		
	}


}
