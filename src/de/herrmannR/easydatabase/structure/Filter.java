package de.herrmannR.easydatabase.structure;

import java.util.HashMap;
import java.util.Iterator;

public class Filter extends HashMap<String, Object> {

	private static final long serialVersionUID = -4446240850868784812L;

	public Filter() {

	}

	public void addAttribute(String row, Object value) {
		this.put(row, value);
	}

	public Object getAttribute(int index) {
		return this.values().toArray()[index];
	}

	@Override
	public String toString() {
		Iterator<String> keys = this.keySet().iterator();
		String expr = "";
		if (keys.hasNext()) {
			expr = " WHERE " + keys.next() + " = ?";
			while (keys.hasNext()) {
				expr += " AND " + keys.next() + " = ?";
			}
		}
		return expr;
	}

	public int getLength() {
		return this.size();
	}
}
