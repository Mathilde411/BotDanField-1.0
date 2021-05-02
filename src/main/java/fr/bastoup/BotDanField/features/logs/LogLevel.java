package fr.bastoup.BotDanField.features.logs;

public enum LogLevel {
	INFO("INFO"),
	CRITICAL("CRIT"),
	WARN("WARN");
	
	private String level;

	private LogLevel(String level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return level;
	}
}
