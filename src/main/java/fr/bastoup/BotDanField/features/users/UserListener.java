package fr.bastoup.BotDanField.features.users;

import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.dao.UserDAO;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserListener extends ListenerAdapter {
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		UserDAO userDAO;
		messageBienvenue(event.getUser());
		try {
			userDAO = InternalProperties.getDAOFactory().getUserDAO();
			UserManagement.addToDB(event.getUser(), userDAO);
			System.out.println("Users table updated !");
		} catch (DAOException e) {
			System.err.println("Error while creating user " + event.getMember().getEffectiveName() + " ("
					+ event.getMember().getUser().getId() + ") in Database");
			e.printStackTrace();
		}
	}

	private void messageBienvenue(User user) {
		user.openPrivateChannel().queue(c -> {
			c.sendMessage("Salut " + user.getAsMention() + " , Bienvenue sur le Discord de DanField ! Il te faut cependant lire ces quelques règles avant de parler avec les autres:").queue();
			c.sendMessage(ConfigHandler.getConfig().getReglement().replaceAll("%n", System.getProperty("line.separator"))).queue();
		});
	}
}
