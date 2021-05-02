package fr.bastoup.BotDanField.dao;

import java.util.List;

import fr.bastoup.BotDanField.beans.User;

public interface UserDAO {
	void add(User user) throws DAOException;
	void update(User user) throws DAOException;
	User get(long id) throws DAOException;
	List<User> getAll() throws DAOException;
	List<User> getTimeout() throws DAOException;
}
