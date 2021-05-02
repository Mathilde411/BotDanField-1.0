package fr.bastoup.BotDanField.features.users;

import java.util.Date;
import java.util.List;

import fr.bastoup.BotDanField.beans.User;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.dao.UserDAO;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserThread extends Thread {

	private boolean running;

	public UserThread() {
		super("User");
		running = false;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			try {
				List<User> usrs = userDAO.getAll();
				for (User user : usrs) {
					if (user.getRoleExpiery() != null && user.getRoleExpiery() != 0 && user.getRoleExpiery() <= new Date().getTime()) {
						user.setRoleExpiery(null);
						userDAO.update(user);
						Guild guild = InternalProperties.getJDA()
								.getGuildById(ConfigHandler.getConfig().getTargetGuildId());
						Member mbr = guild.getMemberById(user.getId());
						if (mbr != null && mbr.getRoles()
								.contains(guild.getRoleById(ConfigHandler.getConfig().getBonusRole()))) {
							guild.removeRoleFromMember(mbr,
									guild.getRoleById(ConfigHandler.getConfig().getBonusRole())).queue();
						}
					}
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void terminate() {
		if(running)
			running = false;
	}
}
