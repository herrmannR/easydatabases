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

	public String getSetExpression() {
		String expr = "SET ";
		for (int i = 1; i < this.values.length; i++) {
			if (!isPrimaryKey(i)) {
				expr += this.columnNames[0] + " = ? , ";
			}
		}
		if (expr.endsWith(", ")) {
			expr = expr.substring(0, expr.length() - 2);
		}
		return expr;
	}
}
