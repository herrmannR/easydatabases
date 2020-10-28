package de.herrmannR.easydatabase.GUI.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.InputField;
import de.herrmannR.easydatabase.structure.Filter;
import de.herrmannR.easydatabase.structure.RowPackage;

public abstract class RowDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -7919629073096617762L;

	private static final String SAVE = "save";
	private static final String CANCEL = "cancel";

	private static final int START_HEIGHT = 25;
	private static final int ROW_HEIGHT = 50;
	private static final int DIALOG_WIDTH = 400;
	private static final int SPACE_LAST_FIELD_BUTTONS = 65;

	private static final int FIELD_HEIGHT = 25;
	private static final int FIELD_WIDTH = 200;
	private static final int FIELD_START_WIDTH = 150;

	private static final int LABEL_HEIGHT = 25;
	private static final int LABEL_WIDTH = 130;
	private static final int LABEL_START_WIDTH = 20;

	private static final int BUTTON_HEIGHT = 25;
	private static final int BUTTON_WIDTH = 130;
	private static final int BUTTON_SPACE = 5;

	private RowPackage dataPackage;

	protected String tableName;
	protected InputField[] inputFields;
	protected Filter primaryKeys;

	public RowDialog(DatabaseView parent, String tableName, String saveButtonName) throws SQLException {
		this(parent, tableName, new Filter(), saveButtonName);
	}

	public RowDialog(DatabaseView parent, String tableName, Filter primaryKeys, String saveButtonName)
			throws SQLException {
		super(parent);
		this.dataPackage = DatabaseManager.getInstance().selectFrom(tableName, primaryKeys).createRowPackage(0);
		this.inputFields = new InputField[dataPackage.getColumnCount()];
		this.tableName = tableName;
		this.primaryKeys = primaryKeys;

		this.getContentPane().setLayout(null);
		this.initLabels();
		this.initFields();
		this.initButtons(saveButtonName);
		this.setMinimumSize(new Dimension(DIALOG_WIDTH, dataPackage.getColumnCount() * ROW_HEIGHT + START_HEIGHT
				+ SPACE_LAST_FIELD_BUTTONS + BUTTON_HEIGHT + START_HEIGHT));
		this.setModal(true);
		this.setVisible(true);
	}

	private void initLabels() {
		for (int i = 0; i < dataPackage.getColumnCount(); i++) {
			JLabel label = new JLabel((String) dataPackage.getColumnName(i));
			label.setLocation(LABEL_START_WIDTH, i * ROW_HEIGHT + START_HEIGHT);
			label.setSize(LABEL_WIDTH, LABEL_HEIGHT);
			this.getContentPane().add(label);
		}
	}

	private void initFields() {
		for (int i = 0; i < dataPackage.getColumnCount(); i++) {
			boolean isPrimaryKey = dataPackage.isPrimaryKey(i);
			inputFields[i] = this.createInputfield(dataPackage.getColumnName(i), dataPackage.getDataType(i),
					dataPackage.getValue(i), isPrimaryKey);
			inputFields[i].setSize(FIELD_WIDTH, FIELD_HEIGHT);
			inputFields[i].setLocation(FIELD_START_WIDTH, i * ROW_HEIGHT + START_HEIGHT);
			this.getContentPane().add(inputFields[i]);
		}
	}

	private void initButtons(String saveButtonName) {
		JButton cancelButton = new JButton("Cancel");
		JButton saveButton = new JButton(saveButtonName);

		saveButton.setActionCommand(SAVE);
		saveButton.addActionListener(this);
		saveButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		saveButton.setLocation((DIALOG_WIDTH - BUTTON_SPACE) / 2 - BUTTON_WIDTH - 15,
				(dataPackage.getColumnCount() - 1) * ROW_HEIGHT + START_HEIGHT + SPACE_LAST_FIELD_BUTTONS);

		cancelButton.setActionCommand(CANCEL);
		cancelButton.addActionListener(this);
		cancelButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		cancelButton.setLocation((DIALOG_WIDTH + BUTTON_SPACE) / 2 - 15,
				(dataPackage.getColumnCount() - 1) * ROW_HEIGHT + START_HEIGHT + SPACE_LAST_FIELD_BUTTONS);

		this.getContentPane().add(saveButton);
		this.getContentPane().add(cancelButton);
	}

	protected RowPackage getCurrentData() {
		this.updateRowPackage();
		return dataPackage;
	}

	private void updateRowPackage() {
		for (int i = 0; i < inputFields.length; i++) {
			try {
				this.dataPackage.setValue(i, inputFields[i].getValue());
			} catch (NumberFormatException e) {
				this.dataPackage.setValue(i, null);
			}
		}
	}

	protected abstract InputField createInputfield(String columnName, String datatype, Object data,
			boolean isPrimaryKey);

	protected abstract void performSave();

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(CANCEL)) {
			this.dispose();
		} else if (command.equals(SAVE)) {
			this.performSave();
		}
	}
}
