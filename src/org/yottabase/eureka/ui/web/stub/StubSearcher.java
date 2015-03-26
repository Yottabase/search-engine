package org.yottabase.eureka.ui.web.stub;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.Searcher;
import org.yottabase.eureka.core.WebPageSearchResult;

public class StubSearcher implements Searcher{

	@Override
	public SearchResult search(String query, Integer page, Integer itemInPage,
			Integer lastDocID, Float lastDocScore) {
		
		ArrayList<String> highlights= new ArrayList<String>();
		highlights.add("snippet");
		highlights.add("high2");
		
		ArrayList<String> skippedWords= new ArrayList<String>();
		skippedWords.add("skip");
		skippedWords.add("high2");
		
		List<WebPageSearchResult> webPages = new ArrayList<WebPageSearchResult>();
		webPages.add(new WebPageSearchResult(
			"Christian", 
			"Ciao sono uno snippet", 
			highlights,
			"http://www.google.it", 
			skippedWords,
			new GregorianCalendar(2012,10,1)
		));
		
		webPages.add(new WebPageSearchResult(
			"Leonardo", 
			"Ciao sono un professor snippet ", 
			new ArrayList<String>(),
			"http://www.google.it/leonardo.proff", 
			new ArrayList<String>(),
			new GregorianCalendar(1015,12,4)
		));
		
		webPages.add(new WebPageSearchResult(
			"Alessandro", 
			"Ciao sono uno snippet assistente del proff Leonardo", 
			new ArrayList<String>(),
			"http://www.google.it/leonardo.proff/alessandro", 
			new ArrayList<String>(),
			new GregorianCalendar(2013,2,54)
		));
		
		SearchResult result = new SearchResult();
		result.setPage(page);
		result.setItemsInPage(itemInPage);
		result.setItemsCount(3);
		result.setQueryResponseTime(0.45);
		result.setWebPages(webPages);
		result.setExecutedQuery(query);
		
		result.addSuggestedSearch("Alessandro Magno");
		result.addSuggestedSearch("Giulio Cesare");
		result.addSuggestedSearch("Christian Vadalà");
		
		return result;
	}

	@Override
	public List<String> autocomplete(String query) {
		
		String[] states = {"Alabama", "Alaska", "Arizona", "Arkansas", "California",
        	  "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii",
        	  "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
        	  "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
        	  "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
        	  "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota",
        	  "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
        	  "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
        	  "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
		};
		
		List<String> results = new ArrayList<String>();
		
		for(String state: states){
			if(state.toLowerCase().startsWith(query.toLowerCase())){
				results.add(state);
			}
		}
		
		return results;
	}

	

}
