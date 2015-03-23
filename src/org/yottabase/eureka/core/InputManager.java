package org.yottabase.eureka.core;

public interface InputManager {
	
	/**
	 * Aggiunge un path di 
	 * 	- un file .warc 
	 * 	- un archivio GZip contente un file .warc
	 * 	- una cartella contente files .warc e/o archivi contenenti ciascuno un file .warc
	 * 
	 * @param path
	 */
	public void addInputPath(String path);
	
	/**
	 * Restituisce la prossima WebPage se presente nel complesso dei file .warc di input,
	 * null altrimenti.
	 * 
	 * @param webpage
	 */
	public WebPage getNextWebPage();

}
