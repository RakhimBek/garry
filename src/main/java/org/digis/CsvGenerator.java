package org.digis;

import org.digis.handlers.CsvRowsWriterHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CsvGenerator {
	private static final String DELIMITER = ";";
	private final File file;

	public CsvGenerator(File file) {
		this.file = file;
	}

	public void generate() throws ParserConfigurationException, SAXException, IOException {
		final var extractor = new ColumnsNameExtractor();
		final var columns = extractor.extract(file);

		final var fileInputStream = new FileInputStream(file);
		final var csvRowsWriterHandler = new CsvRowsWriterHandler(DELIMITER, columns);
		final var saxParserFactory = SAXParserFactory.newInstance();
		final var parser = saxParserFactory.newSAXParser();
		parser.parse(fileInputStream, csvRowsWriterHandler);
	}
}
