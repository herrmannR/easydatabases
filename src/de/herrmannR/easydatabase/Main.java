package de.herrmannR.easydatabase;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.derby.run.run;

import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.util.Command;

public class Main {

	private static Scanner scanner;

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		System.out.print(" > ");
		do {
			String nextCommand = scanner.next();
			if (isValidCommand(nextCommand)) {
				Optional<Command> match = Stream.of(Command.values()).filter(c -> c.getExpression().equals(nextCommand))
						.findFirst();
				if (!match.isPresent()) {
					System.out.println("ERROR: Unknown command. Type '/help' for info.");
					System.out.print(" > ");
				} else {
					perfomCommand(match.get());
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
			DatabaseView.main(null);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + command);
		}
	}
}
