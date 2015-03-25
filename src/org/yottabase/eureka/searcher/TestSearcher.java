package org.yottabase.eureka.searcher;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.WebPageSearchResult;

public class TestSearcher {

	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
		/*
		 * richiamo la classe SearchIndex per testare il funzionamento
		 * successivamente sarà sostituita dalla chiamata del controller
		 */

		IndexSearch searcher = new IndexSearch();
		SearchResult view = new SearchResult();

		view = searcher.search("the", 1, 1);
		for (WebPageSearchResult page : view.getWebPages())
			System.out.println( page.toString() + "\n" );
	}
}
