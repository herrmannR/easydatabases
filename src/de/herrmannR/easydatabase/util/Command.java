package de.herrmannR.easydatabase.util;

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
}
