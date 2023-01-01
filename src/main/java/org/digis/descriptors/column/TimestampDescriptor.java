package org.digis.descriptors.column;

public class TimestampDescriptor implements ColumnDescriptor {
	private final String name;

	public TimestampDescriptor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("\t%s timestamp", name);
	}
}
