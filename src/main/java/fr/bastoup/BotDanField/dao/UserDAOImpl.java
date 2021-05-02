package fr.bastoup.BotDanField.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.Statement;

import fr.bastoup.BotDanField.beans.User;

import static fr.bastoup.BotDanField.dao.DAOUtils.*;

public class UserDAOImpl implements UserDAO {

	private static final String TABLE_NAME = "users";
	
	private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (id, pseudo, money, role_expiery, next_key) VALUES (?, ?, ?, ?, ?)";
	private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
			+ " SET pseudo=?, money=?, role_expiery=?, next_key=? WHERE id=?";
	private static final String SQL_SELECT = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
	private static final String SQL_SELECT_TIMEOUT = "SELECT * FROM " + TABLE_NAME + " WHERE role_expiery < ?";
	private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
	
	private DAOFactory daoFactory;
	
	public UserDAOImpl(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	@Override
	public void add(User user) throws DAOException {
		Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        connection = daoFactory.getConnection();
	        preparedStatement = preparedStatementInit( connection, SQL_INSERT, false, user.getId(), user.getPseudo(), 0, 0, 0 );
	        int status = preparedStatement.executeUpdate();
	        if ( status == 0 ) {
	            throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        closeConn( preparedStatement, connection );
	    }
	}

	@Override
	public void update(User user) throws DAOException {
		Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        connection = daoFactory.getConnection();
	        preparedStatement = preparedStatementInit( connection, SQL_UPDATE, false, user.getPseudo(), user.getMoney(), user.getRoleExpiery(), user.getNextKey() , user.getId() );
	        int status = preparedStatement.executeUpdate();
	        if ( status == 0 ) {
	            throw new DAOException( "Échec de la mis a jour de l'utilisateur, aucune ligne modifiée dans la table." );
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	        closeConn( preparedStatement, connection );
	    }
	}

	@Override
	public User get(long id) throws DAOException {
		Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    User user = null;

	    try {
	        connection = daoFactory.getConnection();
	        preparedStatement = preparedStatementInit( connection, SQL_SELECT, false, id );
	        resultSet = preparedStatement.executeQuery();
	        if ( resultSet.next() ) {
	            user = map( resultSet );
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	    	closeConn(resultSet, preparedStatement, connection);
	    }

	    return user;
	}

	@Override
	public List<User> getAll() throws DAOException {
		Connection connection = null;
	    Statement statement = null;
	    ResultSet resultSet = null;
	    List<User> users = new ArrayList<User>();

	    try {
	        connection = daoFactory.getConnection();
	        statement = connection.createStatement();
	        resultSet = statement.executeQuery(SQL_SELECT_ALL);
	        while ( resultSet.next() ) {
	        	User user = new User();
	    	    user.setId( resultSet.getLong( "id" ) );
	    	    user.setMoney( resultSet.getLong( "money" ) );
	    	    user.setPseudo( resultSet.getString( "pseudo" ) );
	    	    user.setRoleExpiery( resultSet.getLong( "role_expiery" ) );
	    	    user.setNextKey( resultSet.getLong( "next_key" ) );
	    	    users.add(user);
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	    	closeConn(resultSet, statement, connection);
	    }

	    return users;
	}
	
	@Override
	public List<User> getTimeout() throws DAOException {
		Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<User> users = new ArrayList<User>();

	    try {
	        connection = daoFactory.getConnection();
	        preparedStatement = preparedStatementInit( connection, SQL_SELECT_TIMEOUT, false, new Date().getTime() );
	        resultSet = preparedStatement.executeQuery();
	        while ( resultSet.next() ) {
	        	User user = new User();
	    	    user.setId( resultSet.getLong( "id" ) );
	    	    user.setMoney( resultSet.getLong( "money" ) );
	    	    user.setPseudo( resultSet.getString( "pseudo" ) );
	    	    user.setRoleExpiery( resultSet.getLong( "role_expiery" ) );
	    	    user.setNextKey( resultSet.getLong( "next_key" ) );
	    	    users.add(user);
	        }
	    } catch ( SQLException e ) {
	        throw new DAOException( e );
	    } finally {
	    	closeConn(resultSet, preparedStatement, connection);
	    }

	    return users;
	}
	
	private static User map( ResultSet resultSet ) throws SQLException {
		User user = new User();
	    user.setId( resultSet.getLong( "id" ) );
	    user.setMoney( resultSet.getLong( "money" ) );
	    user.setPseudo( resultSet.getString( "pseudo" ) );
	    user.setRoleExpiery( resultSet.getLong( "role_expiery" ) );
	    user.setNextKey( resultSet.getLong( "next_key" ) );
	    return user;
	}

}
