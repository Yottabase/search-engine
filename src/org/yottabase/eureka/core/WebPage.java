package org.yottabase.eureka.core;

import java.util.Calendar;

public class WebPage {

	/**
	 * Url della pagina
	 */
	private String url;

	/**
	 * Titolo
	 */
	private String title;

	/**
	 * Pagina senza tag html
	 */
	private String content;

	/**
	 * Pagina senza tag html
	 */
	private String contentWithoutTags;

	/**
	 * La data in cui è stato eseguito il crawling della pagina
	 */
	private Calendar indexingDate;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentWithoutTags() {
		return contentWithoutTags;
	}

	public void setContentWithoutTags(String contentWithoutTags) {
		this.contentWithoutTags = contentWithoutTags;
	}

	public Calendar getIndexingDate() {
		return indexingDate;
	}

	public void setIndexingDate(Calendar indexingDate) {
		this.indexingDate = indexingDate;
	}

	@Override
	public String toString() {
		return "WebPage [url=" + url + ", title=" + title + ", indexingDate="
				+ indexingDate + "]";
	}

}
