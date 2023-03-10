package org.digis.csv;

import org.digis.csv.handlers.ColumnNamesHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ColumnsNameExtractor {
	public List<String> extract(File file) throws IOException, ParserConfigurationException, SAXException {
		final var fileInputStream = new FileInputStream(file);
		final var rowsHandler = new ColumnNamesHandler();
		final var saxParserFactory = SAXParserFactory.newInstance();
		final var parser = saxParserFactory.newSAXParser();
		parser.parse(fileInputStream, rowsHandler);
		return rowsHandler.getAttributes();
	}
}
