package fr.bastoup.BotDanField.features.moderation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import fr.bastoup.BotDanField.features.commands.CommandException;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.logs.LogHandler;
import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import fr.bastoup.BotDanField.features.users.UserManagement;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AntiAd extends ListenerAdapter {

	private static final String INVITE_REGEX = "(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/.+([a-z]|[0-9]|[A-Z])";
	private static final String PERM_BYPASS = "bypassantiad";
	private static boolean antiAdOn = true;
	private static List<Long> bypassers = new ArrayList<Long>();
	private static final String[] COMMAND_TRIGGERS_ON =  new String[] {"1", "on", "true"};
	private static final String[] COMMAND_TRIGGERS_OFF =  new String[] {"0", "false", "off"};

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (antiAdOn && Pattern.compile(INVITE_REGEX).matcher(event.getMessage().getContentRaw()).find()
				&& !event.getAuthor().isBot() && !PermissionHandler.hasPermission(event.getMember(), PERM_BYPASS)) {
			if (!bypassers.contains(event.getAuthor().getIdLong())) {
				event.getMessage().delete().queue();
				EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ANTI-PUB");
				builder.setDescription(event.getAuthor().getAsMention()
						+ " la publicité sans accord des modérateurs est interdite sur le serveur, réfère toi au réglement.");
				event.getTextChannel().sendMessage(builder.build()).queue();
				LogHandler.logInfo("ANTI-PUB", "[#" + event.getTextChannel().getName() + "] La publicité de " + event.getMember().getEffectiveName() + " a été supprimée", "🛠", event.getGuild());
				if(Long.valueOf(ConfigHandler.getConfig().getPunishments().get("pub").get("timeout")) <= 0L) {
					UserManagement.autoPermWarnUser(event.getAuthor().getId(), ConfigHandler.getConfig().getPunishments().get("pub").get("reason"), event.getGuild());
				} else {
					UserManagement.autoWarnUser(event.getAuthor().getId(), ConfigHandler.getConfig().getPunishments().get("pub").get("reason"), Long.valueOf(ConfigHandler.getConfig().getPunishments().get("pub").get("timeout")), event.getGuild());
				}
			} else {
				bypassers.remove(event.getAuthor().getIdLong());
				LogHandler.logInfo("ANTI-PUB", "[#" + event.getTextChannel().getName() + "] " + event.getMember().getEffectiveName() + " a contourné l'anti-pub", "🛠", event.getGuild());
			}
		}
		super.onMessageReceived(event);
	}
	
	public static String adbypass(MessageReceivedEvent event, String[] args) {
		Long id = -1L;
		
		if (args != null && args.length > 0) {
			id = Utils.getIdFromMentionEntry(args[0]);
			if (id == -1) {
				throw new CommandException("Vous devez utiliser la mention ou l'id de la personne à viser");
			}
		} else {
			throw new CommandException("La commande a un argument manquant");
		}
		
		if(!bypassers.contains(id)) {
			bypassers.add(id);
			return event.getMember().getEffectiveName() + " a bien été ajouté à la liste des contournements pub";
		} else {
			throw new CommandException(event.getMember().getEffectiveName() + " est déjà dans la liste des contournements pub", false);
		}
	}
	
	public static String adunbypass(MessageReceivedEvent event, String[] args) {
		Long id = -1L;
		
		if (args != null && args.length > 0) {
			id = Utils.getIdFromMentionEntry(args[0]);
			if (id == -1) {
				throw new CommandException("Vous devez utiliser la mention ou l'id de la personne à viser");
			}
		} else {
			throw new CommandException("La commande a un argument manquant");
		}
		
		if(bypassers.contains(id)) {
			bypassers.remove(id);
			return event.getMember().getEffectiveName() + " a bien été retiré de la liste des contournements pub";
		} else {
			throw new CommandException(event.getMember().getEffectiveName() + " n'est pas dans la liste des contournements pub", false);
		}
	}
	
	public static String antiadstate(MessageReceivedEvent event, String[] args) {
		if (args != null && args.length > 0) {
			if(Arrays.asList(COMMAND_TRIGGERS_ON).contains(args[0])) {
				antiAdOn = true;
				return "L'anti-pub a été activé";
			} else if(Arrays.asList(COMMAND_TRIGGERS_OFF).contains(args[0])) {
				antiAdOn = false;
				return "L'anti-pub a été désactivé";
			} else {
				throw new CommandException("Vous devez utiliser 'on', 'off', '1', '0', 'true', ou 'false'");
			}
		} else {
			EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ANTI-PUB");
			builder.setDescription((antiAdOn ? "🔵" : "🔴") + " L'anti-pub est " + (antiAdOn ? "activé" : "désactivé"));
			event.getTextChannel().sendMessage(builder.build()).queue();
			return "L'état de l'anti-pub a été envoyé";
		}
	}
}
