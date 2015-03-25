package org.yottabase.eureka.searcher;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.yottabase.eureka.core.SearchResult;

public class TestSearcher {

	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
		/*
		 * richiamo la classe SearchIndex per testare il funzionamento
		 * successivamente sarà sostituita dalla chiamata del controller
		 */

		SearchIndexFile search = new SearchIndexFile();
		SearchResult view = new SearchResult();

		view = search.search("ale", 1, 1);
		System.out.println("title:" + view.getWebPages().get(0).getTitle()
				+ "   	url:" + view.getWebPages().get(0).getUrl()
				+ "     content:" + view.getWebPages().get(0).getSnippet()
				+ "  	item:   " + view.getItemsCount() + "  tempo:  "
				+ view.getQueryResponseTime() + "    suggerimenti: "
				+ view.getSuggestedSearch());

	}
}
