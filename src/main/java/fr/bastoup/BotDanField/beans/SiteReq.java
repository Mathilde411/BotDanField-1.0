package fr.bastoup.BotDanField.beans;

import com.google.gson.annotations.SerializedName;

public class SiteReq {
	
	@SerializedName("user_name")
	private String username;
	@SerializedName("bot_id")
	private String botId;
	@SerializedName("user_id")
	private String userId;
	private String timestamp;
	private String sign;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
