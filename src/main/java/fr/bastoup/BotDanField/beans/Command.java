package fr.bastoup.BotDanField.beans;

import java.lang.reflect.Method;

public class Command {
	
	private String label;
	private Method exec;
	private Argument[] args;
	private String description;
	private String permission;
	private boolean visible;
	
	public Command(String label, Method exec, Argument[] args, String permission, String description, boolean visible) {
		setLabel(label.toLowerCase());
		setExec(exec);
		setArgs(args);
		setDescription(description);
		setPermission(permission);
		setVisible(visible);
	}
	
	public String getLabel() {
		return label;
	}
	
	private void setLabel(String label) {
		this.label = label;
	}
	
	public Method getExec() {
		return exec;
	}
	
	private void setExec(Method exec) {
		this.exec = exec;
	}
	
	public Argument[] getArgs() {
		return args;
	}
	
	private void setArgs(Argument[] args) {
		this.args = args;
	}

	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	
}
