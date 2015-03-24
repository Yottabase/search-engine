package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class suggest {

	public static ArrayList<String> spell(String queryString)
			throws IOException {

		// String suggestedQueryString = null;
		ArrayList<String> similarWords = new ArrayList<String>();

		Directory indexDir = FSDirectory.open(new File("indexDataset"));
		@SuppressWarnings("resource")
		SpellChecker spellChecker = new SpellChecker(indexDir);

		Collections.addAll(similarWords,
				spellChecker.suggestSimilar(queryString, 5));

		/*
		 * if (similarWords != null) { suggestedQueryString = similarWords[0];
		 * System.out.println("Forse cercavi " + suggestedQueryString); } return
		 * suggestedQueryString;
		 */
		return similarWords;
	}
}
