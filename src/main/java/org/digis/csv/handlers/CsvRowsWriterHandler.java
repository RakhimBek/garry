package org.digis.csv.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsvRowsWriterHandler extends DefaultHandler {
	private static final String EMPTY_STRING = "\"\"";
	private static final byte[] NEW_LINE_BYTES = String.format("%n").getBytes(StandardCharsets.UTF_8);

	private final String delimiter;
	private final List<String> columns;
	private final BufferedOutputStream fileOutputStream;
	private final Map<String, String> values = new HashMap<>();
	private int depth = 0;
	private int count = 0;

	public CsvRowsWriterHandler(String delimiter, List<String> columns, BufferedOutputStream fileOutputStream) {
		this.delimiter = delimiter;
		this.columns = columns;
		this.fileOutputStream = fileOutputStream;
	}

	@Override
	public void startDocument() throws SAXException {
		try {
			fileOutputStream.write(String.join(delimiter, columns).getBytes(StandardCharsets.UTF_8));
			fileOutputStream.write(NEW_LINE_BYTES);

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		depth++;

		values.clear();
		for (int index = 0; index < attributes.getLength(); index++) {
			final var attributesLocalName = attributes.getLocalName(index);
			final var attributesValue = attributes.getValue(index);
			values.put(attributesLocalName, attributesValue);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		depth--;

		if (depth != 1) {
			return;
		}

		final List<String> csvValues = new ArrayList<>();
		for (String column : columns) {
			final var value = Objects.requireNonNullElse(values.get(column), EMPTY_STRING)
					.trim();

			csvValues.add(value);
		}

		try {
			if (!csvValues.isEmpty()) {
				fileOutputStream.write(String.join(delimiter, csvValues).getBytes(StandardCharsets.UTF_8));
				fileOutputStream.write(NEW_LINE_BYTES);
				count++;
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public int getCount() {
		return count;
	}
}
