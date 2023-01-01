package org.digis.descriptors.column;

public class BooleanDescriptor implements ColumnDescriptor {
	private final String name;

	public BooleanDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("\t\"%s\" BOOL", name);
	}
}
