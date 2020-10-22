package de.herrmannR.easydatabase.util;

import java.util.NoSuchElementException;

public enum Command {
	HELP("/help", "Lists all available commands."), EXIT("/exit", "The programm will be terminated."),
	RUN_IJ_TOOL("/ij", "Starts ij-tool."), RUN_TABLE_MANAGER("/tm", "Starts table managment GUI.");

	private final String expression;
	private final String description;

	private Command(String expression, String description) {
		this.expression = expression;
		this.description = description;
	}

	public String getExpression() {
		return expression;
	}

	public String getDescription() {
		return description;
	}

	public static Command byExpr(String expr) {
		for (Command command : Command.values()) {
			if (command.getExpression().equals(expr)) {
				return command;
			}
		}
		throw new NoSuchElementException("The command '" + expr + "' does not exist.");
	}
}
