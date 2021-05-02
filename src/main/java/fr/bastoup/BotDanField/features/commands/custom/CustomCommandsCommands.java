package fr.bastoup.BotDanField.features.commands.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.bastoup.BotDanField.beans.CustomCommand;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.features.commands.CommandException;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CustomCommandsCommands {

	public static String addCustomCommand(MessageReceivedEvent event, String[] args) {
		if (args.length < 3)
			throw new CommandException("Il manque des arguments à la commande");
		try {
			switch (args[1].toLowerCase()) {
			case "phrase":
				CustomCommandHandler.addCustomCommand(args[0], CustomCommandAction.PHRASE,
						String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
				return "Commande ajoutée";
			case "role":
				CustomCommandHandler.addCustomCommand(args[0], CustomCommandAction.ROLE,
						String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
				return "Commande ajoutée";
			default:
				throw new CommandException("Cette action n'existe pas." + System.lineSeparator()
						+ "Vous devez choisir entre role et phrase.");
			}
		} catch (CustomCommandException e) {
			throw new CommandException(e.getMessage());
		}
	}

	public static String removeCustomCommand(MessageReceivedEvent event, String[] args) {
		if (args.length < 1)
			throw new CommandException("Il manque des arguments à la commande");
		try {
			CustomCommandHandler.removeCustomCommand(args[0]);
			return "Commande retirée";
		} catch (CustomCommandException e) {
			throw new CommandException(e.getMessage());
		}
	}

	public static String addRoles(MessageReceivedEvent event, String[] args) {
		if (args.length < 2)
			throw new CommandException("Il manque des arguments à la commande");
		try {
			CustomCommandHandler.addRoles(args[0], new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length))));
			return "Rôles ajoutés";
		} catch (CustomCommandException e) {
			throw new CommandException(e.getMessage());
		}
	}

	public static String removeRoles(MessageReceivedEvent event, String[] args) {
		if (args.length < 2)
			throw new CommandException("Il manque des arguments à la commande");
		try {
			CustomCommandHandler.removeRoles(args[0], new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length))));
			return "Rôles ajoutés";
		} catch (CustomCommandException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	public static String commandList(MessageReceivedEvent event, String[] args) {
		try {
			List<CustomCommand> commands = InternalProperties.getDAOFactory().getCustomCommandDAO().getAll();
			EmbedBuilder embd = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "Custom Commands");
			for (CustomCommand cmd : commands) {
				embd.addField(ConfigHandler.getConfig().getCommandChar() + cmd.getCommand(), (cmd.getAction().equalsIgnoreCase("role") ? "Role: " + event.getGuild().getRoleById(cmd.getEffect()).getName() : "Phrase: " + cmd.getEffect()) + System.lineSeparator() + "Groupes: " + (cmd.getRoles().isEmpty() ? "Aucun" : String.join(", ", cmd.getRoles())), false);
			}
			event.getAuthor().openPrivateChannel().queue(c -> {
				c.sendMessage(embd.build()).queue();
			});
			return "Résumé envoyé";
		} catch (DAOException e) {
			throw new CommandException(e.getMessage());
		}
	}
}
