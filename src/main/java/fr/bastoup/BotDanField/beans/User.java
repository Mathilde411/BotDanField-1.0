package fr.bastoup.BotDanField.beans;

import java.util.List;

import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.utils.InternalProperties;

public class User {
	private long id;
	private String name;
	private long money;
	private Long role_expiery;
	private Long next_key;

	public User(long id, String name, long money) {
		this.setId(id);
		this.setPseudo(name);
		this.setMoney(money);
	}

	public User() {
	}
	
	public List<Warn> getWarns() {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().getUserWarns(Long.toString(id));
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Warn> getActiveWarns() {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().getUserNotTimedoutWarns(Long.toString(id));
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Warn> getInactiveWarns() {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().getUserTimedoutWarns(Long.toString(id));
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPseudo() {
		return name;
	}

	public void setPseudo(String name) {
		this.name = name;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public Long getRoleExpiery() {
		return role_expiery;
	}

	public void setRoleExpiery(Long role_expiery) {
		this.role_expiery = role_expiery;
	}

	public Long getNextKey() {
		return next_key;
	}

	public void setNextKey(Long next_key) {
		this.next_key = next_key;
	}
}
