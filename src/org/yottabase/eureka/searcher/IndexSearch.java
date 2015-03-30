package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queries.mlt.MoreLikeThis;
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
	
	private final Integer maxHits = 500;

	public IndexSearch() {
		this.indexPath = SearcherConfiguration.	getIndexPath();
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
		long startTimeQuery = System.currentTimeMillis();
		
		int slotsNumber = Math.min(itemInPage*page, maxHits);
		TopScoreDocCollector collector = TopScoreDocCollector.create(slotsNumber, true);

		try {
			QueryParser queryParser = new QueryParser(
					Version.LUCENE_47, 
					WebPage.CONTENT, 
					analyzer);
			query = queryParser.parse( queryStr );
			searcher.search(query, collector);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ScoreDoc[] hits = collector.topDocs((page-1)*itemInPage, page*itemInPage-1 ).scoreDocs;
		List<WebPageSearchResult> webPagesList = new LinkedList<WebPageSearchResult>();
		for (int i = 0; i < hits.length; i++) {
			try {
				int id = hits[i].doc;
				Document doc = searcher.doc(id);

				
				WebPageSearchResult webPageSearchResult = 
						documentToWebPageSearchResult(doc, query, id, WebPage.CONTENT, 3);
				
				webPagesList.add( webPageSearchResult );
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		/*
		 * da valutare se mantenere 
		 * 
		 */
	/*
			try {
				getMoreLikeThis(hits[0]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
			}*/
		
		/* 
		 */
		
		long endTimeQuery = System.currentTimeMillis();
		double queryTime = (endTimeQuery - startTimeQuery) / 1000d;
		
		/* suggestion */
		SearchSuggestion suggestionEngine = new SearchSuggestion();
		List<String >suggestions = suggestionEngine.spell(queryStr);
		
		/* Filling in the search result values */
		searchResult.setItemsCount( collector.getTotalHits() );
		searchResult.setPage( page );
		searchResult.setItemsInPage( itemInPage );
		searchResult.setQueryResponseTime( queryTime );
		searchResult.setSuggestedSearches(suggestions);
		searchResult.setWebPages( webPagesList );
		// TODO set executed query to search result
		return searchResult;
	}
	
	
	private WebPageSearchResult documentToWebPageSearchResult(
			Document doc, Query query, int docID, String snippedDocField, int snippetFragsNum) {
		
		WebPageSearchResult page = new WebPageSearchResult();
		
		String title = doc.get( WebPage.TITLE );
		String url = doc.get( WebPage.URL );
		String snippet = getHighlightedSnippet(query, doc, docID, snippedDocField);
		
		page.setTitle(title);
		page.setHighlightedSnippet(snippet);
		page.setUrl(url);
		return page;
	}
	
	/**
	 * 
	 * @param query
	 * @param doc
	 * @param id
	 * @param field
	 * @return
	 */
	private String getHighlightedSnippet(Query query, Document doc, int id, String field) {
		String highlights = new String();
		
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		QueryScorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(htmlFormatter, scorer);
		String text = doc.get(field);
		
	    try {
	    	highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 80));
	    	TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, field, analyzer);
	    	TextFragment[] fragments = highlighter.getBestTextFragments(tokenStream, text, false, 3);
	    	
	    	for (TextFragment frag : fragments) {
	    		String fragment = frag.toString();
	    		fragment = fragment.replaceAll("^[^\\w]*", "");
	    		fragment = fragment.replaceAll("[^\\w]*$", "");
	    		
	    		highlights += fragment + "..." + "\n";
	    	}
	    	
		} catch (IOException | InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}

	    return highlights;
	}

	
	@Override
	/*
	 * Dopo le modifiche apportate dovrei andare andare a modificare l'interfaccia di Search
	 * 
	 */
	public List<String> autocomplete(String query) {
		SearchSuggestion suggest_=new SearchSuggestion();
		List<String >suggestions =suggest_.autocomplete(query);
		return suggestions;
	}
	
	public String getMoreLikeThis(ScoreDoc hit) throws IOException, ParseException{

		MoreLikeThis mlt = new MoreLikeThis(reader);
		mlt.setAnalyzer(analyzer);
		mlt.setFieldNames(new String[] {WebPage.CONTENT});
		String splitted = null;
		
		Query query = mlt.like(hit.doc);

		TopDocs topDocs = searcher.search(query,10);		
		int i=0;
		while(topDocs.totalHits>5 && i<=4){
			Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
			splitted = doc.get(WebPage.TITLE);
			System.out.println("ricerche simili :  " + splitted);
			i++;
		}
		
		
		return splitted;
	}
	
}
