package org.yottabase.eureka.searcher;

import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.WebPageSearchResult;

public class TestSearcher {

	public static void main(String[] args) {
		/*
		 * richiamo la classe SearchIndex per testare il funzionamento
		 * successivamente sarà sostituita dalla chiamata del controller
		 */
		Integer a; Float b;

		IndexSearch searcher = new IndexSearch();
		SearchResult result = new SearchResult();

		result = searcher.search("the", 1, 10, null, null);
		a = result.getDocID();
		b = result.getDocScore();
		
		System.out.println(result.getItemsCount() + " risultati\n");
		System.out.println("Suggested searches: " + result.getSuggestedSearches().toString() + "\n");
		for (WebPageSearchResult page : result.getWebPages())
			System.out.println( page.toString() + "\n" );
		

		System.out.println("-----------------------------------");
		
		searcher = new IndexSearch();
		result = new SearchResult();
		result = searcher.search("the", 2, 10, a, b);
		
		System.out.println(result.getItemsCount() + " risultati\n");
		System.out.println("Suggested searches: " + result.getSuggestedSearches().toString() + "\n");
		for (WebPageSearchResult page : result.getWebPages())
			System.out.println( page.toString() + "\n" );

	}
}
