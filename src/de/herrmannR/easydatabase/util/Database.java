package de.herrmannR.easydatabase.util;

public enum Database {
	DERBY_LOCAL("org.apache.derby.client.ClientAutoloadedDriver", "local/content",
			"jdbc:derby:../zweieuro/ZweiEuro/databases/local/content"),
	POSTGRES_ONLINE_TEST("org.postgresql.Driver", "rlyjdmjo",
			"jdbc:postgresql://rogue.db.elephantsql.com:5432/rlyjdmjo", "rlyjdmjo", "ACOnvHmvxxZCKqcb4hbmXr6GdySCymAK");

	private final String driver;
	private final String name;
	private final String url;
	private final String user;
	private final String password;

	private Database(String driver, String name, String url) {
		this(driver, name, url, "", "");
	}

	private Database(String driver, String name, String url, String user, String password) {
		this.driver = driver;
		this.name = name;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

//	public static Database byName(String name) {
//		for (Database database : Database.values()) {
//			if (database.getName().equals(name)) {
//				return database;
//			}
//		}
//		throw new NoSuchElementException("There does not exist a database named '" + name + "'.");
//	}
}
