package fr.bastoup.BotDanField.features.commands.custom;

public enum CustomCommandAction {
	ROLE("role"), PHRASE("phrase");

	private String action;

	CustomCommandAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return action;
	}

	public static CustomCommandAction toAction(String action) {
		switch (action.toLowerCase()) {
		case "role":
			return CustomCommandAction.ROLE;
		case "phrase":
			return CustomCommandAction.PHRASE;
		default:
			return null;
		}
	}
}
