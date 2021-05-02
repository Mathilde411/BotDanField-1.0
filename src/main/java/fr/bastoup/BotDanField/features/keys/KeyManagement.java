package fr.bastoup.BotDanField.features.keys;

import java.util.List;

import fr.bastoup.BotDanField.beans.Key;
import fr.bastoup.BotDanField.beans.KeyBundle;
import fr.bastoup.BotDanField.beans.StoredKey;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.dao.KeyDAO;
import fr.bastoup.BotDanField.dao.StoredKeyDAO;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.KeyType;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class KeyManagement {

	public static void startKeyThread() {
		throw new NotImplementedException();
	}
	
	public static KeyBundle redeemKey(Guild guild, User user) {
		throw new NotImplementedException();
	}
}
