package fr.bastoup.BotDanField.dao;

import java.util.List;

import fr.bastoup.BotDanField.beans.StoredKey;

public interface StoredKeyDAO {
	StoredKey add(StoredKey key) throws DAOException;
	
	void updateUsed(StoredKey key) throws DAOException;

	StoredKey get(int id) throws DAOException;
	
	StoredKey getKey(String key) throws DAOException;

	List<StoredKey> getAll() throws DAOException;
	
	List<StoredKey> getNotUsed() throws DAOException;
	
	List<StoredKey> getUsageKeys(int usageId) throws DAOException;

	void delete(int id) throws DAOException;
}
