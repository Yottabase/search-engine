package org.yottabase.eureka.ui.web.stub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.Searcher;
import org.yottabase.eureka.core.WebPageSearchResult;

public class StubSearcher implements Searcher{

	@Override
	public SearchResult search(String query, Integer page, Integer itemInPage) {
		
		List<WebPageSearchResult> webPages = new ArrayList<WebPageSearchResult>();
		webPages.add(new WebPageSearchResult(
			"Christian", 
			"Ciao sono uno snippet", 
			"http://www.google.it", 
			new ArrayList<String>(),
			new Date()
		));
		
		webPages.add(new WebPageSearchResult(
			"Leonardo", 
			"Ciao sono un professor snippet ", 
			"http://www.google.it/leonardo.proff", 
			new ArrayList<String>(),
			new Date()
		));
		
		webPages.add(new WebPageSearchResult(
			"Alessandro", 
			"Ciao sono uno snippet assistente del proff Leonardo", 
			"http://www.google.it/leonardo.proff/alessandro", 
			new ArrayList<String>(),
			new Date()
		));
		
		SearchResult result = new SearchResult();
		result.setPage(page);
		result.setItemInPage(itemInPage);
		result.setItemsCount(3);
		result.setQueryResponseTime(0.45);
		result.setWebPages(webPages);
		return result;
	}

	@Override
	public List<String> autocomplete(String query) {
		// TODO Auto-generated method stub
		return null;
	}

}
