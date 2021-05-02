package fr.bastoup.BotDanField.beans;

public class Ban {
	private int id;
	private String banedUser;
	private String banAuthor;
	private String banReason;
	private long banDate;
	private Long banTimeout;
	
	public Ban() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBanedUser() {
		return banedUser;
	}

	public void setBanedUser(String banedUser) {
		this.banedUser = banedUser;
	}

	public String getBanAuthor() {
		return banAuthor;
	}

	public void setBanAuthor(String banAuthor) {
		this.banAuthor = banAuthor;
	}

	public String getBanReason() {
		return banReason;
	}

	public void setBanReason(String banReason) {
		this.banReason = banReason;
	}

	public long getBanDate() {
		return banDate;
	}

	public void setBanDate(long banDate) {
		this.banDate = banDate;
	}

	public Long getBanTimeout() {
		return banTimeout;
	}

	public void setBanTimeout(Long banTimeout) {
		this.banTimeout = banTimeout;
	}
	
}
