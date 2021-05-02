package fr.bastoup.BotDanField.dao;

import java.util.List;

import fr.bastoup.BotDanField.beans.Warn;

public interface WarnDAO {
	void add(Warn warn) throws DAOException;

	Warn get(int id) throws DAOException;

	List<Warn> getAll() throws DAOException;

	List<Warn> getTimedout() throws DAOException;
	
	List<Warn> getNotTimedout() throws DAOException;
	
	List<Warn> getUserWarns(String id) throws DAOException;
	
	List<Warn> getUserTimedoutWarns(String id) throws DAOException;
	
	List<Warn> getUserNotTimedoutWarns(String id) throws DAOException;

	void delete(int id) throws DAOException;
}
