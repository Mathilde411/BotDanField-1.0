package fr.bastoup.BotDanField.features.commands.custom;

import java.util.ArrayList;
import java.util.List;

import fr.bastoup.BotDanField.beans.Argument;
import fr.bastoup.BotDanField.beans.Command;
import fr.bastoup.BotDanField.beans.CustomCommand;
import fr.bastoup.BotDanField.beans.Group;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.features.commands.CommandException;
import fr.bastoup.BotDanField.features.commands.CommandHandler;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.logs.LogHandler;
import fr.bastoup.BotDanField.features.other.Errors;
import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CustomCommandHandler {

	public static void init() {
		try {
			List<CustomCommand> cmds = InternalProperties.getDAOFactory().getCustomCommandDAO().getAll();
			for (CustomCommand cmd : cmds) {
				if (CommandHandler.commandExists(cmd.getCommand())) {
					InternalProperties.getDAOFactory().getCustomCommandDAO().delete(cmd.getId());
					continue;
				}
				switch (CustomCommandAction.toAction(cmd.getAction())) {
				case PHRASE:
					CommandHandler.addCommand(new Command(cmd.getCommand(), null, new Argument[] {}, "custom",
							"Permet de faire dire \"" + cmd.getEffect() + "\" au bot", true));
					break;
				case ROLE:
					long r = Utils.getIdFromRoleEntry(cmd.getEffect());
					if (r < 0)
						InternalProperties.getDAOFactory().getCustomCommandDAO().delete(cmd.getId());
					Guild guild = InternalProperties.getJDA()
							.getGuildById(ConfigHandler.getConfig().getTargetGuildId());
					Role role = guild.getRoleById(r);
					if (role == null)
						InternalProperties.getDAOFactory().getCustomCommandDAO().delete(cmd.getId());
					CommandHandler.addCommand(new Command(cmd.getCommand(), null, new Argument[] {}, "custom",
							"Permet de se donner le rôle " + role.getName(), true));
					break;
				default:
					InternalProperties.getDAOFactory().getCustomCommandDAO().delete(cmd.getId());
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public static void addCustomCommand(String cmd, CustomCommandAction action, String effect)
			throws CustomCommandException {
		if (CommandHandler.commandExists(cmd))
			throw new CustomCommandException("Cette commande exite déjà");
		CustomCommand command = new CustomCommand();
		try {
			switch (action) {
			case PHRASE:
				command.setAction("phrase");
				command.setCommand(cmd.toLowerCase());
				command.setEffect(effect);
				command.setRoles(new ArrayList<String>());
				InternalProperties.getDAOFactory().getCustomCommandDAO().add(command);
				CommandHandler.addCommand(new Command(cmd.toLowerCase(), null, new Argument[] {}, "custom",
						"Permet de faire dire \"" + effect + "\" au bot", true));
				break;
			case ROLE:
				long r = Utils.getIdFromRoleEntry(effect);
				if (r < 0)
					throw new CustomCommandException("Veuillez entrer la mention d'un rôle");
				Role role = InternalProperties.getJDA().getGuildById(ConfigHandler.getConfig().getTargetGuildId())
						.getRoleById(r);
				if (role == null)
					throw new CustomCommandException("Veuillez entrer la mention d'un rôle");
				command.setAction("role");
				command.setCommand(cmd.toLowerCase());
				command.setEffect(Long.toString(r));
				command.setRoles(new ArrayList<String>());
				InternalProperties.getDAOFactory().getCustomCommandDAO().add(command);
				CommandHandler.addCommand(new Command(cmd.toLowerCase(), null, new Argument[] {}, "custom",
						"Permet de se donner le rôle " + role.getName(), true));
				break;
			default:
				throw new CustomCommandException("L'action n'est pas bonne");
			}
		} catch (SecurityException | DAOException e) {
			e.printStackTrace();
		}
	}

	public static void removeCustomCommand(String cmd) throws CustomCommandException {
		try {
			CustomCommand command = InternalProperties.getDAOFactory().getCustomCommandDAO().get(cmd.toLowerCase());
			if (command != null) {
				InternalProperties.getDAOFactory().getCustomCommandDAO().delete(command.getId());
				CommandHandler.deleteCommand(cmd);
			} else {
				throw new CustomCommandException("Cette commande n'existe pas");
			}
		} catch (DAOException e) {
			throw new CustomCommandException("Erreur de BDD");
		}
	}

	public static void addRoles(String cmd, List<String> roles) throws CustomCommandException {
		try {
			CustomCommand command = InternalProperties.getDAOFactory().getCustomCommandDAO().get(cmd.toLowerCase());
			if (command != null) {
				for (String role : roles) {
					command.addRole(role);
				}
				InternalProperties.getDAOFactory().getCustomCommandDAO().updateRoles(command);
			} else {
				throw new CustomCommandException("Cette commande n'existe pas");
			}
		} catch (DAOException e) {
			throw new CustomCommandException("Erreur de BDD");
		}
	}

	public static void removeRoles(String cmd, List<String> roles) throws CustomCommandException {
		try {
			CustomCommand command = InternalProperties.getDAOFactory().getCustomCommandDAO().get(cmd.toLowerCase());
			if (command != null) {
				for (String role : roles) {
					command.removeRole(role);
				}
				InternalProperties.getDAOFactory().getCustomCommandDAO().updateRoles(command);
			} else {
				throw new CustomCommandException("Cette commande n'existe pas");
			}
		} catch (DAOException e) {
			throw new CustomCommandException("Erreur de BDD");
		}
	}

	public static void executeCommand(String cmd, MessageReceivedEvent event) {
		try {
			CustomCommand command = InternalProperties.getDAOFactory().getCustomCommandDAO().get(cmd.toLowerCase());
			if (command == null)
				throw new CommandException("Cette commande n'existe pas");
			if (canUseCommand(command, event.getMember())) {

				EmbedBuilder emb;
				switch (CustomCommandAction.toAction(command.getAction())) {
				case PHRASE:
					emb = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, cmd.toUpperCase());
					emb.setDescription(command.getEffect());
					event.getChannel().sendMessage(emb.build()).queue();
					LogHandler.logInfo("CUSTOM-COMMAND",
							"[" + cmd.toLowerCase() + "][("
									+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
									+ event.getMember().getEffectiveName() + "][EXE] Phrase envoyée",
							"⚙", event.getGuild());
					break;
				case ROLE:
					long r = Utils.getIdFromRoleEntry(command.getEffect());
					if (r < 0)
						throw new CommandException("Le rôle n'existe plus, veuillez supprimer la commande.");
					Role role = InternalProperties.getJDA().getGuildById(ConfigHandler.getConfig().getTargetGuildId())
							.getRoleById(r);
					if (role == null)
						throw new CommandException("Le rôle n'existe plus, veuillez supprimer la commande.");
					emb = Utils.prepareEmbedBuilder(ThemeEmbed.OK, cmd.toUpperCase());
					if (event.getMember().getRoles().contains(role)) {
						event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
						emb.appendDescription(
								event.getMember().getAsMention() + " vous n'avez plus le rôle " + role.getName());
						LogHandler.logInfo("CUSTOM-COMMAND",
								"[" + cmd.toLowerCase() + "][("
										+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
										+ event.getMember().getEffectiveName() + "][EXE] Rôle " + role.getName() + " retiré",
								"⚙", event.getGuild());
					} else {
						event.getGuild().addRoleToMember(event.getMember(), role).queue();
						emb.appendDescription(
								event.getMember().getAsMention() + " vous avez désormais le rôle " + role.getName());
						LogHandler.logInfo("CUSTOM-COMMAND",
								"[" + cmd.toLowerCase() + "][("
										+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
										+ event.getMember().getEffectiveName() + "][EXE] Rôle " + role.getName() + " donné",
								"⚙", event.getGuild());
					}
					event.getChannel().sendMessage(emb.build()).queue();
					break;
				default:
					throw new CommandException("L'action n'est pas bonne");
				}
			} else {
				LogHandler.logWarn("CUSTOM-COMMAND",
						"[" + command.getCommand() + "][("
								+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
								+ event.getMember().getEffectiveName() + "] Accès refusé",
						"⚙", event.getGuild());
				Errors.errorMessage(event.getTextChannel(),
						event.getMember().getAsMention() + " Vous n'avez pas la permission d'utiliser la commande `"
								+ ConfigHandler.getConfig().getCommandChar() + command.getCommand() + "`."
								+ System.getProperty("line.separator")
								+ "Si vous pensez que c'est une erreur, veuillez contacter un administrateur.");
			}
		} catch (SecurityException | DAOException e) {
			throw new CommandException("Problème de BDD", false);
		}
	}

	public static boolean canUseCommand(CustomCommand cmd, Member member) {
		List<Group> grps = PermissionHandler.getGroups(member);
		if (cmd.getRoles().contains("*"))
			return true;
		for (Group group : grps) {
			for (String role : cmd.getRoles()) {
				if (role.equalsIgnoreCase(group.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
