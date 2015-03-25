package org.yottabase.eureka.core;

import java.util.Calendar;
import java.util.List;

public class WebPageSearchResult {

	/**
	 * Titolo
	 */
	private String title;

	/**
	 * Pezzo di pagina che contiene il risultato della query
	 */
	private String snippet;

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

	public WebPageSearchResult(String title, String snippet, String url,
			List<String> skippedWords, Calendar date) {
		this.title = title;
		this.snippet = snippet;
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

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
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

	@Override
	public String toString() {
		return "WebPageSearchResult" + "\n" +
				"\t" + "TITLE" + "\t\t" + title + "\n" +
				"\t" + "SNIPPET" + "\t\t" + snippet + "\n" +
				"\t" + "SKIPPED" + "\t\t" + skippedWords + "\n" +
				"\t" + "URL" + "\t\t" + url + "\n" +
				"\t" + "DATE" + "\t\t" + date.getTime().toString();
	}
	

}
