package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
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
	
	private final Integer maxHits = 500;

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
	public SearchResult search(String queryStr, Integer page, Integer itemInPage) {
		
		Query query = null;
		MultiFieldQueryParser queryParser;
		
		long startTimeQuery = System.currentTimeMillis();
		
		int slotsNumber = Math.min(itemInPage*page, maxHits);
		TopScoreDocCollector collector = TopScoreDocCollector.create(slotsNumber, true);

		try {
			queryParser = new MultiFieldQueryParser(
					Version.LUCENE_47, 
					new String[] { WebPage.TITLE, WebPage.CONTENT }, 
					analyzer);
			query = queryParser.parse( queryStr );
			searcher.search(query, collector);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ScoreDoc[] hits = collector.topDocs((page-1)*itemInPage, page*itemInPage-1 ).scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			int id = hits[i].doc;
			Document doc = null;
			
			try {
				doc = searcher.doc(id);
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
					doc.get( WebPage.TITLE ), 
					doc.get( WebPage.CONTENT).substring(0, 300 ),	// DRAMMA
					getHighlightedSnippets(query, doc, id, WebPage.CONTENT),	// STATICO
					doc.get(WebPage.URL), 
					skippedWords, 	// STATICO...
					date);			// QUI: O TUTTO O NIENTE!
			searchResult.addWebSearchResult( webPageSearchResult );
		}
		
		long endTimeQuery = System.currentTimeMillis();
		double queryTime = (endTimeQuery - startTimeQuery) / 1000d;
		
		searchResult.setItemsCount( collector.getTotalHits() );
		searchResult.setPage( page );
		searchResult.setItemsInPage( itemInPage );
//		searchResult.setSuggestedSearches( Suggest.spell(queryStr) );		// TODO I SUGGERIMENTI NON FUNZIONANO
		searchResult.setQueryResponseTime( queryTime );
		// TODO set executed query to search result
		return searchResult;
	}
	
	/**
	 * 
	 * @param query
	 * @param doc
	 * @param id
	 * @param field
	 * @return
	 */
	private List<String> getHighlightedSnippets(Query query, Document doc, int id, String field) {
		List<String> highlights = new LinkedList<String>();
		
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		String text = doc.get(field);
		
	    try {
	    	
	    	TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, field, analyzer);
	    	TextFragment[] fragments = highlighter.getBestTextFragments(tokenStream, text, false, 10);	// TODO settare numero di frammenti
	    	
	    	for (TextFragment frag : fragments)
	    		highlights.add(frag.toString());
	    	
		} catch (IOException | InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
	    
	    return highlights;
	}

	
	@Override
	public List<String> autocomplete(String query) {

		return null;
	}
	
}
