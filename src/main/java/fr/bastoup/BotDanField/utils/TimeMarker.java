package fr.bastoup.BotDanField.utils;

public enum TimeMarker {
	MINUTE(60L),
	HOUR(3600L),
	DAY(86400L),
	WEEK(604800L),
	MONTH(2592000L),
	YEAR(31536000L);
	
	private Long toSecond;

	private TimeMarker(Long toSecond) {
		this.setToSecond(toSecond);
	}

	public Long getToSecond() {
		return toSecond;
	}
	
	private void setToSecond(Long toSecond) {
		this.toSecond = toSecond;
	}
}
