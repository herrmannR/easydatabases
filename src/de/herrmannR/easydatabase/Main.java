package de.herrmannR.easydatabase;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.derby.run.run;

import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.util.Command;
import de.herrmannR.easydatabase.util.Database;

public class Main {

	private static Scanner scanner;

	public static Database database;
	public static DatabaseView adminFrame;

	public static void main(String[] args) {
		database = (Database) JOptionPane.showInputDialog(null, "Select the database you want to access.",
				"Database-Selection", JOptionPane.OK_CANCEL_OPTION, null, Database.values(), 0);

		if (database == null) {
			System.exit(0);
		}

		for (String arg : args) {
			String[] splitted = arg.split("-");
			String type = splitted[0];
			String param = splitted[1];
			switch (type) {
			case "command":
				System.out.println(" > " + param);
				if (isValidCommand(param)) {
					try {
						perfomCommand(Command.byExpr(param));
					} catch (NoSuchElementException e) {
						System.out.println("ERROR: Unknown command.");
					}
				} else {
					System.out.println("ERROR: Invalid command.");
				}
				break;
			default:
				System.err.println("Unknown argument type: " + type);
				break;
			}
		}

		scanner = new Scanner(System.in);
		System.out.print(" > ");
		do {
			String nextCommand = scanner.next();
			if (isValidCommand(nextCommand)) {
				try {
					perfomCommand(Command.byExpr(nextCommand));
				} catch (NoSuchElementException e) {
					System.out.println("ERROR: Unknown command. Type '/help' for info.");
				} finally {
					System.out.print(" > ");
				}
			} else {
				System.out.println("ERROR: Invalid command. Type '/help' for info.");
				System.out.print(" > ");
			}
		} while (true);
	}

	private static boolean isValidCommand(String str) {
		if (str.startsWith("/") && str.length() > 1) {
			return true;
		}
		return false;
	}

	private static void perfomCommand(Command command) {
		switch (command) {
		case HELP:
			System.out.println("Type '/' at first, if you want to call a command. Available commands are:");
			for (Command cmd : Command.values()) {
				System.out.println("\t" + cmd.getExpression() + ": " + cmd.getDescription());
			}
			break;
		case EXIT:
			System.exit(0);
			break;
		case RUN_IJ_TOOL:
			String[] ij = { "ij" };
			System.out.println("___________________________________");
			System.out.println("___________Start ij-tool___________");
			System.out.println();
			try {
				run.main(ij);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
			System.out.println("___________Exit ij-tool____________");
			System.out.println("___________________________________");
			System.exit(0);
			break;
		case RUN_TABLE_MANAGER:
			adminFrame = new DatabaseView();
			adminFrame.setVisible(true);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + command);
		}
	}

	public static boolean isMetaTable(String table) {
		return table.equals("TABLE_DATA") || table.equals("TABLE_DEPENDENCIES");
	}
}
