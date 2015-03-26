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
	private int maxHits;
	private String indexPath;
	private Directory indexDir;
	private DirectoryReader reader;
	private StandardAnalyzer analyzer;
	private IndexSearcher searcher;
	private SearchResult searchResultItem;	// TODO SearchResult NON E' UN ITEM: PERCHE' SI CHIAMA COSI??!
//	private ScoreDoc lastScore;

	public IndexSearch() throws IOException {
		this.maxHits = 500000;
		this.indexPath = "index"; 			// TODO DA MODIFICARE
		this.indexDir = FSDirectory.open(new File(indexPath));
		this.reader = DirectoryReader.open( indexDir );
		this.searcher = new IndexSearcher( reader );
		this.analyzer = new StandardAnalyzer(Version.LUCENE_47, CharArraySet.EMPTY_SET);
		this.searchResultItem = new SearchResult();
//		this.lastScore = null;
	}

	@Override
	public SearchResult search(String queryStr, Integer page, Integer itemInPage) 
			throws IOException, ParseException, java.text.ParseException {
		
		long startTimeQuery, endTimeQuery;
		double queryTime;
		
		// open a directory reader and create searcher and topdocs
		TopScoreDocCollector collector = TopScoreDocCollector.create( itemInPage, true);
		
		// TODO UNIRE RICERCA IN TITLE, CONTENT (E URL?)
		QueryParser queryParser = new QueryParser(Version.LUCENE_47, WebPage.CONTENT, analyzer);
		Query query = queryParser.parse( queryStr );

		/* search into the index */
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		// TODO DEVE ESSERE ALL'INIZIO DELLA SOTTOPOSIZIONE DELLA QUERY
		startTimeQuery = System.currentTimeMillis();

		// TODO SearchResult DOVREBBE AVERE UN COSTRUTTORE NO-ARG CHE INIZIALIZZA QUESTA LISTA
		List<WebPageSearchResult> webPagesList = new LinkedList<WebPageSearchResult>();

		int i;
		for (i = 0; i < hits.length; i++) {
			int docID = hits[i].doc;
			Document doc = searcher.doc( docID );
			
			// VEDI PENULTIMO COMMENTO NEL METODO
			Calendar date = new GregorianCalendar();
			date.setTimeInMillis( Long.parseLong(doc.get(WebPage.INDEXING_DATE)) );
			
			// TODO STATICO: DA IMPLEMENTARE (non qui)
			List<String> skippedWords = new LinkedList<String>();
			skippedWords.add( "skipWord" );

			// TODO FUORI DAL FOR! (IN BASSO, ASSIEME AL RESTO)
			// TODO I SUGGERIMENTI NON FUNZIONANO
			List<String> suggestions = Suggest.spell(queryStr);
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
//		System.out.println("i="+i);
//		System.out.println("hits.length="+hits.length);
//		lastScore = hits[hits.length-1];
//		System.out.println("DOC = " + lastScore.doc);
//		System.out.println("SCORE = " + lastScore.score);
		// TODO DEVE ESSERE AL PUNTO DI RISPOSTA DELLA QUERY
		endTimeQuery = System.currentTimeMillis();
		queryTime = (endTimeQuery - startTimeQuery) / 1000d;
		
		// TUTTI (o quasi) GLI ATTRIBUTI DI SearchResult SETTATI QUI (o all'inizio, ma insieme)
		searchResultItem.setItemsCount( collector.getTotalHits() );
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
