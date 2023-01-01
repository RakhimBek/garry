package org.digis;

import java.io.File;
import java.util.concurrent.Executors;

public class Main {
	private static final int THREAD_COUNT = 20;

	public static void main(String[] args) {
		final var outputDirectory = new File(args[0]);
		if (!outputDirectory.mkdirs()) {
			System.out.println("Can not create output directory.");
			return;
		}

		final var csvGenerator = new CsvGenerator();
		final var executor = Executors.newFixedThreadPool(THREAD_COUNT);
		for (String fileName : args) {
			final var file = new File(fileName);
			if (file.isFile()) {
				executor.execute(() -> {
					final var tid = Thread.currentThread().getId();
					try {
						final var generatedFile = csvGenerator.generate(file, outputDirectory);
						System.out.printf("%s: '%s' generated from '%s'%n", tid, generatedFile.getName(), file.getAbsolutePath());

					} catch (NoRowsException e) {
						System.out.printf("%s: Empty rows on '%s'%n", tid, file.getAbsolutePath());
					} catch (Exception e) {
						System.out.printf("%s: Generation failed for '%s'%n", tid, file.getAbsolutePath());
					}
				});
			}
		}

		executor.shutdown();
	}
}
