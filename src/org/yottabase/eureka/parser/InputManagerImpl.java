package org.yottabase.eureka.parser;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jwat.warc.WarcHeader;
import org.jwat.warc.WarcRecord;
import org.yottabase.eureka.core.InputManager;
import org.yottabase.eureka.core.WebPage;

public class InputManagerImpl implements InputManager {
	
	private Parser parser;
	
	private List<String> inputSources;
	
	private Iterator<String> iter;
	
	
	public InputManagerImpl(String path) {
		List<String> paths = new LinkedList<String>();
		
		paths.add(path);
		init(paths);
	}
	
	
	public InputManagerImpl(List<String> paths) {
		init(paths);
	}
	
	/**
	 * 
	 * @param paths
	 */
	private void init(List<String> paths) {
		this.inputSources = new LinkedList<String>();
		
		processInputPaths(paths);
		
		this.iter = inputSources.iterator();
		this.parser = new WarcParser( getNextInputSource() );
	}
	
	/**
	 * Aggiunge tutti i path alla lista dei path contenenti sorgenti di input e, per 
	 * ciascuno, cerca tutte le sorgenti di input ed esso radicate.
	 * @param paths
	 */
	private void processInputPaths(List<String> paths) {
		for (String p : paths)
			processInputPath(p);
	}
	
	/**
	 * Aggiunge il path alla lista dei path contenenti sorgenti di input e cerca tutte
	 * le sorgenti di input radicate al path.
	 */
	private void processInputPath(String path) {
		this.findInputSources( new File(path) );
	}
	
	/**
	 * Ricerca tutte le potenziali sorgenti di input.
	 * Una sorgente di input è un file .warc o un archivio con estensione .warc.gz
	 * Se il file è una cartella, vengono ricercate tutte le sorgente di input ad 
	 * essa radicate, altrimenti il file stesso è considerata una sorgente di input
	 * @param file
	 */
	private void findInputSources(File file) {
		String path = file.getPath();
		
		if ( file.isFile() ) {
			if ( path.endsWith(".warc") )
				inputSources.add(path);
		} else {
			File[] children = file.listFiles();
			
			for ( File f : children )
				findInputSources(f);
		}
	}

	/**
	 * Restituisce la successiva WebPage estraendola dalla sorgente di input corrente 
	 * se la sorgente di input corrente dispone di un ulteriore record, altrimenti la 
	 * WebPage viene estratta da una successiva sorgente di input se vi sono ulterio- 
	 * -ri sorgenti di input disponibili.
	 * 
	 * Se non sono presenti ulteriori record Warc (sorgenti di input terminate) viene
	 * restituito null. 
	 * 
	 */
	public WebPage getNextWebPage() {
		WarcRecord record;
		WebPage page;
		
		do {
			// Se esistente prendi un record non nullo
			// Un record è nullo quando l'input corrente è terminato
			// Se ci sono altri input ma sono vuoti i record successivi possono continuare ad essere nulli
			
			while ( ((record = parser.getNextRecord()) == null) && this.hasNextInputSource() ) {
				String inputSource = this.getNextInputSource();
				parser.updateInputSource( inputSource );
				
				System.out.println("Analisi del file: " + inputSource);
			}
			
			// Se il miglior record pescato tra tutti i file di input rimanenti è comunque nullo
			// allora l'input è terminato: si chiude il parser
			
			if ( record == null ) {
				parser.close();
				return null;
			}
			
			// Trasforma il record in una web-page
			
			page = warcRecordToWebPage(record);
			
			// Esegui questi passi fin tanto che la web-page generata non è valida 
			// oppure finchè la web-page non è nulla (web-page nulla = fine input)
			
		} while ( !isValid(page) );
		
		return page;
	}
	
	/**
	 * Restituisce true se all'interno dei path configurati vi sono ulteriori sorgenti 
	 * di input a disposizione, false altrimenti.
	 * @return
	 */
	private boolean hasNextInputSource() {
		return iter.hasNext();
	}
	
	/**
	 * Restituisce la prossima sorgente di input disponibile.
	 * @return
	 */
	private String getNextInputSource() {
		return iter.next();
	}
	
	/**
	 * Converte un record Warc in un oggetto WebPage
	 * @param record
	 * @return
	 */
	private WebPage warcRecordToWebPage(WarcRecord record) {
		WebPage webPage = new WebPage();
		WarcHeader warcHeader = record.header;
		Document htmlPage = Jsoup.parse(streamToString( record.getPayloadContent() ));
		
		if ( warcHeader != null ) {
			webPage.setUrl(warcHeader.warcTargetUriStr);
			
			String dateString = warcHeader.warcDateStr;
			String[] dateComps = dateString.
					substring(0, dateString.indexOf("T")).
					split("-");
			String[] timeComps = dateString.
					substring(dateString.indexOf("T") + 1, dateString.lastIndexOf("-")).
					split(":");	
			Calendar date = new GregorianCalendar(
					Integer.parseInt(dateComps[0]),
					Integer.parseInt(dateComps[1]),
					Integer.parseInt(dateComps[2]),
					Integer.parseInt(timeComps[0]),
					Integer.parseInt(dateComps[1]),
					Integer.parseInt(dateComps[2]));
			webPage.setIndexingDate(date);
		}
		if ( htmlPage != null ) {
			webPage.setTitle(htmlPage.title());
			if ( htmlPage.body() != null ) {
				webPage.setContent(htmlPage.body().toString());
				webPage.setContentWithoutTags(htmlPage.body().text());
			}
		}

		return webPage;
	}
	
	/**
	 * Legge uno stream di tipo InputStream convertendone in contenuto in una stringa
	 * @param is 
	 * @return
	 */
	private String streamToString(InputStream is) {
		String str;
		Scanner scan = new Scanner(is);
		scan.useDelimiter("\\A");
		
		str = scan.hasNext() ? scan.next() : "";
		scan.close();
		
		return str;
	}
	
	/**
	 * Verifica che la rappresentazione della pagina web sia valida, ovvero che
	 * tutti i suoi campi non siano nulli
	 * @param page
	 * @return
	 */
	private boolean isValid(WebPage page) {
		return (page.getIndexingDate() != null &&
				page.getUrl() != null &&
				page.getTitle() != null &&
				page.getContent() != null &&
				page.getContentWithoutTags() != null);
	}

}
