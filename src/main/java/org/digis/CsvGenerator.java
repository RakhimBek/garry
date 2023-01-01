package org.digis;

import org.digis.handlers.CsvRowsWriterHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CsvGenerator {
	private static final String DELIMITER = ";";
	private static final String OUPUT_FILE_EXTENSION = ".csv";
	private static final int BUFFER_SIZE = 92 * 1024 * 1024; // 2 Mb

	public File generate(File file, File outputDirectory) throws IOException, SAXException, ParserConfigurationException, NoRowsException {
		final var extractor = new ColumnsNameExtractor();
		final var columns = extractor.extract(file);
		final var fileInputStream = new FileInputStream(file);

		final var outputFile = outputDirectory
				.toPath()
				.resolve(file.getName().concat(OUPUT_FILE_EXTENSION))
				.toFile();

		try (
				final var fileOutputStream = new FileOutputStream(outputFile);
				final var bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE)
		) {
			final var csvRowsWriterHandler = new CsvRowsWriterHandler(DELIMITER, columns, bufferedOutputStream);
			final var saxParserFactory = SAXParserFactory.newInstance();
			final var parser = saxParserFactory.newSAXParser();
			parser.parse(fileInputStream, csvRowsWriterHandler);

			if (csvRowsWriterHandler.getCount() == 0) {
				throw new NoRowsException();
			}
			return outputFile;
		} catch (Exception e) {
			outputFile.delete();
			throw e;
		}
	}
}
