package org.digis.csv.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ColumnNamesHandler extends DefaultHandler {
	private final Set<String> attributesSet = new LinkedHashSet<>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		for (int index = 0; index < attributes.getLength(); index++) {
			final var attributesLocalName = attributes.getLocalName(index);
			attributesSet.add(attributesLocalName);
		}
	}

	public List<String> getAttributes() {
		return new ArrayList<>(attributesSet);
	}
}
