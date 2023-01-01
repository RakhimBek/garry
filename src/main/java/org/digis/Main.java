package org.digis;

import org.digis.csv.CsvGenerator;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) {
		final var outputDirectory = new File(args[0]);
		if (!outputDirectory.mkdirs()) {
			System.out.println("Can not create output directory.");
			return;
		}

		final var fileList = Arrays.stream(args)
				.map(File::new)
				.filter(File::isFile)
				.collect(Collectors.toList());

		final var csvGenerator = new CsvGenerator();
		csvGenerator.generate(fileList, outputDirectory);
	}
}
