package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
	private SearchResult searchResultItem;

	public IndexSearch() throws IOException {
		this.maxHits = 100; /* set the maximum number of results */
		this.indexPath = "index"; // da modificare
		this.indexDir = FSDirectory.open(new File(indexPath));
		this.reader = DirectoryReader.open(indexDir);
		this.searcher = new IndexSearcher(reader);
		this.analyzer = new StandardAnalyzer(Version.LUCENE_47,CharArraySet.EMPTY_SET);
		this.searchResultItem = new SearchResult();

	}

	@Override
	public SearchResult search(String query, Integer page, Integer itemInPage) throws IOException, ParseException, java.text.ParseException {

		/* open a directory reader and create searcher and topdocs */

		TopScoreDocCollector collector = TopScoreDocCollector.create( this.maxHits, true);

		
		//QueryParser qp = createQuery(query);
		QueryParser qp = new QueryParser(Version.LUCENE_47, WebPage.CONTENT, this.analyzer);
		/* query string */
		Query q = qp.parse(query);

		/* search into the index */
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		/* creo l'oggetto searchResult */
		long startTimeQuery = System.currentTimeMillis();
		searchResultItem.setItemsCount( hits.length );
		searchResultItem.setPage(page);

		ArrayList<WebPageSearchResult> ListWebPages = new ArrayList<WebPageSearchResult>();

		for (int i = 0; i < hits.length; ++i) {

			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			Calendar data = new GregorianCalendar();
			data.setTimeInMillis(Long.parseLong(doc.get("indexingDate")));
			/*
			 * adesso passo il content dovro passare i snippet della ricerca
			 */
			ArrayList<String> skippedWords = new ArrayList<String>();
			skippedWords.add(new String("skipWord"));

			/*
			 * richiamo la classe per i suggerimenti
			 */
			ArrayList<String> suggestion = Suggest.spell(query);
			searchResultItem.setSuggestedSearch(suggestion);

			WebPageSearchResult webPageSearchResult = new WebPageSearchResult(
					doc.get("title"), 
					doc.get("content").substring(0, 30),
					doc.get("url"), skippedWords, data);
			ListWebPages.add(webPageSearchResult);

			searchResultItem.setWebPages(ListWebPages);
		}
		long EndTimeQuery = System.currentTimeMillis();
		searchResultItem
				.setQueryResponseTime((double) ((EndTimeQuery - startTimeQuery)) / 1000d);

		return searchResultItem;

	}
	
	private QueryParser createQuery(String query) {
		
		
		return null;
	}

	@Override
	public List<String> autocomplete(String query) throws IOException {

		return null;
	}
}
