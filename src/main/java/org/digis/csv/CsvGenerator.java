package org.digis.csv;

import org.digis.csv.handlers.CsvRowsWriterHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class CsvGenerator {
	private static final int THREAD_COUNT = 20;
	private static final String DELIMITER = "\t";
	private static final String OUPUT_FILE_EXTENSION = ".tsv";
	private static final int BUFFER_SIZE = 2 * 1024 * 1024; // 2 Mb

	public void generate(List<File> fileList, File outputDirectory) {
		final var executor = Executors.newFixedThreadPool(THREAD_COUNT);
		for (File file : fileList) {
			executor.execute(() -> {
				final var tid = Thread.currentThread().getId();
				try {
					final var generatedFile = generate(file, outputDirectory);
					System.out.printf("%s: '%s' generated from '%s'%n", tid, generatedFile.getName(), file.getAbsolutePath());

				} catch (NoRowsException e) {
					System.out.printf("%s: Empty rows on '%s'%n", tid, file.getAbsolutePath());
				} catch (Exception e) {
					System.out.printf("%s: Generation failed for '%s'%n", tid, file.getAbsolutePath());
				}
			});
		}
		executor.shutdown();
	}

	private File generate(File file, File outputDirectory) throws IOException, SAXException, ParserConfigurationException, NoRowsException {
		final var extractor = new ColumnsNameExtractor();
		final var columns = extractor.extract(file);

		final var outputFile = outputDirectory
				.toPath()
				.resolve(file.getName().concat(OUPUT_FILE_EXTENSION))
				.toFile();

		try (
				final var fileInputStream = new FileInputStream(file);
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
