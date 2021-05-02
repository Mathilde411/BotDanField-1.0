package fr.bastoup.BotDanField.features.users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.bastoup.BotDanField.beans.User;
import fr.bastoup.BotDanField.beans.Warn;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.dao.UserDAO;
import fr.bastoup.BotDanField.dao.WarnDAO;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.logs.LogHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserManagement {

	private static UserThread userThread = null;

	public static void updateUsers(Guild guild) {
		UserDAO userDAO;
		userDAO = InternalProperties.getDAOFactory().getUserDAO();
		for (Member i : guild.getMembers()) {
			try {
				addToDB(i.getUser(), userDAO);
			} catch (DAOException e) {
				System.err.println("Error while creating user " + i.getEffectiveName() + " (" + i.getUser().getId()
						+ ") in Database");
				e.printStackTrace();
			}
		}
	}

	public static void addToDB(net.dv8tion.jda.api.entities.User user, UserDAO userDAO) throws DAOException {
		if (!user.isBot() && userDAO.get(user.getIdLong()) == null) {
			userDAO.add(new User(user.getIdLong(), user.getName(), 0));
		}
	}

	public static User getUser(long id) {
		User user = null;
		try {
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			user = userDAO.get(id);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return user;
	}

	public static boolean addBonusRole(long id, Guild guild, long timeout) {
		Member mbr = guild.getMemberById(id);
		if (mbr != null && !mbr.getRoles().contains(guild.getRoleById(ConfigHandler.getConfig().getBonusRole()))) {
			addRoleTimeout(id, timeout);
			guild.addRoleToMember(mbr, guild.getRoleById(ConfigHandler.getConfig().getBonusRole())).queue();
			return true;
		}
		return false;
	}

	public static boolean userHasRole(long id) {
		UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
		try {
			User usr = userDAO.get(id);
			if (usr.getRoleExpiery() == null || usr.getRoleExpiery() <= 0) {
				return false;
			} else {
				return true;
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void addRoleTimeout(long id, long timeout) {
		try {
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			User user = userDAO.get(id);
			if (user == null) {
				return;
			}
			long role_expiery = (timeout * 60000);
			user.setRoleExpiery((new Date().getTime() + role_expiery));
			userDAO.update(user);
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public static void setKeyTimeout(long id, long timeout) {
		try {
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			User user = userDAO.get(id);
			if (user == null) {
				return;
			}
			long next_key = (timeout * 60000);
			user.setNextKey(new Date().getTime() + next_key);
			userDAO.update(user);
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public static Long getKeyTimeout(long id) {
		try {
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			User user = userDAO.get(id);
			if (user == null) {
				return null;
			}
			long next_key = user.getNextKey() - new Date().getTime();
			return (next_key > 0 ? next_key : 0L);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addMoney(long id, long money) {
		try {
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			User user = userDAO.get(id);
			if (user == null) {
				return;
			}
			long usrMoney = user.getMoney() + money;
			if (usrMoney < 0) {
				usrMoney = 0;
			}
			user.setMoney(usrMoney);
			userDAO.update(user);
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public static void setMoney(long id, long money) {
		try {
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			User user = userDAO.get(id);
			if (user == null) {
				return;
			}
			if (money < 0)
				money = 0;
			user.setMoney(money);
			userDAO.update(user);
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public static void startUserThread() {
		if (userThread == null) {
			userThread = new UserThread();
			userThread.start();
		}
	}

	public static void stopUserThread() {
		if (userThread != null) {
			userThread.terminate();
			userThread = null;
		}
	}

	public static boolean warnUser(String warnedId, String warnAuthor, String warnReason, Long warnTimeout,
			Guild guild) {
		Member m = guild.getMemberById(warnedId);
		Member m2 = null;
		if (warnAuthor != null)
			m2 = guild.getMemberById(warnAuthor);
		if (warnedId == null || warnReason == null || m == null || (warnAuthor != null && m2 == null))
			return false;

		WarnDAO warnDAO = InternalProperties.getDAOFactory().getWarnDAO();

		Warn warn = new Warn();
		warn.setWarnedUserID(warnedId);
		warn.setWarnAuthorID(warnAuthor);
		warn.setWarnDate(new Date().getTime());
		warn.setWarnReason(warnReason);
		warn.setWarnTimeout(warnTimeout != null ? warnTimeout * 1000 : 0L);

		try {
			warnDAO.add(warn);
			net.dv8tion.jda.api.entities.User user = m.getUser();
			EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "WARN");
			build.setTitle("Vous avez reçu un avertissement.");
			build.setDescription(
					"Une accumulation d'avertissements entraînera des sanctions allant du mute au bannissement définitif.");
			build.setThumbnail(
					"https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Ambox_warning_orange.svg/2000px-Ambox_warning_orange.svg.png");
			build.addField("N°", Integer.toString(warn.getId()), true);
			build.addField("Durée", warn.getWarnTimeout() == null ? "Permanent" : Utils.secondsToLiteral(warnTimeout),
					true);
			build.addField("Raison", warn.getWarnReason(), false);
			user.openPrivateChannel().queue(c -> {
				c.sendMessage(build.build()).queue();
			});

			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			EmbedBuilder build2 = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "WARN");
			build2.setTitle("Avertissement n°" + Integer.toString(warn.getId()));
			build2.addField("Averti", m.getEffectiveName(), true);
			build2.addField("User ID", m.getUser().getId(), true);
			build2.addField("Auteur", warn.getWarnAuthorID() == null ? "Automatique" : m2.getEffectiveName(), true);
			build2.addField("Actifs",
					Integer.toString(UserManagement.getUserActiveWarns(warn.getWarnedUserID()).size()), true);
			build2.addField("Date", df.format(new Date(warn.getWarnDate())), true);
			build2.addField("Durée",
					warn.getWarnTimeout() != null ? Utils.secondsToLiteral(warn.getWarnTimeout() / 1000) : "Permanent",
					true);
			build2.addField("Raison", warn.getWarnReason(), false);
			guild.getTextChannelById(ConfigHandler.getConfig().getBanChannel()).sendMessage(build2.build()).queue();
			LogHandler.logInfo("WARN",
					"[#" + warn.getId() + "] " + m.getEffectiveName() + " a été averti "
							+ (warnTimeout != null ? "pendant " + Utils.secondsToLiteral(warnTimeout) + " " : "")
							+ (warnAuthor != null ? "par " + m2.getEffectiveName() : "automatiquement") + ": "
							+ warn.getWarnReason(),
					"🛠", guild);
			return true;
		} catch (DAOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean permWarnUser(String warnedId, String warnAuthor, String warnReason, Guild guild) {
		return warnUser(warnedId, warnAuthor, warnReason, null, guild);
	}

	public static boolean autoWarnUser(String warnedId, String warnReason, Long warnTimeout, Guild guild) {
		return warnUser(warnedId, null, warnReason, warnTimeout, guild);
	}

	public static boolean autoPermWarnUser(String warnedId, String warnReason, Guild guild) {
		return permWarnUser(warnedId, null, warnReason, guild);
	}

	public static Warn getWarn(int id) {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().get(id);
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean deleteWarn(int id, net.dv8tion.jda.api.entities.Member mbr) {
		try {
			if (InternalProperties.getDAOFactory().getWarnDAO().get(id) == null)
				return false;
			InternalProperties.getDAOFactory().getWarnDAO().delete(id);
			LogHandler.logInfo("WARN",
					"[#" + id + "] Le warn a été supprimé "
							+ (mbr != null ? "par " + mbr.getEffectiveName() : "automatiquement"),
					"🛠", mbr.getGuild());
			return true;
		} catch (DAOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static List<Warn> getUserWarns(String id) {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().getUserWarns(id);
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Warn> getUserActiveWarns(String id) {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().getUserNotTimedoutWarns(id);
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Warn> getUserInactiveWarns(String id) {
		try {
			return InternalProperties.getDAOFactory().getWarnDAO().getUserTimedoutWarns(id);
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
