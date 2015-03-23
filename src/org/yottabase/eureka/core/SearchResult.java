package org.yottabase.eureka.core;

import java.util.List;

public class SearchResult {

	/**
	 * Numero di item totali riscontrati dalla ricerca
	 */
	private Integer itemsCount;
	
	/**
	 * Numero di pagina della ricerca
	 */
	private Integer page;
	
	/**
	 * Numero di item (reali) presenti nella pagina
	 */
	private Integer itemInPage;
	
	/**
	 * Items della ricerca
	 */
	private List<WebPageSearchResult> webPages;
	
	/**
	 * Elenco di possibile alternative della query (Forse cercavi)
	 */
	private List<String> suggestedSearch;

	/**
	 * Numero di secondi che sono stati utilizzati per effettuare la ricerca
	 */
	private Double queryResponseTime;

	public Integer getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Integer itemsCount) {
		this.itemsCount = itemsCount;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getItemInPage() {
		return itemInPage;
	}

	public void setItemInPage(Integer itemInPage) {
		this.itemInPage = itemInPage;
	}

	public List<WebPageSearchResult> getWebPages() {
		return webPages;
	}

	public void setWebPages(List<WebPageSearchResult> webPages) {
		this.webPages = webPages;
	}

	public List<String> getSuggestedSearch() {
		return suggestedSearch;
	}

	public void setSuggestedSearch(List<String> suggestedSearch) {
		this.suggestedSearch = suggestedSearch;
	}

	public Double getQueryResponseTime() {
		return queryResponseTime;
	}

	public void setQueryResponseTime(Double queryResponseTime) {
		this.queryResponseTime = queryResponseTime;
	}
	
	
	
}
