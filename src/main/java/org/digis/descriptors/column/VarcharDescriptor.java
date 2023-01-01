package org.digis.descriptors.column;

public class VarcharDescriptor implements ColumnDescriptor {
	private final String name;
	private final int size;

	public VarcharDescriptor(String name, int size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("\t\"%s\" VARCHAR(%d)", name, size);
	}
}
