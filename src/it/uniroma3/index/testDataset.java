package it.uniroma3.index;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class testDataset {
	public static void main(String[] args) throws IOException, ParseException {
		/* create a standard analyzer */
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47,
				CharArraySet.EMPTY_SET);
		/* create the index in the pathToFolder or in RAM (choose one) */
		Directory index = FSDirectory.open(new File("path"));
		/* set an index congif */
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				analyzer);
		config.setOpenMode(OpenMode.CREATE);
		/* create the writer */
		IndexWriter writer = new IndexWriter(index, config);
		/* pick a document */
		String url = "http://www.mirror.co.uk/sport/football/transfer-news/";
		String textdoc = "The captain of Liverpool football club Steven "
				+ "Gerrard announce he will leave the club";
		/* create the document adding the fields */
		Document doc = new Document();
		doc.add(new StringField("url", url, Field.Store.YES));
		doc.add(new TextField("body", textdoc, Field.Store.YES));
		/* write the document */
		writer.addDocument(doc);
		/* close the writer */
		writer.close();

		// /////query

		/* set the maximum number of results */
		int maxHits = 10;
		/* open a directory reader and create searcher and topdocs */
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits,
				true);
		/* create the query parser */
		QueryParser qp = new QueryParser(Version.LUCENE_47, "body", analyzer);
		/* query string */
		String querystring = "gerrard";
		Query q = qp.parse(querystring);
		/* search into the index */
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		/* print results */
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println("url: " + d.get("url") + " Body: "
					+ d.get("body"));
		}
	}
}
