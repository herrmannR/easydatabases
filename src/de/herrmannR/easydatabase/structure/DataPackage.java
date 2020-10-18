package de.herrmannR.easydatabase.structure;

import java.util.ArrayList;
import java.util.Set;

public class DataPackage extends Package {

	private final ArrayList<Object[]> values;
	private int rowCount;

	public DataPackage(int columnCount, Set<String> primaryKeys) {
		super(new String[columnCount], new String[columnCount], primaryKeys, columnCount);
		this.values = new ArrayList<>();
		this.rowCount = 0;
	}

	public void setValue(int row, int column, Object value) {
		this.checkValidAccess(row, column);
		if (this.values.get(row)[column] != null) {
			throw new IllegalArgumentException(
					"For this column (" + column + ") a value is already set!" + "\n" + "Current value: "
							+ this.values.get(row)[column] + "\n" + "Tried to overwrite with this value: " + value);
		}
		this.values.get(row)[column] = value;
	}

	public void setColumnName(int column, String columnName) {
		this.checkForValidColumn(column);
		if (this.columnNames[column] != null) {
			throw new IllegalArgumentException(
					"For this column (" + column + ") a name is already set!" + "\n" + "Current name: "
							+ this.columnNames[column] + "\n" + "Tried to overwrite with this name: " + columnName);
		}
		this.columnNames[column] = columnName;
	}

	public void setDataType(int column, String dataType) {
		this.checkForValidColumn(column);
		if (this.dataTypes[column] != null) {
			throw new IllegalArgumentException(
					"For this column (" + column + ") a datatype is already set!" + "\n" + "Current datatype: "
							+ this.dataTypes[column] + "\n" + "Tried to overwrite with this datatype: " + dataType);
		}
		this.dataTypes[column] = dataType;
	}

	public int addRow() {
		this.values.add(new Object[this.columnCount]);
		this.rowCount++;
		return rowCount - 1;
	}

	public Object[][] getDataArray() {
		Object[][] data = new Object[this.rowCount][];
		Object[] rawData = this.values.toArray();
		for (int i = 0; i < this.rowCount; i++) {
			data[i] = (Object[]) rawData[i];
		}
		return data;
	}

	public RowPackage createRowPackage(int column) {
		Object[] rowData;
		try {
			rowData = this.values.get(column);
		} catch (IndexOutOfBoundsException e) {
			rowData = new Object[this.columnCount];
		}
		System.out.println(this);
		return new RowPackage(rowData, this.columnNames, this.dataTypes, this.primaryKeys, column + 1);
	}

	public Object getValue(int row, int column) {
		try {
			return this.values.get(row)[column];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	private void checkValidAccess(int row, int column) {
		if (row >= this.rowCount || column >= this.columnCount) {
			throw new ArrayIndexOutOfBoundsException("Typed column index or row index is to big.");
		}
	}

	@Override
	public String toString() {
		String array = "\n[";
		for (int i = 0; i < this.rowCount; i++) {
			array += "[ ";
			for (int j = 0; i < this.columnCount; i++) {
				try {
					array += "\"" + this.getValue(i, j) + "\" ";
				} catch (IndexOutOfBoundsException e) {
					array += "\"!UNDEFIENED!\" ";
				}
			}
			array += "]";
		}
		array += "]";
		return array;
	}
}
