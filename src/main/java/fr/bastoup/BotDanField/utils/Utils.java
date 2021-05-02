package fr.bastoup.BotDanField.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.dv8tion.jda.api.EmbedBuilder;

public class Utils {
	public static Long toInt(String nbr) {
		try {
			return Long.valueOf(nbr);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	public static String decodeGoogleText(String txt) {
		String res = "";
		String tmp = txt;
		while (tmp.contains("&#") && tmp.contains(";")) {
			int i1 = tmp.indexOf("&#");
			int i2 = tmp.indexOf(";");
			try {
				String s = tmp.substring(i1 + 2, i2);
				char c = (char) Integer.parseInt(s);
				res += tmp.substring(0, i1);
				res += c;
				tmp = tmp.substring(i2 + 1);
			} catch (NumberFormatException e) {
				res += tmp.substring(0, i2 + 1);
				tmp = tmp.substring(i2 + 1);
			}
		}

		return res + tmp;
	}

	public static String getZuluFormatedTime(Date dt) {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
		df1.setTimeZone(TimeZone.getTimeZone("UTC"));
		df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df1.format(dt) + "T" + df2.format(dt) + "Z";
	}

	public static String getZuluFormatedTime() {
		return getZuluFormatedTime(new Date());
	}

	public static long getIdFromMention(String mention) {
		String transform;
		if (mention.startsWith("<@!") && mention.endsWith(">")) {
			transform = mention.substring(3, mention.length() - 1);
		} else if (mention.startsWith("<@") && mention.endsWith(">")) {
			transform = mention.substring(2, mention.length() - 1);
		} else {
			return -1;
		}

		try {
			return Long.valueOf(transform);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static long getIdFromRoleMention(String mention) {
		if (!(mention.startsWith("<@&") && mention.endsWith(">"))) {
			return -1;
		}
		String transform = mention.substring(3, mention.length() - 1);
		try {
			return Long.valueOf(transform);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static long getIdFromMentionEntry(String entry) {
		Long id = toInt(entry);
		if (id == null) {
			id = getIdFromMention(entry);
		}
		return id;
	}

	public static long getIdFromRoleEntry(String entry) {
		Long id = toInt(entry);
		if (id == null) {
			id = getIdFromRoleMention(entry);
		}
		return id;
	}

	public static EmbedBuilder prepareEmbedBuilder(ThemeEmbed theme, String title) {
		EmbedBuilder build = new EmbedBuilder();
		build.setColor(theme.getColor());
		build.setFooter("Bot DanField | V." + InternalProperties.getVersion(),
				"https://cdn.discordapp.com/attachments/283717313423867925/397051490855944192/unnamed.png");
		build.setAuthor(title.toUpperCase(), "http://keys.danfield.fr", theme.toString());
		return build;
	}

	public static String secondsToLiteral(long time) {
		long timeToSub = time;

		int years = 0;
		int month = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		if (timeToSub >= 31536000) {
			while (timeToSub >= 31536000) {
				timeToSub = timeToSub - 31536000;
				years++;
			}
		}

		if (timeToSub >= 2592000) {
			while (timeToSub >= 2592000) {
				timeToSub = timeToSub - 2592000;
				month++;
			}
		}

		if (timeToSub >= 86400) {
			while (timeToSub >= 86400) {
				timeToSub = timeToSub - 86400;
				days++;
			}
		}

		if (timeToSub >= 3600) {
			while (timeToSub >= 3600) {
				timeToSub = timeToSub - 3600;
				hours++;
			}
		}

		if (timeToSub >= 60) {
			while (timeToSub >= 60) {
				timeToSub = timeToSub - 60;
				minutes++;
			}
		}

		seconds = (int) timeToSub;

		String txt = "";

		if (years > 0) {
			txt += (years > 0 ? (years + " an" + (years > 1 ? "s" : "")) : "");
			txt += (month > 0 ? " " + (month + " mois") : "");
		} else if (month > 0) {
			txt += (month > 0 ? (month + " mois") : "");
			txt += (days > 0 ? " " + (days + " jour" + (days > 1 ? "s" : "")) : "");
		} else if (days > 0) {
			txt += (days > 0 ? (days + " jour" + (days > 1 ? "s" : "")) : "");
			txt += (hours > 0 ? " " + (hours + " heure" + (hours > 1 ? "s" : "")) : "");
		} else if (hours > 0) {
			txt += (hours > 0 ? (hours + " heure" + (hours > 1 ? "s" : "")) : "");
			txt += (minutes > 0 ? " " + (minutes + " minute" + (minutes > 1 ? "s" : "")) : "");
		} else if (minutes > 0) {
			txt += (minutes > 0 ? (minutes + " minute" + (minutes > 1 ? "s" : "")) : "");
		} else {
			txt += (seconds > 0 ? (seconds + " seconde" + (seconds > 1 ? "s" : "")) : "");
		}

		return txt;
	}
}
