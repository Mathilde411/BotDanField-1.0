package fr.bastoup.BotDanField.dao;

import java.util.List;

import fr.bastoup.BotDanField.beans.Key;

public interface KeyDAO {
	Key add(Key key) throws DAOException;

	Key get(int id) throws DAOException;

	List<Key> getAll() throws DAOException;
	
	List<Key> getUserKeys(String userId) throws DAOException;
}
