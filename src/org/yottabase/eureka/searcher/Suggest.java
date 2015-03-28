package org.yottabase.eureka.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.yottabase.eureka.core.WebPage;

public class Suggest {
	float accuracy;
	public Suggest(float set_accuracy){
		this.accuracy=set_accuracy;
	}
	public Suggest(){
		this.accuracy=0.87f;
	}
	public ArrayList<String> spell(String queryString){
		String indexPath;
		Directory indexDir;
		Directory spellDir;
		DirectoryReader reader;
		StandardAnalyzer analyzer;
	    IndexWriterConfig config; 
	    
		SpellChecker spellChecker;
		
		ArrayList<String> similarWords = new ArrayList<String>();

		
		try {
			indexPath=SearcherConfiguration.getIndexPath();
			indexDir = FSDirectory.open(new File(indexPath));
		    reader = DirectoryReader.open( indexDir);
		    analyzer = new StandardAnalyzer(Version.LUCENE_47);
		    config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		    
			spellDir = FSDirectory.open(new File("spellChecker"));
			spellChecker = new SpellChecker(spellDir);

			// To index a field of a user index:
			spellChecker.indexDictionary(new LuceneDictionary(reader,WebPage.CONTENT ), config, true);
			spellChecker.setAccuracy(accuracy);
			
			Collections.addAll(similarWords,spellChecker.suggestSimilar(queryString, 10));

	        spellChecker.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return similarWords;
	}
}
