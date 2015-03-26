package org.yottabase.eureka.ui.web.action;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.Searcher;
import org.yottabase.eureka.core.WebPageSearchResult;
import org.yottabase.eureka.ui.web.core.Action;

public class ApiSearchAction implements Action{

	final int itemInPage = 20;
	
	public Searcher getSearcher(){
		Searcher searcher;
		
		 searcher = new org.yottabase.eureka.ui.web.stub.StubSearcher();
		 //searcher = new org.yottabase.eureka.searcher.IndexSearch();
		
		return searcher;
	}
	
	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		
		//TODO aggiungere qualche controllo sugli input q, page, lastDocId, lastDocScore?
		String q =  request.getParameter("q");
		Integer page = Integer.getInteger(request.getParameter("page"));
		Integer lastDocId = null; //TODO va implementato
		Float lastDocScore = null; //TODO va implementato
		
		
		Searcher searcher = this.getSearcher();
		SearchResult result = searcher.search(q, page, this.itemInPage, lastDocId, lastDocScore);
		
		
		JSONObject json = new JSONObject();
		json.put("itemsCount", result.getItemsCount());
		json.put("page", result.getPage());
		json.put("itemsInPage", result.getItemsInPage());
		JSONArray suggestedSearch = new JSONArray(result.getSuggestedSearches().toArray());
		json.put("suggestedSearch", suggestedSearch);
		json.put("queryResponseTime", result.getQueryResponseTime());
		json.put("docId", result.getDocID());
		json.put("docScore", result.getDocScore());
		
		
		JSONArray items = new JSONArray();
		json.put("webPages", items);
		for(WebPageSearchResult webPage : result.getWebPages()){
			JSONObject item = new JSONObject();
			item.put("title", webPage.getTitle());
			item.put("snippet", webPage.getSnippet());
			item.put("url", webPage.getUrl());
			item.put("date", df.format(webPage.getDate().getTime()));
			
			JSONArray highlights = new JSONArray(webPage.getHighlights().toArray());
			item.put("highlights", highlights);
			JSONArray skippedWords = new JSONArray(webPage.getSkippedWords().toArray());
			item.put("skippedWords", skippedWords);
			
			items.put(item);
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}

}
