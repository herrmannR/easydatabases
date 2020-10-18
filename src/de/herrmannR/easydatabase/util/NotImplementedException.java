package de.herrmannR.easydatabase.util;

public class NotImplementedException extends Exception {

	private static final long serialVersionUID = -2764858713640108487L;

	public NotImplementedException(String unimplemented) {
		super(unimplemented + " is not implemented yet.");
	}
}
