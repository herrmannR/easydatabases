package de.herrmannR.easydatabase.GUI.dialogs;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.InputField;

public class AddRowDialog extends RowDialog {

	private static final long serialVersionUID = 9176943484740649961L;

	public AddRowDialog(DatabaseView parent, String tableName) throws SQLException {
		super(parent, tableName, "Add");
	}

	@Override
	protected InputField createInputfield(String columnName, String dataType, Object data, boolean isPrimaryKey) {
		InputField field;
		if (columnName.equals("ID") || columnName.equals("id")) {
			field = new InputField(InputField.STRING, "DEFAULT", false);
		} else {
			field = new InputField(dataType);
		}
		return field;
	}

	@Override
	protected void performSave() {
		try {
			String result = DatabaseManager.getInstance().insertRow(this.tableName, this.getCurrentData());
			JOptionPane.showMessageDialog(this, result);
			this.dispose();
		} catch (Exception e) {
			if (JOptionPane.showOptionDialog(this, e.toString() + "\nView details in console?", "Insert failed",
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
				e.printStackTrace();
			}
		}
	}
}
