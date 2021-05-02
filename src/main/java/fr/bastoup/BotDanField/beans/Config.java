package fr.bastoup.BotDanField.beans;

import java.util.List;
import java.util.Map;

public class Config {

	private String token;
	private String clientId;
	private String clientSecret;
	private String websiteURL;
	private String commandChar;
	private String targetGuildId;
	private String logsChannel;
	private String announceChannel;
	private String notifLoterieRole;
	private String notifEventRole;
	private String moneyEmoji;
	private String gameChannel;
	private String keyChannel;
	private String banChannel;
	private String keyRole;
	private String reglement;
	private String bonusRole;
	private String concoursChannel;
	private long roleTimeout;
	private long keyTimeout;
	private int roleCost;
	private int triviaPrize;
	private int premiumPrize;
	private int keyCost;
	private Map<String, String> keysSite;
	private Map<String, Map<String, String>> punishments;
	private Map<String, Integer> triviaStats;
	private Map<String, String> database;
	private Map<String, Map<String, Float>> betReward;
	private Map<String, Long> betLimits;
	private Map<String, String> liens;
	private Map<String, Map<String, String>> groups;
	private Map<String, List<String>> permissions;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCommandChar() {
		return commandChar;
	}

	public void setCommandChar(String commandChar) {
		this.commandChar = commandChar;
	}

	public Map<String, Map<String, String>> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Map<String, String>> groups) {
		this.groups = groups;
	}

	public Map<String, List<String>> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, List<String>> permissions) {
		this.permissions = permissions;
	}

	public Map<String, String> getLiens() {
		return liens;
	}

	public void setLiens(Map<String, String> liens) {
		this.liens = liens;
	}

	public String getTargetGuildId() {
		return targetGuildId;
	}

	public void setTargetGuildId(String targetGuildId) {
		this.targetGuildId = targetGuildId;
	}

	public String getLogsChannel() {
		return logsChannel;
	}

	public void setLogsChannel(String logsChannel) {
		this.logsChannel = logsChannel;
	}

	public String getNotifLoterieRole() {
		return notifLoterieRole;
	}

	public void setNotifLoterieRole(String notifLoterieRole) {
		this.notifLoterieRole = notifLoterieRole;
	}

	public String getAnnounceChannel() {
		return announceChannel;
	}

	public void setAnnounceChannel(String announceChannel) {
		this.announceChannel = announceChannel;
	}

	public Map<String, String> getDatabase() {
		return database;
	}

	public void setDatabase(Map<String, String> database) {
		this.database = database;
	}

	public String getMoneyEmoji() {
		return moneyEmoji;
	}

	public void setMoneyEmoji(String moneyEmoji) {
		this.moneyEmoji = moneyEmoji;
	}

	public String getGameChannel() {
		return gameChannel;
	}

	public void setGameChannel(String gameChannel) {
		this.gameChannel = gameChannel;
	}

	public String getReglement() {
		return reglement;
	}

	public void setReglement(String reglement) {
		this.reglement = reglement;
	}

	public String getBonusRole() {
		return bonusRole;
	}

	public void setBonusRole(String bonusRole) {
		this.bonusRole = bonusRole;
	}

	public long getRoleTimeout() {
		return roleTimeout;
	}

	public void setRoleTimeout(long roleTimeout) {
		this.roleTimeout = roleTimeout;
	}

	public int getRoleCost() {
		return roleCost;
	}

	public void setRoleCost(int roleCost) {
		this.roleCost = roleCost;
	}

	public int getTriviaPrize() {
		return triviaPrize;
	}

	public void setTriviaPrize(int triviaPrize) {
		this.triviaPrize = triviaPrize;
	}

	public String getKeyChannel() {
		return keyChannel;
	}

	public void setKeyChannel(String keyChannel) {
		this.keyChannel = keyChannel;
	}

	public String getKeyRole() {
		return keyRole;
	}

	public void setKeyRole(String keyRole) {
		this.keyRole = keyRole;
	}

	public int getKeyCost() {
		return keyCost;
	}

	public void setKeyCost(int keyCost) {
		this.keyCost = keyCost;
	}

	public String getBanChannel() {
		return banChannel;
	}

	public void setBanChannel(String banChannel) {
		this.banChannel = banChannel;
	}

	public Map<String, Map<String, String>> getPunishments() {
		return punishments;
	}

	public void setPunishments(Map<String, Map<String, String>> punishments) {
		this.punishments = punishments;
	}

	public int getPremiumPrize() {
		return premiumPrize;
	}

	public void setPremiumPrize(int premiumPrize) {
		this.premiumPrize = premiumPrize;
	}

	public String getConcoursChannel() {
		return concoursChannel;
	}

	public void setConcoursChannel(String concoursChannel) {
		this.concoursChannel = concoursChannel;
	}

	public String getNotifEventRole() {
		return notifEventRole;
	}

	public void setNotifEventRole(String notifEventRole) {
		this.notifEventRole = notifEventRole;
	}

	public long getKeyTimeout() {
		return keyTimeout;
	}

	public void setKeyTimeout(long keyTimeout) {
		this.keyTimeout = keyTimeout;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	public Map<String, Integer> getTriviaStats() {
		return triviaStats;
	}

	public void setTriviaStats(Map<String, Integer> triviaStats) {
		this.triviaStats = triviaStats;
	}

	public Map<String, Map<String, Float>> getBetReward() {
		return betReward;
	}

	public void setBetReward(Map<String, Map<String, Float>> betReward) {
		this.betReward = betReward;
	}

	public Map<String, Long> getBetLimits() {
		return betLimits;
	}

	public void setBetLimits(Map<String, Long> betLimits) {
		this.betLimits = betLimits;
	}

}