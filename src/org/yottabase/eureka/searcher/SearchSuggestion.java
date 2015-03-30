package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.FreeTextSuggester;
import org.apache.lucene.search.suggest.tst.TSTLookup;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.yottabase.eureka.core.WebPage;

public class SearchSuggestion {
	private Directory spellDir;
	private SpellChecker spellChecker;
	private float accuracy=0.8f;
	
	public List<String> autocomplete(String query) {
		List<String> result = new ArrayList<String>();
		
		String prefix = "";
		
		query = query.trim();
		
		if(query.contains(" ")) {
			prefix = query.substring(0, query.lastIndexOf(' ')) + ' ';
			query = query.substring(query.lastIndexOf(' ') + 1);
		}
		
		try {
			Lookup lookup = new TSTLookup();
			Directory indexPathDir = FSDirectory.open(new File(SearcherConfiguration.getIndexPath()));
			IndexReader ir = DirectoryReader.open(indexPathDir);
			
			Dictionary dictionary = new LuceneDictionary(ir, WebPage.TITLE);
			
			lookup.build(dictionary);
			
			List<LookupResult> resultsList = lookup.lookup(query, false, 30);
			
			for(LookupResult lr : resultsList){
				String v = lr.key.toString();
				if( !v.equals(query) ){
					result.add(prefix + v);
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
		
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
			queryString = queryString.trim();
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
