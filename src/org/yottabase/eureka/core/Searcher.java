package org.yottabase.eureka.core;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

public interface Searcher {

	public SearchResult search(String query, Integer page, Integer itemInPage)
			throws IOException, ParseException, java.text.ParseException;

	public List<String> autocomplete(String query) throws IOException;

}
