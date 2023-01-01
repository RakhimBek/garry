package org.digis;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		final var csvGenerator = new CsvGenerator();
		for (String fileName : args) {
			final var file = new File(fileName);
			if (file.isFile()) {
				generate(csvGenerator, file);
			}
		}
	}

	private static void generate(CsvGenerator csvGenerator, File file) {
		try {
			final var generatedFile = csvGenerator.generate(file);
			System.out.printf("'%s' generated from '%s'%n", generatedFile.getName(), file.getAbsolutePath());

		} catch (Exception e) {
			System.out.printf("Generation failed for '%s'%n", file.getAbsolutePath());
		}
	}
}
