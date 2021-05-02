package fr.bastoup.BotDanField.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

public class DAOUtils {
	public static PreparedStatement preparedStatementInit( Connection connection, String sql, boolean returnGeneratedKeys, Object... objects ) throws SQLException {
	    PreparedStatement preparedStatement = connection.prepareStatement( sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS );
	    for ( int i = 0; i < objects.length; i++ ) {
	        preparedStatement.setObject( i + 1, objects[i] );
	    }
	    return preparedStatement;
	}
	
	public static void closeConn( ResultSet resultSet ) {
	    if ( resultSet != null ) {
	        try {
	            resultSet.close();
	        } catch ( SQLException e ) {
	            System.out.println( "Échec de la fermeture du ResultSet : " + e.getMessage() );
	        }
	    }
	}

	public static void closeConn( Statement statement ) {
	    if ( statement != null ) {
	        try {
	            statement.close();
	        } catch ( SQLException e ) {
	            System.out.println( "Échec de la fermeture du Statement : " + e.getMessage() );
	        }
	    }
	}

	public static void closeConn( Connection connexion ) {
	    if ( connexion != null ) {
	        try {
	            connexion.close();
	        } catch ( SQLException e ) {
	            System.out.println( "Échec de la fermeture de la connexion : " + e.getMessage() );
	        }
	    }
	}

	public static void closeConn( Statement statement, Connection connexion ) {
		closeConn( statement );
		closeConn( connexion );
	}

	public static void closeConn( ResultSet resultSet, Statement statement, Connection connexion ) {
		closeConn( resultSet );
		closeConn( statement );
		closeConn( connexion );
	}
}
