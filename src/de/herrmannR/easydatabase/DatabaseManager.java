package de.herrmannR.easydatabase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.herrmannR.easydatabase.structure.DataPackage;
import de.herrmannR.easydatabase.structure.Filter;
import de.herrmannR.easydatabase.structure.RowPackage;
import de.herrmannR.easydatabase.util.Database;
import de.herrmannR.easydatabase.util.NotImplementedException;

public class DatabaseManager {

	private static final String GET_ROW_COUNT = "SELECT COUNT(*) FROM ";
	private static final String GET_ID_FROM_TABLE_NAME = "SELECT id FROM table_data WHERE name = ?";
	private static final String GET_TABLE_DESCRIPTION = "SELECT description FROM table_data WHERE name = ?";
	private static final String GET_DEPENDENCIES = "SELECT table_data.name " + "FROM table_dependencies "
			+ "JOIN table_data ON table_data.id = table_dependencies.ref_id " + "WHERE table_dependencies.table_id = ?";
	private static final String GET_ROW_COLUMNS = "SELECT primary_keys FROM table_data WHERE name = ?";

	private static final String SELECT_FROM = "SELECT * FROM ";

	private final PreparedStatement tableDescription;
	private final PreparedStatement tableGetId;
	private final PreparedStatement tableDependencies;
	private final PreparedStatement tableGetPrimaryColumns;

	private static DatabaseManager instance;

	private Connection connection;
	private DatabaseMetaData meta;

	private DatabaseManager() throws SQLException {
		connection = DriverManager.getConnection(Database.DERBY_LOCAL.getUrl());
		meta = connection.getMetaData();
		tableDescription = connection.prepareStatement(GET_TABLE_DESCRIPTION);
		tableGetId = connection.prepareStatement(GET_ID_FROM_TABLE_NAME);
		tableDependencies = connection.prepareStatement(GET_DEPENDENCIES);
		tableGetPrimaryColumns = connection.prepareStatement(GET_ROW_COLUMNS);
	}

	public DataPackage selectFrom(String table, Filter filter) throws SQLException {
		DataPackage selection = null;
		PreparedStatement pstmt;
		pstmt = connection.prepareStatement(SELECT_FROM + table + filter.toString());
		for (int i = 0; i < filter.getLength(); i++) {
			pstmt.setObject(i + 1, filter.getAttribute(i));
		}
		ResultSet result = pstmt.executeQuery();
		ResultSetMetaData meta = result.getMetaData();
		int cols = meta.getColumnCount();
		selection = new DataPackage(cols, this.getPrimaryCols(table));
		while (result.next()) {
			int rowCount = selection.addRow();
			for (int columnCount = 0; columnCount < cols; columnCount++) {
				selection.setValue(rowCount, columnCount, result.getObject(columnCount + 1));
			}
		}
		for (int i = 0; i < cols; i++) {
			selection.setColumnName(i, meta.getColumnName(i + 1));
			selection.setDataType(i, meta.getColumnClassName(i + 1));
		}
		result.close();
		pstmt.close();
		return selection;
	}

	/**
	 * @param table
	 * @param data
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	public String updateRow(String table, RowPackage rowPackage, Filter filter) throws SQLException {
		String state = "Data successfull updated";
		PreparedStatement pstmt = connection
				.prepareStatement("UPDATE " + table + rowPackage.getSetExpression() + filter.toString());
		for (int i = 0; i < rowPackage.getColumnCount(); i++) {
			pstmt.setObject(i + 1, rowPackage.getValue(i));
		}
		for (int i = rowPackage.getColumnCount() - 1; i < rowPackage.getColumnCount() + filter.getLength(); i++) {
			pstmt.setObject(i + 1, filter.getAttribute(i));
		}
		pstmt.executeUpdate();
		pstmt.close();
		return state;
	}

	public String insertRow(String table, RowPackage data) throws NotImplementedException {
//		String state = "Data successfull inserted.";
//		PreparedStatement pstmt = connection.prepareStatement(INSERT_INTO.get(table));
//		int skipped = 0;
//		for (int i = 0; i < data.getColumnCount(); i++) {
//			Object obj = data.getValue(i);
//			if (obj != null && obj.equals("DEFAULT")) {
//				skipped++;
//			} else {
//				pstmt.setObject(i + 1 - skipped, data.getValue(i));
//			}
//		}
//		pstmt.executeUpdate();
//		pstmt.close();
//		return state;
		throw new NotImplementedException("insertRow(String table, RowPackage data)");
	}

	public ArrayList<String> getTables() throws SQLException {
		ResultSet result = meta.getTables(null, "APP", null, null);
		ArrayList<String> tables = new ArrayList<String>();
		while (result.next()) {
			tables.add(result.getString(3));
		}
		result.close();
		return tables;
	}

	public int getTableId(String table) throws SQLException {
		int id = -1;
		tableGetId.setString(1, table);
		ResultSet result = tableGetId.executeQuery();
		if (result.next()) {
			id = result.getInt(1);
		}
		result.close();
		return id;
	}

	public String getTableDescription(String table) throws SQLException {
		String description = "";
		tableDescription.setString(1, table);
		ResultSet result = tableDescription.executeQuery();
		if (result.next()) {
			description = result.getString(1);
		}
		result.close();
		return description;
	}

	public int getRowCount(String table) throws SQLException {
		int count = 0;
		Statement stmt = this.connection.createStatement();
		ResultSet result = stmt.executeQuery(GET_ROW_COUNT + table);
		if (result.next()) {
			count = result.getInt(1);
		}
		result.close();
		return count;
	}

	public String getTableDependencies(String table) throws SQLException {
		tableDependencies.setInt(1, this.getTableId(table));
		ResultSet result = tableDependencies.executeQuery();
		String dependencies = " ";
		if (result.next()) {
			dependencies += result.getString(1);
			while (result.next()) {
				dependencies += ", " + result.getString(1);
			}
		}
		return dependencies;
	}

	public Set<String> getPrimaryCols(String table) throws SQLException {
		Set<String> primaryCols = null;
		tableGetPrimaryColumns.setString(1, table);
		ResultSet result = tableGetPrimaryColumns.executeQuery();
		if (result.next()) {
			String primaryKeys = result.getString(1);
			primaryCols = new HashSet<>(Arrays.asList(primaryKeys.split("\\.")));
		}
		result.close();
		return primaryCols;
	}

	public DataPackage selectFrom(String table) throws SQLException {
		return this.selectFrom(table, new Filter());
	}

	public static DatabaseManager getInstance() throws SQLException {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	public static void closeInstance() {
		if (instance != null) {
			try {
				instance.connection.close();
				instance = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			Iterator<String> tables = DatabaseManager.getInstance().getTables().iterator();
			while (tables.hasNext()) {
				Set<String> res = DatabaseManager.getInstance().getPrimaryCols(tables.next());
				for (String s : res) {
					System.out.println(s);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}