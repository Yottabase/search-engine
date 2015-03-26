package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.Searcher;
import org.yottabase.eureka.core.WebPage;
import org.yottabase.eureka.core.WebPageSearchResult;

public class IndexSearch implements Searcher {
	private String indexPath;
	private Directory indexDir;
	private DirectoryReader reader;
	private StandardAnalyzer analyzer;
	private IndexSearcher searcher;
	private SearchResult searchResult;

	public IndexSearch() {
		this.indexPath = "index"; 													// TODO DA MODIFICARE
		try {
			this.indexDir = FSDirectory.open( new File(indexPath) );
			this.reader = DirectoryReader.open( indexDir );
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.searcher = new IndexSearcher( reader );
		this.analyzer = new StandardAnalyzer(Version.LUCENE_47, CharArraySet.EMPTY_SET);
		this.searchResult = new SearchResult();
	}

	@Override
	public SearchResult search(String queryStr, Integer page, Integer itemInPage, 
			Integer lastDocID, Float lastDocScore) {
		
		Query query;
		QueryParser queryParser;
		ScoreDoc lastScore, newLastScore;
		TopScoreDocCollector collector;
		long startTimeQuery, endTimeQuery;
		double queryTime;
		
		startTimeQuery = System.currentTimeMillis();
		
		lastScore = ( (lastDocID != null) && (lastDocScore != null) ) ? 
				new ScoreDoc(lastDocID, lastDocScore) : null;
				
		collector = (lastScore == null) ? 
				TopScoreDocCollector.create( itemInPage, true) :
				TopScoreDocCollector.create( itemInPage, lastScore, true);

		try {
			// TODO Unire ricerca in title, content (e url?)
			queryParser = new QueryParser(Version.LUCENE_47, WebPage.CONTENT, analyzer);
			query = queryParser.parse( queryStr );
			searcher.search(query, collector);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (ScoreDoc hit : hits) {
			Document doc = null;
			int docID = hit.doc;
			
			try {
				doc = searcher.doc( docID );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// VEDI PENULTIMO COMMENTO NEL METODO
			Calendar date = new GregorianCalendar();
			date.setTimeInMillis( Long.parseLong(doc.get(WebPage.INDEXING_DATE)) );
			
			// TODO STATICO: DA IMPLEMENTARE (non qui)
			List<String> skippedWords = new LinkedList<String>();
			skippedWords.add( "skipWord" );

			WebPageSearchResult webPageSearchResult = new WebPageSearchResult(
					doc.get(WebPage.TITLE), 
					doc.get(WebPage.CONTENT).substring(0, 300),	// DRAMMA
					new LinkedList<String>(),
					doc.get(WebPage.URL), 
					skippedWords, 	// STATICO...
					date);			// QUI: O TUTTO O NIENTE!
			searchResult.addWebSearchResult( webPageSearchResult );
		}
		
		newLastScore = hits[hits.length - 1];
		
		endTimeQuery = System.currentTimeMillis();
		queryTime = (endTimeQuery - startTimeQuery) / 1000d;
		
		searchResult.setItemsCount( collector.getTotalHits() );
		searchResult.setPage( page );
		searchResult.setItemsInPage( itemInPage );
//		searchResult.setSuggestedSearches( Suggest.spell(queryStr) );		// TODO I SUGGERIMENTI NON FUNZIONANO
		searchResult.setQueryResponseTime( queryTime );
		searchResult.setDocID( newLastScore.doc );
		searchResult.setDocScore( newLastScore.score );
		return searchResult;
	}

	@Override
	public List<String> autocomplete(String query) {

		return null;
	}
	
}
