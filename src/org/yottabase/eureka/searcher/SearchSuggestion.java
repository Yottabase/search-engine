package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchSuggestion {
	
	private float accuracy=0.87f;
	
	public List<String> autocomplete(String query) {
		this.accuracy=0.8f;
		List<String >suggestions =spell(query);
		
		return suggestions;
	}
	
	public ArrayList<String> spell(String queryString){
		Directory spellDir;
		SpellChecker spellChecker;
		ArrayList<String> similarWords = new ArrayList<String>();

		try {
			spellDir = FSDirectory.open(new File(SearcherConfiguration.getDictionaryPath()));
			spellChecker = new SpellChecker(spellDir);

			// To index a field of a user index:
			spellChecker.setAccuracy(accuracy);
			
			Collections.addAll(similarWords,spellChecker.suggestSimilar(queryString, 10));

	        spellChecker.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return similarWords;
	}

}
