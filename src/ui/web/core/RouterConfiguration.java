package ui.web.core;

import java.util.ArrayList;
import java.util.List;

public class RouterConfiguration {
	
	public List<Route> getRoutes(){
		
		List<Route> routes = new ArrayList<Route>();
		
		// per aggiungere una nuova rotta copiare la riga e configurare opportunamente
		routes.add(new Route("home", "ui.web.searchengine.HomeAction"));
		routes.add(new Route("esempio", "ui.web.searchengine.EsempioAction"));
		
		return routes;
	}

}
