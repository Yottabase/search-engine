package org.yottabase.eureka.indexer;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.yottabase.eureka.core.InputManagerFake;
import org.yottabase.eureka.core.SearchResult;
import org.yottabase.eureka.searcher.SearchIndexFile;

public class Indexer {

	public static void main(String[] args) throws IOException, ParseException,
			java.text.ParseException {
		boolean create = false;
		String path = "indexDataset";
		InputManagerFake source = new InputManagerFake();

		/* create a standard analyzer */
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47,
				CharArraySet.EMPTY_SET);

		/* create the index in the pathToFolder */
		Directory index = FSDirectory.open(new File(path));

		/* set an index congif */
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				analyzer);

		if (!create) {
			config.setOpenMode(OpenMode.CREATE);
		} else {
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}

		/* create the writer */
		IndexWriter writer = new IndexWriter(index, config);

		// create the document adding the fields
		Document doc = new Document();

		/*
		 * ciclo while da abilitare successivamente
		 */
		// while (source.getNextWebPage() != null) {

		doc.add(new StringField("url", source.getNextWebPage().getUrl(),
				Field.Store.YES));

		doc.add(new TextField("title", source.getNextWebPage().getTitle(),
				Field.Store.YES));
		doc.add(new TextField("content", source.getNextWebPage().getContent(),
				Field.Store.YES));
		System.out.println(source.getNextWebPage().getIndexingDate());

		String data = DateTools.dateToString(source.getNextWebPage()
				.getIndexingDate(), Resolution.DAY);
		doc.add(new TextField("indexingDate", data, Field.Store.YES));

		writer.addDocument(doc);
		// }

		writer.close();

		/*
		 * richiamo la classe SearchIndex per testare il funzionamento
		 * successivamente sarà sostituita dalla chiamata del controller
		 */
		SearchIndexFile search = new SearchIndexFile();
		SearchResult view = new SearchResult();

		view = search.search("prova", 1, 1);
		System.out.println("title:" + view.getWebPages().get(0).getTitle()
				+ "   	url:" + view.getWebPages().get(0).getUrl()
				+ "     content:" + view.getWebPages().get(0).getSnippet()
				+ "  	item:   " + view.getItemsCount() + "  tempo:  "
				+ view.getQueryResponseTime() + "    suggerimenti: "
				+ view.getSuggestedSearch());

	}
}
