package de.herrmannR.easydatabase.GUI.dialogs;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.InputField;
import de.herrmannR.easydatabase.structure.Filter;

public class EditRowDialog extends RowDialog {

	private static final long serialVersionUID = -3087962565055357930L;

	public EditRowDialog(DatabaseView parent, String tableName, Filter primaryKeys) throws SQLException {
		super(parent, tableName, primaryKeys, "Save");
	}

	@Override
	protected InputField createInputfield(String columnName, String dataType, Object data, boolean isPrimaryKey) {
		InputField field = new InputField(dataType, data);
		if (isPrimaryKey) {
			field.setEditable(false);
		}
		return field;
	}

	@Override
	protected void performSave() {
		try {
			String result = DatabaseManager.getInstance(this.database).updateRow(tableName, this.getCurrentData(),
					this.primaryKeys);
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
