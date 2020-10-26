package de.herrmannR.easydatabase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
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

public class DatabaseManager {

	private static final String GET_ROW_COUNT = "SELECT COUNT(*) FROM ";
	private static final String GET_ID_FROM_TABLE_NAME = "SELECT id FROM meta.table_data WHERE name = ?";
	private static final String GET_TABLE_DESCRIPTION = "SELECT description FROM meta.table_data WHERE name = ?";
	private static final String GET_DEPENDENCIES = "SELECT meta.table_data.name " + "FROM meta.table_dependencies "
			+ "JOIN meta.table_data ON meta.table_data.id = meta.table_dependencies.ref_id "
			+ "WHERE meta.table_dependencies.table_id = ?";
	private static final String GET_ROW_COLUMNS = "SELECT primary_keys FROM meta.table_data WHERE name = ?";

	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String DELETE_FROM = "DELETE FROM ";

	private final PreparedStatement tableDescription;
	private final PreparedStatement tableGetId;
	private final PreparedStatement tableDependencies;
	private final PreparedStatement tableGetPrimaryColumns;

	private static DatabaseManager instance;

	private Connection connection;
	private DatabaseMetaData meta;

	private DatabaseManager(Database database) throws SQLException {
		try {
			Class.forName(database.getDriver());
		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}

		connection = DriverManager.getConnection(database.getUrl(), database.getUser(), database.getPassword());

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
	public String updateRow(String table, RowPackage data, Filter filter) throws SQLException {
		String state = "Data successfull updated";
		PreparedStatement pstmt = connection
				.prepareStatement("UPDATE " + table + data.getSetExpression() + filter.toString());

		int k = 0;
		for (int i = 0; i < data.getColumnCount(); i++) {
			if (!data.isPrimaryKey(i)) {
				pstmt.setObject(k + 1, data.getValue(i));
				k++;
			}
		}

		int startFilter = k;
		for (int i = startFilter; i < startFilter + filter.getLength(); i++) {
			pstmt.setObject(i + 1, filter.getAttribute(i - startFilter));
		}
		pstmt.executeUpdate();
		pstmt.close();
		return state;
	}

	public String insertRow(String table, RowPackage data) throws SQLException {
		String state = "Data successfull inserted";
		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO " + table + data.getInsertExpression());
		int k = 1;
		for (int i = 0; i < data.getColumnCount(); i++) {
			Object obj = data.getValue(i);
			if (obj == null || !obj.equals("DEFAULT")) {
				pstmt.setObject(k, data.getValue(i));
				k++;
			}
		}
		pstmt.executeUpdate();
		pstmt.close();
		return state;
	}

	public String deleteRow(String table, Filter filter) throws SQLException {
		String state = "Row successfully deleted.";
		PreparedStatement pstmt;
		pstmt = connection.prepareStatement(DELETE_FROM + table + filter.toString());
		for (int i = 0; i < filter.getLength(); i++) {
			pstmt.setObject(i + 1, filter.getAttribute(i));
		}
		pstmt.executeUpdate();
		return state;
	}

	public ArrayList<String> getTables(String schema) throws SQLException {
		String[] types = { "TABLE" };
		ResultSet result = meta.getTables(null, schema, null, types);
		ArrayList<String> tables = new ArrayList<String>();
		while (result.next()) {
			tables.add(schema + "." + result.getString(3));
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
			instance = new DatabaseManager(Main.database);
		}
		return instance;
	}

	public static void closeInstance() {
		try {
			instance.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Iterator<Driver> drivers = DriverManager.getDrivers().asIterator();
		while (drivers.hasNext()) {
			System.out.println(drivers.next().getClass().toString());
		}

//		try {
//			Iterator<String> tables = DatabaseManager.getInstance().getTables().iterator();
//			while (tables.hasNext()) {
//				Set<String> res = DatabaseManager.getInstance().getPrimaryCols(tables.next());
//				for (String s : res) {
//					System.out.println(s);
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
}
