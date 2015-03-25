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
	private int maxHits;					// TODO NON DOVREBBE ESSERE UN LONG?
	private String indexPath;
	private Directory indexDir;
	private DirectoryReader reader;
	private StandardAnalyzer analyzer;
	private IndexSearcher searcher;
	private SearchResult searchResultItem;	// TODO SearchResult NON E' UN ITEM: PERCHE' SI CHIAMA COSI??!

	public IndexSearch() throws IOException {
		this.maxHits = 100; 				// set the maximum number of results */
		this.indexPath = "index"; 			// TODO DA MODIFICARE
		this.indexDir = FSDirectory.open(new File(indexPath));
		this.reader = DirectoryReader.open( indexDir );
		this.searcher = new IndexSearcher( reader );
		this.analyzer = new StandardAnalyzer(Version.LUCENE_47, CharArraySet.EMPTY_SET);
		this.searchResultItem = new SearchResult();
	}

	@Override
	public SearchResult search(String queryStr, Integer page, Integer itemInPage) 
			throws IOException, ParseException, java.text.ParseException {
		
		long startTimeQuery, endTimeQuery;
		double queryTime;
		
		// open a directory reader and create searcher and topdocs
		TopScoreDocCollector collector = TopScoreDocCollector.create( maxHits, true);
		
		// TODO Unire ricerca in content, title (e url ?)
		QueryParser queryParser = new QueryParser(Version.LUCENE_47, WebPage.CONTENT, analyzer);
		Query query = queryParser.parse( queryStr );

		/* search into the index */
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// TODO DEVE ESSERE ALL'INIZIO DELLA SOTTOPOSIZIONE DELLA QUERY
		startTimeQuery = System.currentTimeMillis();

		// TODO SearchResult DOVREBBE AVERE UN COSTRUTTORE NO-ARG CHE INIZIALIZZA QUESTA LISTA
		List<WebPageSearchResult> webPagesList = new LinkedList<WebPageSearchResult>();

		for (ScoreDoc hit : hits) {
			Document doc = searcher.doc( hit.doc );
			
			// VEDI PENULTIMO COMMENTO NEL METODO
			Calendar date = new GregorianCalendar();
			date.setTimeInMillis( Long.parseLong(doc.get(WebPage.INDEXING_DATE)) );
			
			// TODO STATICO: DA IMPLEMENTARE (non qui)
			List<String> skippedWords = new LinkedList<String>();
			skippedWords.add( "skipWord" );

			// TODO I SUGGERIMENTI NON FUNZIONANO
			List<String> suggestions = Suggest.spell(queryStr);
			// TODO VENGONO SETTATI I SUGGERIMENTI SOLO RELATIVI ALL'ULTIMA QUERY 
			//		(VEDI PENULTIMO COMMENTO DENTRO IL FOR)
			searchResultItem.setSuggestedSearch( suggestions );

			WebPageSearchResult webPageSearchResult = new WebPageSearchResult(
					doc.get(WebPage.TITLE), 
					doc.get(WebPage.CONTENT).substring(0, 30),	// DRAMMA
					doc.get(WebPage.URL), 
					skippedWords, 	// STATICO...
					date);			// QUI: O TUTTO O NIENTE!
			webPagesList.add(webPageSearchResult);

			// TODO SearchResult DOVREBBE AVERE DIRETTAMENTE UN METODO "addWebSearchResult"
			// 		COSI' COME UN METODO "addSuggestedSearch"
			// TODO MODIFICA QUINDI ANCHE PASSAGGIO SUBITO PRECEDENTE
			searchResultItem.setWebPages(webPagesList);
		}
		// TODO DEVE ESSERE AL PUNTO DI RISPOSTA DELLA QUERY
		endTimeQuery = System.currentTimeMillis();
		queryTime = (endTimeQuery - startTimeQuery) / 1000d;
		
		// TUTTI (o quasi) GLI ATTRIBUTI DI SearchResult SETTATI QUI (o all'inizio, ma insieme)
		searchResultItem.setItemsCount( hits.length );
		searchResultItem.setPage( page );
		searchResultItem.setQueryResponseTime( queryTime );
		// TODO MANCA IL SETTING DI ALCUNI ATTRIBUTI (e.g.: itemInPage ? )

		return searchResultItem;
	}

	@Override
	public List<String> autocomplete(String query) throws IOException {

		return null;
	}
	
}
