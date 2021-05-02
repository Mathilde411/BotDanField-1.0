package fr.bastoup.BotDanField.features.commands;

public class CommandException extends IllegalArgumentException {
	
	private static final long serialVersionUID = 1L;
	
	private boolean showMessage;

	public CommandException(String msg, boolean showMessage) {
		super(msg);
		this.setShowMessage(showMessage);
	}
	
	public CommandException(String msg) {
		super(msg);
		this.setShowMessage(true);
	}

	public boolean doesShowMessage() {
		return showMessage;
	}

	private void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}
}
