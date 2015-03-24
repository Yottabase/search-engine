package org.yottabase.eureka.ui.web.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.Searcher;
import org.yottabase.eureka.core.WebPageSearchResult;
import org.yottabase.eureka.ui.web.core.Action;
import org.yottabase.eureka.ui.web.stub.StubSearcher;

public class ApiSearchAction implements Action{

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String q =  request.getParameter("q");
		Integer page = Integer.getInteger(request.getParameter("page"));
		//TODO aggiungere qualche controllo su q?
		//TODO aggiungere qualche controllo su page?
		
		Searcher searcher = new StubSearcher();
		SearchResult result = searcher.search(q, page, 10);
		
		JSONObject json = new JSONObject();
		
		json.put("page", result.getPage());
		json.put("itemInPage", result.getItemInPage());
		json.put("itemsCount", result.getItemsCount());
		json.put("queryResponseTime", result.getQueryResponseTime());
		JSONArray suggestedSearch = new JSONArray(result.getSuggestedSearch().toArray());
		json.put("suggestedSearch", suggestedSearch);
		
		JSONArray items = new JSONArray();
		json.put("webPages", items);
		
		for(WebPageSearchResult webPage : result.getWebPages()){
			JSONObject item = new JSONObject();
			item.put("title", webPage.getTitle());
			item.put("url", webPage.getUrl());
			item.put("snippet", webPage.getSnippet());
			//item.put("skippedWords", new JSONArray());
			item.put("date", webPage.getDate());
			items.put(item);
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}

}
