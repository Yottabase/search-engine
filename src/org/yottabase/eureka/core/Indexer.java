package org.yottabase.eureka.core;

public interface Indexer {
	
	/**
	 * Indicizza una singola pagina del web
	 * @param snippet
	 */
	public void addToIndex(WebPageSearchResult snippet);

}
