package org.digis.descriptors.column;

public class UuidDescriptor implements ColumnDescriptor {
	private final String name;

	public UuidDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("\t%s uuid", name);
	}
}
