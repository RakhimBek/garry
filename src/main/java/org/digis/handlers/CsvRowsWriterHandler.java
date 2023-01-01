package org.digis.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsvRowsWriterHandler extends DefaultHandler {

	private final String delimiter;
	private final List<String> columns;

	private int depth = 0;
	private final Map<String, String> values = new HashMap<>();

	public CsvRowsWriterHandler(String delimiter, List<String> columns) {
		this.delimiter = delimiter;
		this.columns = columns;
	}

	@Override
	public void startDocument() throws SAXException {
		System.out.println(String.join(delimiter, columns));
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
			final var value = values.get(column);
			csvValues.add(Objects.requireNonNullElse(value, ""));
		}

		System.out.println(String.join(delimiter, csvValues));
	}
}
