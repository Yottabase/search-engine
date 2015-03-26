package org.yottabase.eureka.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

public class WarcParser implements Parser {
	
	private InputStream in;
	
	private WarcReader reader;
	
	
	public WarcParser(String sourceInputPath) {
		init(sourceInputPath);
	}
	
	/**
	 * Punta il reader Warc sul file al path passato come parametro
	 * @param sourceInputPath
	 */
	private void init(String sourceInputPath) {
		File file = new File(sourceInputPath);
		
		try {
			this.in = new FileInputStream(file);
			this.reader = WarcReaderFactory.getReader(in);
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
		}
		catch (IOException e) {
            e.printStackTrace();
		}
		
	}

	/**
	 * Restituisce il prossimo record Warc presente nel file 
	 * di input corrente o null se non vi sono ulteriori record
	 */
	@Override
	public WarcRecord getNextRecord() {
		WarcRecord record = null;
		
		try {
			record = reader.getNextRecord();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return record;
	}

	/**
	 * Aggiorna la sorgente di input con un nuovo file al path
	 * passato come parametro
	 */
	@Override
	public void updateInputSource(String newInputSourcePath) {
		close();
		init(newInputSourcePath);
	}

	/**
	 * Chiude lo stream ed il reader Warc
	 */
	@Override
	public void close() {
		try {
			this.in.close();
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
