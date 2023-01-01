package org.digis;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		final var extract = new ColumnsNameExtractor();
		final var file = new File(args[0]);

		final var csvGenerator = new CsvGenerator(file);
		csvGenerator.generate();
	}
}
