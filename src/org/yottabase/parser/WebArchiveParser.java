package org.yottabase.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jwat.common.Diagnosis;
import org.jwat.common.HeaderLine;
import org.jwat.common.Payload;
import org.jwat.warc.WarcHeader;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

public class WebArchiveParser {
	
	static String warcFile = "/home/hduser/Scrivania/00.warc.3sites";
	
	public static void main(String[] args) {
		File file = new File(warcFile);
		try {
			InputStream in = new FileInputStream(file);
			
			int records = 0;
			int errors = 0;
			
			WarcReader reader = WarcReaderFactory.getReader(in);
			WarcRecord record;
			
			while ( (record = reader.getNextRecord()) != null ) {
				printRecord(record);
				printRecordErrors(record);
				
				++records;
				
				if (record.diagnostics.hasErrors())
					errors += record.diagnostics.getErrors().size();
			}
			
			System.out.println("--------------");
            System.out.println("       Records: " + records);
            System.out.println("        Errors: " + errors);
            reader.close();
            in.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
		}
	}

	public static void printRecord(WarcRecord record) {
		WarcHeader header = record.header;
		//HeaderLine field = record.getHeader("field");
		List<HeaderLine> headerList = record.getHeaderList();
		
		Payload payload = record.getPayload();
		String payloadContent = streamToString( record.getPayloadContent() );
		
		System.out.println();
		System.out.println("*********** NEW RECORD ***********");
		// header
		// HEADER --> TARGET-URI
		System.out.println();
		System.out.println("-------------- HEADER --------------");
		System.out.println("        Header: " + header.toString());
		System.out.println("       TypeIdx: " + header.warcTypeIdx);
		System.out.println("          Type: " + header.warcTypeStr);
		System.out.println("      Filename: " + header.warcFilename);
		System.out.println("     Record-ID: " + header.warcRecordIdUri.toString());
		System.out.println("          Date: " + header.warcDateStr);
		System.out.println("Content-Length: " + header.contentLengthStr);
		System.out.println("  Content-Type: " + header.contentTypeStr);
		System.out.println("     Truncated: " + header.warcTruncatedStr);
		System.out.println("   InetAddress: " + header.warcInetAddress);
		System.out.println("     IpAddress: " + header.warcIpAddress);
		System.out.println("  ConcurrentTo: " + header.warcConcurrentToList);
		System.out.println("      RefersTo: " + header.warcRefersToUri);
		System.out.println("     TargetUri: " + header.warcTargetUriUri);
		System.out.println("   WarcInfo-Id: " + header.warcWarcInfoIdUri);
		System.out.println("   BlockDigest: " + header.warcBlockDigest);
		System.out.println(" PayloadDigest: " + header.warcPayloadDigest);
		System.out.println("IdentPloadType: " + header.warcIdentifiedPayloadType);
		System.out.println("       Profile: " + header.warcProfileStr);
		System.out.println("      Segment#: " + header.warcSegmentNumber);
        System.out.println(" SegmentOrg-Id: " + header.warcSegmentOriginIdUrl);
        System.out.println("SegmentTLength: " + header.warcSegmentTotalLength);
        
        // header-list
        System.out.println();
        System.out.println("-------------- HEADER-LIST --------------");
        int i = 1;
        for(HeaderLine headerLine : headerList) {
        	//System.out.println("   Header-Line: " + headerLine.toString());
        	System.out.println("   Header-Line " + i);
        	System.out.println("          	Name: " + headerLine.name);
        	//System.out.println("          Type: " + headerLine.type);
        	System.out.println("         	Value: " + headerLine.value);
        	//System.out.println("         Lines: " + headerLine.lines.toString());
        	i++;
        }
        
		// payload
        // PAYLOAD-->HTTP-HEADER-->CONTENT-TYPE 
        System.out.println();
        System.out.println("-------------- PAYLOAD --------------");
        System.out.println("       Payload: " + payload.toString());
        System.out.println("     HttpHeader " + payload.getHttpHeader());
        
        // payload content
        System.out.println();
        System.out.println("-------------- PAYLOAD-CONTENT --------------");
        System.out.println("PayloadContent: " + payloadContent);
        //Document doc = Jsoup.parse(payloadContent);
        //System.out.println(doc.body());
        //System.out.println( ( new HtmlToPlainText() ).getPlainText(doc) );
        
        System.out.println();
        System.out.println();
    }
	
	public static void printRecordErrors(WarcRecord record) {
		System.out.println("PRINTING ERRORS NOW");
        if ( record.diagnostics.hasErrors() ) {
            Collection<Diagnosis> errorCol = record.diagnostics.getErrors();
            if (errorCol != null && errorCol.size() > 0) {
                Iterator<Diagnosis> iter = errorCol.iterator();
                while ( iter.hasNext() ) {
                	Diagnosis error = iter.next();
                    //System.out.println( error.error );
                    //System.out.println( error.field );
                    //System.out.println( error.value );
                	for(int i = 0; i < error.information.length; i++)
                		System.out.println( error.information[i]);
                }
            }
        }
    }
	
	private static String streamToString(InputStream is) {
		String str;
		Scanner scan = new Scanner(is);
		scan.useDelimiter("\\A");
		
		str = scan.hasNext() ? scan.next() : "";
		scan.close();
		
		return str;
	}
	
}
