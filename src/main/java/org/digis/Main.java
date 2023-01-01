package org.digis;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		final var extract = new ColumnsNameExtractor();
		final var file = new File(args[0]);

		System.out.println();
		System.out.println(extract.extract(file));
	}
}
