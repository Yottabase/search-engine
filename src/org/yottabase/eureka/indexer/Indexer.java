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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.yottabase.eureka.core.InputManager;
import org.yottabase.eureka.core.WebPage;
import org.yottabase.eureka.parser.InputManagerImpl;

public class Indexer {
	
	public static void main(String[] args) throws IOException, ParseException {
		String outputPath;
		List<String> inputPaths;
		InputManager iManager;
		
		Directory index;
		StandardAnalyzer analyzer;
		IndexWriterConfig config;
		IndexWriter writer;
		
		Document doc;
		WebPage webPage;
		
		long start, end, pages;
		double time;
		
		
		inputPaths = new LinkedList<String>();
		inputPaths.add( args[0] );
//		inputPaths.add( args[1] );
		iManager = new InputManagerImpl( inputPaths );
		
		outputPath = "index";

		index = FSDirectory.open( new File(outputPath) );
		analyzer = new StandardAnalyzer(Version.LUCENE_47, CharArraySet.EMPTY_SET);
		config = ( new IndexWriterConfig(Version.LUCENE_47, analyzer) ).setOpenMode(OpenMode.CREATE);
		writer = new IndexWriter(index, config);

		System.out.println("Index creation...\n");
		pages = 0;
		start = System.currentTimeMillis();
		
		while ( (webPage = iManager.getNextWebPage()) != null ) {
			doc = new Document();
			doc.add(new StringField(WebPage.URL, webPage.getUrl(), Field.Store.YES));
			doc.add(new TextField(WebPage.TITLE, webPage.getTitle(), Field.Store.YES));
			doc.add(new TextField(WebPage.CONTENT, webPage.getContent(), Field.Store.YES));
			doc.add(new LongField(WebPage.INDEXING_DATE, webPage.getIndexingDate().getTimeInMillis(), Field.Store.YES));

			writer.addDocument(doc);
			pages++;
		}
		
		end = System.currentTimeMillis();
		time = (end - start) / 1000d;
		System.out.println("Creation completed!");
		System.out.println(pages + " pages indexed in " + time + " seconds.");
		
		writer.close();
	}
	
}
