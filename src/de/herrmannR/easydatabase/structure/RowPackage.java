package de.herrmannR.easydatabase.structure;

import java.util.Set;

public class RowPackage extends Package {

	private Object[] values;

	protected RowPackage(Object[] values, String[] columnNames, String[] dataTypes, Set<String> primaryKeys,
			int columnCount) {
		super(columnNames, dataTypes, primaryKeys, columnCount);
		this.values = values;
	}

	public void setValue(int index, Object value) {
		this.values[index] = value;
	}

	public Object getValue(int index) {
		try {
			return values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Format: "SET <i>column1</i> = ? , <i>column2</i> = ? , ... "
	 * 
	 * @return
	 */
	public String getSetExpression() {
		String expr = " SET ";
		for (int i = 0; i < this.values.length; i++) {
			if (!isPrimaryKey(i)) {
				expr += this.columnNames[i] + " = ? ";
				if (i + 1 != this.values.length) {
					expr += ", ";
				}
			}
		}
		return expr;
	}

	/**
	 * Format: " VALUES ( DEFAULT, ... , ? , ... ) " or " VALUES ( ? , ... ) " The
	 * tags DEFAULT an ? can summon in any order.
	 * 
	 * @return
	 */
	public String getInsertExpression() {
		String expr = " VALUES (";
		for (int i = 0; i < this.values.length; i++) {
			if (this.getValue(i).equals("DEFAULT")) {
				expr += "DEFAULT ";
			} else {
				expr += "? ";
			}
			if (i + 1 != this.values.length) {
				expr += ", ";
			}
		}
		expr += ") ";
		return expr;
	}

	@Override
	public String toString() {
		String array = "\n\t[";
		for (int i = 0; i < this.columnCount; i++) {
			try {
				array += "\"" + this.getValue(i) + "\" ";
			} catch (IndexOutOfBoundsException e) {
				array += "\"!UNDEFIENED!\" ";
			}
		}
		array += "]";
		return array;
	}
}
