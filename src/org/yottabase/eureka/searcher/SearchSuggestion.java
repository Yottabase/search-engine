package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchSuggestion {
	private Directory spellDir;
	private SpellChecker spellChecker;
	private float accuracy=0.8f;
	
	public List<String> autocomplete(String query) {
		String autocompleteSuggest[] = null;
		List<String> suggestions= new LinkedList<String>();
		try {
			spellDir = FSDirectory.open(new File(SearcherConfiguration.getDictionaryPath()));
			spellChecker = new SpellChecker(spellDir);
			spellChecker.setAccuracy(accuracy);
			autocompleteSuggest=spellChecker.suggestSimilar(query, 10);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.addAll(suggestions,autocompleteSuggest);
		
		return suggestions;
	}
	
	public ArrayList<String> didYouMean(String queryString){
		
		ArrayList<String> similarWords = new ArrayList<String>();
		String similarWordsConcat = "";
		String listSuggest[]=null;

		try {
			spellDir = FSDirectory.open(new File(SearcherConfiguration.getDictionaryPath()));
			spellChecker = new SpellChecker(spellDir);

			// To index a field of a user index:
			spellChecker.setAccuracy(accuracy);
			
			/*
			 * da valutare l'inserimento del ciclo for che inverte la lista perche dovrebbe migliorare le risposte dei suggerimenti
			 */
			if (queryString.contains(" ")) {
			    String[] parts = queryString.split(" ");
			    for (int i = 0; i < parts.length; i++) {
					listSuggest=spellChecker.suggestSimilar(parts[i], 10);
					if(listSuggest.length!=0){
						similarWordsConcat+=(listSuggest[listSuggest.length-1]);
						similarWordsConcat+=" ";
					}
				}
				Collections.addAll(similarWords,similarWordsConcat);

			} else{
				listSuggest=spellChecker.suggestSimilar(queryString, 10);
				if(listSuggest.length!=0){
					similarWords.add(listSuggest[(listSuggest.length)-1]);
				}
			}
			
	        spellChecker.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return similarWords;
	}

}
