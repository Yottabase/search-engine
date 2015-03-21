package it.uniroma3.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class SearchIndexFile {
	private int maxHits;
	private String indexPath;
	private Directory indexDir;
	private DirectoryReader reader;
	private StandardAnalyzer analyzer;
	private IndexSearcher searcher;

	public SearchIndexFile() throws IOException {
		this.maxHits = 10; /* set the maximum number of results */
		this.indexPath = "path"; // da modificare
		this.indexDir = FSDirectory.open(new File(indexPath));
		this.reader = DirectoryReader.open(indexDir);
		this.searcher = new IndexSearcher(reader);
	}

	public List<String> search(String queryString) throws ParseException,
			IOException {
		List<String> QueryResults = new ArrayList<String>();

		/* open a directory reader and create searcher and topdocs */

		TopScoreDocCollector collector = TopScoreDocCollector.create(
				this.maxHits, true);

		QueryParser qp = new QueryParser(Version.LUCENE_47, "XXXXX",
				this.analyzer);
		/* query string */
		Query q = qp.parse(queryString);

		/* search into the index */
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// int numberOfResults = hits.length;

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			QueryResults.add(d.get("XXXx"));
		}
		return QueryResults;

	}

	public static String spell(String queryString) throws IOException {

		String suggestedQueryString = null;
		String[] similarWords = null;
		SpellChecker spellChecker = new SpellChecker(getIndexDir());

		similarWords = spellChecker.suggestSimilar(queryString, 5);

		if (similarWords != null) {
			suggestedQueryString = similarWords[0];
			System.out.println("Forse cercavi " + suggestedQueryString);
		}
		return suggestedQueryString;
	}

	private static Directory getIndexDir() {
		// TODO Auto-generated method stub
		return this.indexDir;
	}
}
