package de.herrmannR.easydatabase.util;

import java.util.NoSuchElementException;

public enum Database {
	DERBY_LOCAL("local/content", "jdbc:derby:../zweieuro/ZweiEuro/databases/local/content");

	private final String name;
	private final String url;

	private Database(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public static Database byName(String name) {
		for (Database database : Database.values()) {
			if (database.getName().equals(name)) {
				return database;
			}
		}
		throw new NoSuchElementException("There does not exist a database named '" + name + "'.");
	}
}
