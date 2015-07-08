package net.timroden.signedit.data;

public class SignEditDataPackage {
	private final String playerName;
	private final SignFunction function;
	private String line;
	private String[] lines;
	private int lineNum;
	private int amount;

	public SignEditDataPackage(String playerName, SignFunction function) {
		this.playerName = playerName;
		this.function = function;
	}

	public SignEditDataPackage(String playerName, SignFunction function, int amount) {
		this.playerName = playerName;
		this.function = function;
		this.amount = amount;
	}

	public SignEditDataPackage(String playerName, SignFunction function, String[] lines) {
		this.playerName = playerName;
		this.function = function;
		this.lines = lines;
	}

	public SignEditDataPackage(String playerName, String[] lines, int amount, SignFunction function) {
		this.playerName = playerName;
		this.lines = lines;
		this.amount = amount;
		this.function = function;
	}

	public SignEditDataPackage(String playerName, SignFunction function, String line, int lineNum) {
		this.playerName = playerName;
		this.function = function;
		this.line = line;
		this.lineNum = lineNum;
	}

	public SignEditDataPackage(String playerName, SignFunction function, String[] lines, int lineNum) {
		this.playerName = playerName;
		this.function = function;
		this.lines = lines;
		this.lineNum = lineNum;
	}

	public String getPlayerName() {
		return playerName;
	}

	public SignFunction getFunction() {
		return function;
	}

	public String[] getLines() {
		return lines;
	}

	public String getLine() {
		return line;
	}

	public int getLineNum() {
		return lineNum;
	}

	public int getAmount() {
		return amount;
	}
}