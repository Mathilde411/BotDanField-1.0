package fr.bastoup.BotDanField.dao;

import java.util.List;

import fr.bastoup.BotDanField.beans.CustomCommand;

public interface CustomCommandDAO {

	List<CustomCommand> getAll() throws DAOException;

	void delete(int id) throws DAOException;

	CustomCommand getById(int id) throws DAOException;

	CustomCommand get(String command) throws DAOException;

	void updateRoles(CustomCommand cmd) throws DAOException;

	CustomCommand add(CustomCommand cmd) throws DAOException;
	
}
