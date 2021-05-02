package fr.bastoup.BotDanField.beans;

import java.util.List;

public class Group {
	private String name;
	private String id;
	private int rank;
	private String prefix;
	private List<String> permissions;
	
	public Group(String name, String id, int rank, String prefix, List<String> permissions) {
		setName(name);
		setId(id);
		setPrefix(prefix);
		setRank(rank);
		setPermissions(permissions);
	}
	
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	private void setId(String id) {
		this.id = id;
	}
	
	public int getRank() {
		return rank;
	}
	
	private void setRank(int rank) {
		this.rank = rank;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	private void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	private void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
}
