package org.digis;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.digis.csv.CsvGenerator;
import org.digis.csv.NoRowsException;
import org.digis.csv.handlers.CsvRowsWriterHandler;
import org.digis.descriptors.TableDescriptor;
import org.digis.descriptors.TableDescriptorHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		final var firstArg = args[0];
		final var generateSql = firstArg.equals("-d");
		final var outputDirectory = new File(generateSql ? args[1] : args[0]);
		if (!outputDirectory.mkdirs()) {
			System.out.println("Can not create output directory.");
			return;
		}

		if (generateSql) {
			final var fileList = Arrays.stream(args)
					.map(File::new)
					.filter(File::isFile)
					.filter(f -> f.getName().endsWith("xsd"))
					.collect(Collectors.toList());

			for (File file : fileList) {
				final var tableDescriptor = TableDescriptorHelper.getTableDescriptor(file);

				final var outputFile = outputDirectory
						.toPath()
						.resolve(file.getName().concat(".sql"))
						.toFile();

				try (
						final var fileOutputStream = new FileOutputStream(outputFile);
				) {
					fileOutputStream.write(tableDescriptor.toString().getBytes(StandardCharsets.UTF_8));
				}
			}

		} else {
			final var fileList = Arrays.stream(args)
					.map(File::new)
					.filter(File::isFile)
					.collect(Collectors.toList());

			final var csvGenerator = new CsvGenerator();
			csvGenerator.generate(fileList, outputDirectory);
		}
	}
}
