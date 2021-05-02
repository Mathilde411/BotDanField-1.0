package fr.bastoup.BotDanField.beans;

import com.google.gson.annotations.SerializedName;

public class SiteResp {
	@SerializedName("user_id")
	private String userId;
	@SerializedName("access_code")
	private String accessCode;
	@SerializedName("iv")
	private String IV;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getIV() {
		return IV;
	}

	public void setIV(String iV) {
		IV = iV;
	}
}
