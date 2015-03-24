package org.yottabase.eureka.ui.web.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yottabase.eureka.core.Searcher;
import org.yottabase.eureka.ui.web.core.Action;
import org.yottabase.eureka.ui.web.stub.StubSearcher;

public class ApiAutocompleteAction implements Action {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Searcher searcher = new StubSearcher();
		List<String> suggestions = searcher.autocomplete("hello query");
		JSONArray json = new JSONArray();
		
		for(String suggestion: suggestions){
			JSONObject item = new JSONObject();
			item.put("value", suggestion);
			json.put(item);
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}

}
