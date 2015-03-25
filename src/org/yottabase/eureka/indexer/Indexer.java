package org.yottabase.eureka.indexer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.yottabase.eureka.core.InputManager;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.core.WebPage;
import org.yottabase.eureka.parser.InputManagerImpl;
import org.yottabase.eureka.searcher.SearchIndexFile;

public class Indexer {
	

	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
		String outputPath;
		List<String> inputPaths;
		InputManager iManager;
		
		Directory index;
		StandardAnalyzer analyzer;
		IndexWriterConfig config;
		IndexWriter writer;
		
		Document doc;
		WebPage webPage;
		
		
		inputPaths = new LinkedList<String>();
		inputPaths.add( args[0] );
//		inputPaths.add( args[1] );
		iManager = new InputManagerImpl( inputPaths );
		
		outputPath = "indexDataset";

		index = FSDirectory.open( new File(outputPath) );
		analyzer = new StandardAnalyzer(Version.LUCENE_47, CharArraySet.EMPTY_SET);
		config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		
		writer = new IndexWriter(index, config);

		while ( (webPage = iManager.getNextWebPage()) != null ) {
			doc = new Document();
			doc.add(new StringField("url", webPage.getUrl(), Field.Store.YES));
			doc.add(new TextField("title", webPage.getTitle(), Field.Store.YES));
			doc.add(new TextField("content", webPage.getContent(), Field.Store.YES));
			doc.add(new LongField("indexingDate", webPage.getIndexingDate().getTimeInMillis(), Field.Store.YES));

			writer.addDocument(doc);
		}
		
		writer.close();
		
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
