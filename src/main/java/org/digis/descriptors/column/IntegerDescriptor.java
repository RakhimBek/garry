package org.digis.descriptors.column;

public class IntegerDescriptor implements ColumnDescriptor {
	private final String name;

	public IntegerDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("\t\"%s\" INTEGER", name);
	}
}
