package de.herrmannR.easydatabase.GUI.components;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
	public static final String DATE = "java.sql.Date";
	public static final String STRING = "java.lang.String";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	private final String dataType;

	public InputField(String dataType, Object data, boolean isEditable) {
		if (!(dataType.equals(DOUBLE) || dataType.equals(INTEGER) || dataType.equals(STRING)
				|| dataType.equals(DATE))) {
			System.err.println("The dataType " + dataType
					+ " is yet not implement in de.herrmannR.GUI.admin.components.Inputfield!\n It will be treated like String.");
			this.dataType = STRING;
		} else {
			this.dataType = dataType;
		}
		PlainDocument doc = (PlainDocument) this.getDocument();
		doc.setDocumentFilter(new DatatypeController());
		if (!(data == null || data.equals(""))) {
			if (dataType.equals(DATE)) {
				this.setText(SIMPLE_DATE_FORMAT.format((Date) data));
			} else {
				this.setText(String.valueOf(data));
			}
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
		case DATE:
			return LocalDate.parse(this.getText(), DATE_FORMAT);
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
				} else if (InputField.this.dataType == DATE) {
					return isValidDate(text);
				}
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		private boolean isValidDate(String date) {
			String[] splitted = date.split(".");
			for (int i = 0; i < splitted.length; i++) {
				try {
					int k = Integer.parseInt(splitted[i]);
					System.out.println(k);
					switch (i) {
					case 0:
						if (k < 1 && k > 31) {
							return false;
						}
						break;
					case 1:
						if (k < 1 && k > 12) {
							return false;
						}
						break;
					case 2:
						if (k < 0) {
							return false;
						}
						break;
					default:
						return false;
					}
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
	}
}
