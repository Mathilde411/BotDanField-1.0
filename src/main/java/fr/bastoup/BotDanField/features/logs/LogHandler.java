package fr.bastoup.BotDanField.features.logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.bastoup.BotDanField.features.config.ConfigHandler;
import net.dv8tion.jda.api.entities.Guild;

public class LogHandler {
	public static void log(LogLevel level, String dom, String log, String smiley, Guild guild) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		guild.getTextChannelById(ConfigHandler.getConfig().getLogsChannel()).sendMessage("`" + smiley + "[" + df.format(date) + "][" + level + "][" + dom.toUpperCase() + "]" + log + "`").queue();
	}

	public static void logInfo(String dom, String log, String smiley, Guild guild) {
		log(LogLevel.INFO, dom, log, smiley, guild);
	}

	public static void logWarn(String dom, String log, String smiley, Guild guild) {
		log(LogLevel.WARN, dom, log, smiley, guild);
	}

	public static void logCritical(String dom, String log, String smiley, Guild guild) {
		log(LogLevel.CRITICAL, dom, log, smiley, guild);
	}
}
