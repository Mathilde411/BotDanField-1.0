package fr.bastoup.BotDanField.beans;

public class Argument {
	
	private String name;
	private boolean optional;
	
	public Argument(String name, boolean optional) {
		setName(name);
		setOptional(optional);
	}
	
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	private void setOptional(boolean optional) {
		this.optional = optional;
	}
}
