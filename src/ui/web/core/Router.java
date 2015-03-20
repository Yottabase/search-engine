package ui.web.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ui.web.searchengine.HomeAction;


public class Router extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private Map<String, Route> routes = new HashMap<String, Route>();
	
	@Override
	public void init() throws ServletException {
		super.init();
		RouterConfiguration routerConfiguration = new RouterConfiguration();
		
		for(Route route: routerConfiguration.getRoutes()){
			this.routes.put(route.route, route);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		Route route = this.matchRoute(request);
		
		request.setAttribute("routeInfo", route);
		
		
		System.out.println(route);
		
	
		String actionName = "ui.web.searchengine.HomeAction";
		System.out.println(actionName);
		
		Action action = null;
		
		//TODO non funziona
		//action =  (Action) Class.forName(actionName).newInstance();
		
		if(actionName == "ui.web.searchengine.HomeAction"){
			action = new HomeAction();
		}
		
        action.run(request, response);
        
        
	}
	
	protected Route matchRoute(HttpServletRequest request){
		
		String servletPath = request.getServletPath();
		String routeStr = servletPath.substring(1, servletPath.length() - ".do".length() );
		
		Route route = this.routes.get(routeStr);
		
		if(route == null){
			throw new RuntimeException("Rotta non trovata");
		}
		
		return route;
	}
	
	
	
	

}
