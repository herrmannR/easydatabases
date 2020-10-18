package de.herrmannR.easydatabase.GUI.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class InputField extends JTextField {

	private static final long serialVersionUID = -2399460747261180315L;

	public static final String DOUBLE = "java.lang.Double";
	public static final String INTEGER = "java.lang.Integer";
	public static final String STRING = "java.lang.String";

	private final String dataType;

	public InputField(String dataType, Object data, boolean isEditable) {
		if (!(dataType.equals(DOUBLE) || dataType.equals(INTEGER) || dataType.equals(STRING))) {
			System.err.println("The dataType " + dataType
					+ " is yet not implement in de.herrmannR.GUI.admin.components.Inputfield!\n It will be treated like String.");
			this.dataType = STRING;
		} else {
			this.dataType = dataType;
		}
		PlainDocument doc = (PlainDocument) this.getDocument();
		doc.setDocumentFilter(new DatatypeController());
		if (data != null) {
			this.setText(String.valueOf(data));
		}
		this.setEditable(isEditable);
	}

	public InputField(String dataType, Object data) {
		this(dataType, data, true);
	}

	public InputField(String dataType) {
		this(dataType, "");
	}

	public Object getValue() throws NumberFormatException {
		if (this.getText().equals("")) {
			return null;
		}
		switch (this.dataType) {
		case STRING:
			return this.getText();
		case INTEGER:
			return Integer.parseInt(this.getText());
		case DOUBLE:
			return Double.parseDouble(this.getText());
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.dataType);
		}
	}

	class DatatypeController extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (isValid(sb.toString())) {
				super.insertString(fb, offset, string, attr);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (isValid(sb.toString())) {
				super.replace(fb, offset, length, text, attrs);
			}
		}

		private boolean isValid(String text) {
			try {
				if (InputField.this.dataType == INTEGER) {
					Integer.parseInt(text);
				} else if (InputField.this.dataType == DOUBLE) {
					Double.parseDouble(text);
				}
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}
}
