package fr.bastoup.BotDanField.beans;

import java.util.ArrayList;
import java.util.List;

public class CustomCommand {
	private int id;
	private String command;
	private String action;
	private List<String> roles;
	private String effect;
	
	public CustomCommand() {
		roles = new ArrayList<String>();
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public void addRole(String role) {
		if (!roles.contains(role.toLowerCase())) 
			roles.add(role.toLowerCase());
	}
	
	public void removeRole(String role) {
		if (roles.contains(role.toLowerCase())) 
			roles.remove(role.toLowerCase());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
