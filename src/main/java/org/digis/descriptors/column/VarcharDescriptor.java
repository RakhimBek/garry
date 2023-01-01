package org.digis.descriptors.column;

public class VarcharDescriptor implements ColumnDescriptor {
	private final String name;
	private final String type;
	private final int size;

	public VarcharDescriptor(String name, String type, int size) {
		this.name = name;
		this.type = type;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("\t%s %s(%d)", name, type, size);
	}
}
