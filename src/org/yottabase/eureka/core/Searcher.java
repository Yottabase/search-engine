package org.yottabase.eureka.core;

import java.util.List;

public interface Searcher {

	public SearchResult search(String query, Integer page, Integer itemInPage, Integer lastDocID, Float lastDocScore);

	public List<String> autocomplete(String query);

}
