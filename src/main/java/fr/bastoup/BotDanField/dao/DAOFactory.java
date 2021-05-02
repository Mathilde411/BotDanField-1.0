package fr.bastoup.BotDanField.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import fr.bastoup.BotDanField.features.config.ConfigHandler;

public class DAOFactory {
	private static final String PROPERTY_URL = "url";
	private static final String PROPERTY_USERNAME = "username";
	private static final String PROPERTY_PASSWORD = "password";

	BoneCP connectionPool = null;

	private DAOFactory(BoneCP pool) {
		this.connectionPool = pool;
	}

	public static DAOFactory getInstance() throws DAOConfigurationException {
		Map<String, String> databaseConfig = ConfigHandler.getConfig().getDatabase();
		String url = databaseConfig.get(PROPERTY_URL)  + "?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		String username = databaseConfig.get(PROPERTY_USERNAME);
		String password = databaseConfig.get(PROPERTY_PASSWORD);
		BoneCP connectionPool = null;
		try {
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(url);
			config.setUsername(username);
			config.setPassword(password);
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(2);
			connectionPool = new BoneCP(config);
		} catch (SQLException e) {
			throw new DAOConfigurationException("Erreur de configuration du pool de connexions.", e);
		}
		DAOFactory instance = new DAOFactory(connectionPool);
		return instance;
	}

	Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}

	public UserDAO getUserDAO() {
		return new UserDAOImpl(this);
	}
	
	public WarnDAO getWarnDAO() {
		return new WarnDAOImpl(this);
	}
	
	public StoredKeyDAO getStoredKeyDAO() {
		return new StoredKeyDAOImpl(this);
	}
	
	public KeyDAO getKeyDAO() {
		return new KeyDAOImpl(this);
	}
	
	public CustomCommandDAO getCustomCommandDAO() {
		return new CustomCommandDAOImpl(this);
	}
}
