package de.herrmannR.easydatabase.structure;

import java.util.Set;

public abstract class Package {

	protected final String[] columnNames;
	protected final String[] dataTypes;
	protected final Set<String> primaryKeys;
	protected final int columnCount;

	protected Package(String[] columnNames, String[] dataTypes, Set<String> primaryKeys, int columnCount) {
		if (columnCount < 1) {
			throw new IllegalArgumentException(
					"RowPackage has to have at least one column, but columnCount is " + columnCount);
		}
		Set<String> nameSet = Set.of(columnNames);
		for (String key : primaryKeys) {
			if (!nameSet.contains(key)) {
				throw new IllegalArgumentException(
						"The given primary key '" + key + "' does not exist in the column names array, but it has to.");
			}
		}
		this.columnNames = columnNames;
		this.dataTypes = dataTypes;
		this.primaryKeys = primaryKeys;
		this.columnCount = columnCount;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public String getDataType(int column) {
		this.checkForValidColumn(column);
		return this.dataTypes[column];
	}

	public String[] getColumnNames() {
		return this.columnNames;
	}

	public String getColumnName(int column) {
		this.checkForValidColumn(column);
		return this.columnNames[column];
	}

	public boolean isPrimaryKey(int column) {
		return this.primaryKeys.contains(this.getColumnName(column));
	}

	protected void checkForValidColumn(int column) {
		if (!(column < this.columnCount)) {
			throw new ArrayIndexOutOfBoundsException("Typed column index is to big.");
		}
	}
}
