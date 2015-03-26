package org.yottabase.eureka.searcher;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.WebPageSearchResult;

public class TestSearcher {

	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
		/*
		 * richiamo la classe SearchIndex per testare il funzionamento
		 * successivamente sarà sostituita dalla chiamata del controller
		 */

		IndexSearch searcher = new IndexSearch();
		SearchResult result = new SearchResult();

		result = searcher.search("the", 1, 20);
		
		System.out.println(result.getItemsCount() + " risultati\n");
		System.out.println("Suggested searches: " + result.getSuggestedSearch().toString() + "\n");
		for (WebPageSearchResult page : result.getWebPages())
			System.out.println( page.toString() + "\n" );
		
		/**
		System.out.println("-----------------------------------");
		
		searcher = new IndexSearch();
		result = new SearchResult();
		searcher.lastScore = new ScoreDoc(9277, 0.38888752f);
		result = searcher.search("the", 2, 10);
		
		System.out.println(result.getItemsCount() + " risultati\n");
		System.out.println("Suggested searches: " + result.getSuggestedSearch().toString() + "\n");
		for (WebPageSearchResult page : result.getWebPages())
			System.out.println( page.toString() + "\n" );
		**/
	}
}
