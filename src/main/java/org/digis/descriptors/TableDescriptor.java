package org.digis.descriptors;

import org.digis.descriptors.column.ColumnDescriptor;

import java.util.ArrayList;
import java.util.List;

public class TableDescriptor {
	private static final String newline = String.format("%n");
	private static final String delimiter = "\t";
	private final String name;
	private final List<ColumnDescriptor> columnsDescriptors = new ArrayList<>();

	public TableDescriptor(String name) {
		this.name = name.toLowerCase();
	}

	public void add(ColumnDescriptor columnDescriptor) {
		columnsDescriptors.add(columnDescriptor);
	}

	public void addAll(List<ColumnDescriptor> columnDescriptors) {
		columnsDescriptors.addAll(columnDescriptors);
	}

	public String getCsvHeader() {
		return columnsDescriptors.stream()
				.map(ColumnDescriptor::getName)
				.reduce((left, right) -> left + delimiter + right)
				.orElse("");
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder()
				.append(String.format("create table %s%n(%n", name));

		final var primaryKey = String.format("\tid integer not null constraint pk_%s unique", name.toLowerCase());
		final var columns = columnsDescriptors.stream()
				.map(Object::toString)
				.reduce(primaryKey, (left, right) -> String.format("%s,%n%s", left, right));

		builder
				.append(columns)
				.append(newline);

		return builder
				.append(");")
				.toString();
	}
}
