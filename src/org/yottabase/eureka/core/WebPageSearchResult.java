package org.yottabase.eureka.core;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class WebPageSearchResult {

	/**
	 * Titolo
	 */
	private String title;

	/**
	 * Pezzo di pagina che contiene il risultato della query
	 */
	private String highlightedSnippet;

	/**
	 * Url della pagina
	 */
	private String url;

	/**
	 * Elenco dei termini di ricerca saltati durante la query
	 */
	private List<String> skippedWords;

	/**
	 * La data in cui è stato esguito il crawling della pagina
	 */
	private Calendar date;
	
	public WebPageSearchResult() {
		this.skippedWords = new LinkedList<String>();
	}

	public WebPageSearchResult(String title, String snippet, List<String> highlights, 
			String url, List<String> skippedWords, Calendar date) {
		this.title = title;
		this.highlightedSnippet = snippet;
		this.url = url;
		this.skippedWords = skippedWords;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHighlightedSnippet() {
		return highlightedSnippet;
	}

	public void setHighlightedSnippet(String snippet) {
		this.highlightedSnippet = snippet;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getSkippedWords() {
		return skippedWords;
	}

	public void setSkippedWords(List<String> skippedWords) {
		this.skippedWords = skippedWords;
	}


	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public boolean addSkippedWord(String skippedWord) {
		return this.skippedWords.add(skippedWord);
	}

	@Override
	public String toString() {
		return "WebPageSearchResult" + "\n" +
				"\t" + "TITLE" + "\t\t" + title + "\n" +
				"\t" + "SNIPPET" + "\t\t" + highlightedSnippet + "\n" +
				"\t" + "SKIPPED" + "\t\t" + skippedWords + "\n" +
				"\t" + "URL" + "\t\t" + url + "\n" +
				"\t" + "DATE" + "\t\t" + date.getTime().toString();
	}
	

}
