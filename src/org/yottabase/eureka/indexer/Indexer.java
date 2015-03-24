package it.uniroma3.index;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	public static void main(String[] args) throws IOException {
		boolean create = false;
		String path = "indexDataset";
		File sourcePath = new File("a/b");

		/* create a standard analyzer */
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47,
				CharArraySet.EMPTY_SET);

		/* create the index in the pathToFolder or in RAM (choose one) */
		Directory index = FSDirectory.open(new File(path));

		/* set an index congif */
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				analyzer);

		if (!create) {
			config.setOpenMode(OpenMode.CREATE);
		} else {
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}

		/* create the writer */
		IndexWriter writer = new IndexWriter(index, config);
		indexDocs(writer, sourcePath);

		/*
		 * create the document adding the fields Document doc = new Document();
		 * 
		 * doc.add(new StringField("url", url, Field.Store.YES));
		 * 
		 * doc.add(new TextField("body", textdoc, Field.Store.YES));
		 * 
		 * writer.addDocument(doc);
		 * 
		 * writer.close();
		 */
	}

	static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {

			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			}

			else {

				try {
					// vedi lavoro leonardo
					Map<String, String> field2info = null;

					// make a new, empty document
					Document doc = new Document();

					/**
					 * Field title
					 */

					Field titleField = new TextField("title",
							field2info.get("title"), Field.Store.YES);

					Field pathField = new StringField("path", file.getPath(),
							Field.Store.YES);

					FieldType type = new FieldType();
					type.setIndexed(true);
					type.setStored(true);
					type.setStoreTermVectors(true);
					type.setTokenized(true);
					type.setStoreTermVectorOffsets(true);
					Field highlighterField = new Field("highlighterWords",
							field2info.get("text"), type); // with term vector
															// enabled

					Field contentsField = new TextField("words",
							field2info.get("text"), Field.Store.YES);

					doc.add(pathField);
					doc.add(titleField);
					doc.add(contentsField);
					doc.add(highlighterField);

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						writer.addDocument(doc);
					} else {
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}
				} finally {
				}
			}
		}
	}
}
