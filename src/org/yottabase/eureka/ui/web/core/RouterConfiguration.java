package org.yottabase.eureka.ui.web.core;

import java.util.ArrayList;
import java.util.List;

public class RouterConfiguration {
	
	public List<Route> getRoutes(){
		
		List<Route> routes = new ArrayList<Route>();
		
		// per aggiungere una nuova rotta copiare la riga e configurare opportunamente
		routes.add(new Route("search", "org.yottabase.eureka.ui.web.action.SearchAction"));
		
		return routes;
	}

}
