package fr.bastoup.BotDanField.utils;

import fr.bastoup.BotDanField.BotDanField;
import fr.bastoup.BotDanField.dao.DAOFactory;
import net.dv8tion.jda.api.JDA;

public final class InternalProperties {
	private static final String PROJECT_VERSION = "0.9";
	private static final String CONFIG_FILE = "botConfig.yml";
	private static final String TRIVIA_FILE = "trivia.quiz";
	private static BotDanField BOT = null;
	private static DAOFactory DB_CONNECTION = null;
	private static JDA JDA = null;
	
	public static String getVersion() {
        return PROJECT_VERSION;
    }

	public static DAOFactory getDAOFactory() {
		return DB_CONNECTION;
	}

	public static void setDAOFactory(DAOFactory daoFactory) {
		DB_CONNECTION = daoFactory;
	}

	public static String getConfigFile() {
		return CONFIG_FILE;
	}

	public static String getTriviaFile() {
		return TRIVIA_FILE;
	}

	public static JDA getJDA() {
		return JDA;
	}

	public static void setJDA(JDA jda) {
		JDA = jda;
	}

	public static BotDanField getBot() {
		return BOT;
	}

	public static void setBot(BotDanField bot) {
		BOT = bot;
	}
}
