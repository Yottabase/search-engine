package org.yottabase.eureka.core;

import java.util.List;

public class SearchResult {

	private Integer resultsCount;
	
	private Integer page;
	
	private List<Snippet> snippets;
	
	private List<String> suggestedSearch;

	public Integer getResultsCount() {
		return resultsCount;
	}

	public void setResultsCount(Integer resultsCount) {
		this.resultsCount = resultsCount;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public List<Snippet> getSnippets() {
		return snippets;
	}

	public void setSnippets(List<Snippet> snippets) {
		this.snippets = snippets;
	}

	public List<String> getSuggestedSearch() {
		return suggestedSearch;
	}

	public void setSuggestedSearch(List<String> suggestedSearch) {
		this.suggestedSearch = suggestedSearch;
	}
	
	
	
}
