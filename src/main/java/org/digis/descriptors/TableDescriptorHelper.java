package org.digis.descriptors;

import org.digis.descriptors.column.BooleanDescriptor;
import org.digis.descriptors.column.ColumnDescriptor;
import org.digis.descriptors.column.IntegerDescriptor;
import org.digis.descriptors.column.TimestampDescriptor;
import org.digis.descriptors.column.UuidDescriptor;
import org.digis.descriptors.column.VarcharDescriptor;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TableDescriptorHelper {

	private static final Map<String, ColumnDescriptorFactory> typeFactories = Map.of(
			"xs:date", (name, size) -> new TimestampDescriptor(name),
			"xs:long", (name, size) -> new IntegerDescriptor(name),
			"xs:integer", (name, size) -> new IntegerDescriptor(name),
			"xs:boolean", (name, size) -> new BooleanDescriptor(name),
			"xs:string", (name, size) -> new VarcharDescriptor(name, size)
	);

	public static TableDescriptor getTableDescriptor(File file) throws ParserConfigurationException, IOException, SAXException {
		final var filename = file.getName();
		final var tableName = filename
				.substring(3, filename.length() - 4)
				.replaceAll("[_0-9]+", "")
				.toLowerCase();

		final var reader = new FileReader(file);
		final var factory = DocumentBuilderFactory.newInstance();
		final var builder = factory.newDocumentBuilder();
		final var document = builder.parse(new InputSource(reader));
		final var schemaElement = document.getDocumentElement();
		//final var tableName = schemaElement.getElementsByTagName("xs:element")
		//		.item(0)
		//		.getAttributes()
		//		.getNamedItem("name")
		//		.getNodeValue();

		try {
			final var descriptor = new TableDescriptor(tableName);
			final var attributes = schemaElement.getElementsByTagName("xs:attribute");
			final List<ColumnDescriptor> columnNames = new ArrayList<>();
			for (int i = 0; i < attributes.getLength(); i++) {
				final Node node = attributes.item(i);
				columnNames.add(getColumnDescriptor(node));
			}

			columnNames.sort(Comparator.comparing(ColumnDescriptor::getName));
			descriptor.addAll(columnNames);
			return descriptor;

		} catch (Exception e) {
			throw new IllegalStateException(String.format("tableName: %s", tableName), e);
		}
	}

	private static ColumnDescriptor getColumnDescriptor(Node node) {
		final var name = node.getAttributes().getNamedItem("name").getNodeValue();
		try {
			if (name.toLowerCase().contains("uid")) {
				return new UuidDescriptor(name);
			}

			final var type = node.getAttributes().getNamedItem("type");
			if (type != null) {
				final var typeValue = type.getNodeValue();
				if (typeFactories.containsKey(typeValue)) {
					return typeFactories.get(typeValue).create(name, 10);
				} else {
					throw new IllegalStateException(String.format("Undefined type: %s for %s", typeValue, name));
				}
			}

			final var childNodes = node.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				final Node simpleTypeChild = childNodes.item(j);
				if (simpleTypeChild.getNodeName().equals("xs:simpleType")) {
					return getSize(name, simpleTypeChild);
				}
			}

			return new VarcharDescriptor(name, 255);
		} catch (Exception e) {
			throw new IllegalStateException(String.format("column: '%s'", name), e);
		}
	}

	private static ColumnDescriptor getSize(String name, Node node) {
		final var simpleTypeChildList = node.getChildNodes();
		for (int i = 0; i < simpleTypeChildList.getLength(); i++) {
			final var restrictionNode = simpleTypeChildList.item(i);
			if ("xs:restriction".equals(restrictionNode.getNodeName())) {
				final var baseType = restrictionNode.getAttributes().getNamedItem("base").getNodeValue();
				final var childNodes = restrictionNode.getChildNodes();
				if (childNodes.getLength() == 0) {
					return typeFactories.get(baseType).create(name, 10);
				}

				for (int j = 0; j < childNodes.getLength(); j++) {
					final var child = childNodes.item(j);
					if ("xs:length".equals(child.getNodeName())) {
						return typeFactories.get(baseType).create(
								name,
								Integer.parseInt(child.getAttributes().getNamedItem("value").getNodeValue())
						);
					}

					if ("xs:totalDigits".equals(child.getNodeName())) {
						return typeFactories.get(baseType).create(
								name,
								Integer.parseInt(child.getAttributes().getNamedItem("value").getNodeValue())
						);
					}

					if ("xs:pattern".equals(child.getNodeName())) {
						// final var pattern = child.getAttributes().getNamedItem("value").getNodeValue();
						return typeFactories.get(baseType).create(
								name,
								15
						);
					}

					if ("xs:maxLength".equals(child.getNodeName())) {
						return typeFactories.get(baseType).create(
								name,
								Integer.parseInt(child.getAttributes().getNamedItem("value").getNodeValue())
						);
					}

					//if ("xs:minLength".equals(child.getNodeName())) {
					//	return new VarcharDescriptor(
					//			name,
					//			sqlType,
					//			255
					//	);
					//}

					if ("xs:enumeration".equals(child.getNodeName())) {
						int maxEnumSize = 0;
						for (int k = 0; k < childNodes.getLength(); k++) {
							final Node enumItem = childNodes.item(k);
							if ("xs:enumeration".equals(enumItem.getNodeName())) {
								final int enumSize = enumItem
										.getAttributes()
										.getNamedItem("value")
										.getNodeValue()
										.length();

								maxEnumSize = Math.max(maxEnumSize, enumSize);
							}
						}

						return typeFactories.get(baseType).create(
								name,
								maxEnumSize
						);
					}
				}

				throw new IllegalStateException();
			}
		}

		return new VarcharDescriptor(name, 255);
	}

	private interface ColumnDescriptorFactory {
		ColumnDescriptor create(String name, int size);
	}
}
