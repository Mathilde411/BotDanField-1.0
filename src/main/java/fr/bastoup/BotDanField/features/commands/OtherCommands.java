package fr.bastoup.BotDanField.features.commands;

import java.util.Map;

import fr.bastoup.BotDanField.beans.Argument;
import fr.bastoup.BotDanField.beans.Command;
import fr.bastoup.BotDanField.beans.CustomCommand;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.features.commands.custom.CustomCommandHandler;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class OtherCommands {

	public static String vote(MessageReceivedEvent event, String[] args) {
		String str = "";

		if (args != null && args.length > 0) {
			str = String.join(" ", args);
		} else {
			throw new CommandException("La commande a un argument manquant");
		}

		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "VOTE");
		build.setDescription(str);
		event.getChannel().sendMessage(build.build()).queue(m -> {
			m.addReaction("✅").queue();
			m.addReaction("❌").queue();
		});
		return "Vote \"" + str + "\" créé";
	}

	public static String members(MessageReceivedEvent event, String[] args) {
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "MEMBERS");
		build.setDescription("👫 Il y a **" + event.getGuild().getMembers().size() + " membres** sur le serveur.");
		event.getChannel().sendMessage(build.build()).queue();
		return "Nombre de membres envoyé !";
	}

	public static String stop(MessageReceivedEvent event, String[] args) {
		InternalProperties.getBot().stopBot();
		return "Le bot va s'arrêter";
	}

	public static String ban(MessageReceivedEvent event, String[] args) {
		Long id = -1L;
		String str = "";
		String name = "";

		if (args != null && args.length > 0) {
			id = Utils.getIdFromMentionEntry(args[0]);
			if (id == -1) {
				throw new CommandException("Vous devez utiliser la mention ou l'id de la personne à viser");
			}
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					str = str + args[i] + (i == args.length - 1 ? "" : " ");
				}
			}
		} else {
			throw new CommandException("La commande a un argument manquant");
		}

		if (event.getGuild().getMemberById(id) != null) {
			name = event.getGuild().getMemberById(id).getEffectiveName();
		} else {
			name = "" + id;
		}
		event.getGuild().ban("" + id, 0, "Bannissement par de " + event.getMember().getEffectiveName()
				+ (str.equalsIgnoreCase("") ? "" : ": " + str)).queue();
		return name + " banni par " + event.getMember().getEffectiveName()
				+ (str.equalsIgnoreCase("") ? "" : ": " + str);
	}

	public static String nouvLoterie(MessageReceivedEvent event, String[] args) {
		String msg = "";
		if (args != null && args.length > 0) {
			msg = String.join(" ", args);
		} else {
			msg = "Une nouvelle loterie a été publiée sur le site !";
		}
		TextChannel chan = event.getGuild().getTextChannelById(ConfigHandler.getConfig().getAnnounceChannel());
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "NOTIF LOTERIES");
		MessageBuilder message = new MessageBuilder();
		build.setDescription(msg);
		message.append(event.getGuild().getRoleById(ConfigHandler.getConfig().getNotifLoterieRole()));
		message.setEmbed(build.build());
		message.sendTo(chan).queue();
		return "Notification \"" + msg + "\" envoyée";
	}

	public static String clear(MessageReceivedEvent event, String[] args) {
		Long nbr;
		if (args == null || args.length < 1) {
			nbr = 100L;
		} else {
			nbr = Utils.toInt(args[0]);
			if (nbr == null || nbr < 1 || nbr > 99) {
				throw new CommandException("La commande accepte uniquement un nombre entier entre 1 et 100");
			}
		}
		event.getTextChannel().getHistory().retrievePast(Integer.parseInt(nbr.toString())).queue(m -> {
			m.forEach(n -> {
				n.delete().queue();
			});
		});
		return nbr + " messages supprimés";
	}

	public static String help(MessageReceivedEvent event, String[] args) {
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "AIDE");
		build.addField("Pour toute aide supplémentaire, demandez aux modérateurs ou dans le bon canal.", "", false);
		for (Command cmd : CommandHandler.getCommands()) {
			if (cmd.getPermission().equalsIgnoreCase("custom")) {
				try {
					CustomCommand command = InternalProperties.getDAOFactory().getCustomCommandDAO()
							.get(cmd.getLabel());
					if (CustomCommandHandler.canUseCommand(command, event.getMember())) {
						String txt = ConfigHandler.getConfig().getCommandChar() + cmd.getLabel();
						for (Argument arg : cmd.getArgs()) {
							txt += " " + ((arg.isOptional()) ? "[" : "<") + arg.getName()
									+ ((arg.isOptional()) ? "]" : ">");
						}
						build.addField(txt, cmd.getDescription(), false);
					}
				} catch (DAOException e) {
					e.printStackTrace();
				}
			} else if (PermissionHandler.hasPermission(event.getMember(), cmd.getPermission()) && cmd.isVisible()) {
				String txt = ConfigHandler.getConfig().getCommandChar() + cmd.getLabel();
				for (Argument arg : cmd.getArgs()) {
					txt += " " + ((arg.isOptional()) ? "[" : "<") + arg.getName() + ((arg.isOptional()) ? "]" : ">");
				}
				build.addField(txt, cmd.getDescription(), false);
			}
		}
		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});
		EmbedBuilder build2 = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "AIDE");
		build2.appendDescription(
				"📬 " + event.getMember().getAsMention() + " l'aide t'a été envoyée en Message Privé !");
		event.getTextChannel().sendMessage(build2.build()).queue();
		return "L'aide a été envoyée";
	}

	public static String say(MessageReceivedEvent event, String[] args) {
		String str = "";
		if (args != null && args.length > 0) {
			str = String.join(" ", args);
		} else {
			throw new CommandException("La commande a un argument manquant");
		}

		event.getChannel().sendMessage(str).queue();

		return "Le bot a dit: \"" + str + "\"";
	}

	public static String links(MessageReceivedEvent event, String[] args) {
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "LIENS");
		Map<String, String> liens = ConfigHandler.getConfig().getLiens();
		for (String key : liens.keySet()) {
			if (liens.containsKey(key)) {
				build.addField(key, liens.get(key), false);
			}
		}
		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});
		EmbedBuilder build2 = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "LIENS");
		build2.appendDescription(
				"📬 " + event.getMember().getAsMention() + " les liens t'ont été envoyés en Message Privé !");
		event.getTextChannel().sendMessage(build2.build()).queue();
		return "Les liens ont été envoyés";
	}

	public static String panel(MessageReceivedEvent event, String[] args) {
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "WEB PANEL");
		build.appendDescription("Le panel web: " + ConfigHandler.getConfig().getWebsiteURL());
		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});
		return "Le lien du panel a été envoyé";
	}
}
