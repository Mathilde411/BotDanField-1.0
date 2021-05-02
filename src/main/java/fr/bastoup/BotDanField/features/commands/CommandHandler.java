package fr.bastoup.BotDanField.features.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import fr.bastoup.BotDanField.beans.Argument;
import fr.bastoup.BotDanField.beans.Command;
import fr.bastoup.BotDanField.features.commands.custom.CustomCommandHandler;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.logs.LogHandler;
import fr.bastoup.BotDanField.features.other.Errors;
import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandHandler {

	private static List<Command> commands = new ArrayList<Command>();

	public static void addCommand(Command cmd) {
		for (Command command : commands) {
			if (command.getLabel().equalsIgnoreCase(cmd.getLabel())) {
				return;
			}
		}
		commands.add(cmd);
	}

	public static void deleteCommand(String cmd) {
		for (Command command : commands) {
			if (command.getLabel().equalsIgnoreCase(cmd)) {
				commands.remove(command);
				return;
			}
		}
	}

	public static boolean commandExists(String cmd) {
		for (Command command : commands) {
			if (command.getLabel().equalsIgnoreCase(cmd)) {
				return true;
			}
		}
		return false;
	}

	public static void executeCommand(String command, MessageReceivedEvent event, String[] args) {
		for (Command cmd : commands) {
			if (cmd.getLabel().equalsIgnoreCase(command)) {
				Object[] params = { event, args };
				if (cmd.getPermission().equalsIgnoreCase("custom")) {
					try {
						CustomCommandHandler.executeCommand(cmd.getLabel(), event);
					} catch ( CommandException except) {
						if (except.doesShowMessage()) {
							String commandUsage = ConfigHandler.getConfig().getCommandChar() + cmd.getLabel();
							for (Argument arg : cmd.getArgs()) {
								commandUsage += " " + (arg.isOptional() ? "[" : "<") + arg.getName()
										+ (arg.isOptional() ? "]" : ">");
							}
							Errors.errorMessage(event.getTextChannel(),
									event.getMember().getAsMention() + " " + except.getMessage()
											+ System.getProperty("line.separator") + "Utilisation: `" + commandUsage
											+ "`");
						}
						LogHandler.logWarn("CUSTOM-COMMAND",
								"[" + cmd.getLabel() + "][("
										+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
										+ event.getMember().getEffectiveName() + "][ERR] " + except.getMessage(),
								"⚙", event.getGuild());
					} catch( Exception e) {
						e.printStackTrace();
						LogHandler.logCritical("CUSTOM-COMMAND",
								"[" + cmd.getLabel() + "][("
										+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
										+ event.getMember().getEffectiveName() + "] "
										+ e.getClass().getName() + ": "
										+ e.getMessage(),
								"⚠", event.getGuild());
					}
				} else if (PermissionHandler.hasPermission(event.getMember(), cmd.getPermission())) {
					try {
						String ret = (String) cmd.getExec().invoke(null, params);
						LogHandler.logInfo("COMMAND",
								"[" + cmd.getLabel().toLowerCase() + "][("
										+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
										+ event.getMember().getEffectiveName() + "][EXE] " + ret,
								"⚙", event.getGuild());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						if (e.getTargetException() instanceof CommandException) {
							CommandException except = (CommandException) e.getTargetException();
							if (except.doesShowMessage()) {
								String commandUsage = ConfigHandler.getConfig().getCommandChar() + cmd.getLabel();
								for (Argument arg : cmd.getArgs()) {
									commandUsage += " " + (arg.isOptional() ? "[" : "<") + arg.getName()
											+ (arg.isOptional() ? "]" : ">");
								}
								Errors.errorMessage(event.getTextChannel(),
										event.getMember().getAsMention() + " " + except.getMessage()
												+ System.getProperty("line.separator") + "Utilisation: `" + commandUsage
												+ "`");
							}
							LogHandler.logWarn("COMMAND",
									"[" + cmd.getLabel() + "][("
											+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
											+ event.getMember().getEffectiveName() + "][ERR] " + except.getMessage(),
									"⚙", event.getGuild());
						} else {
							e.printStackTrace();
							LogHandler.logCritical("COMMAND",
									"[" + cmd.getLabel() + "][("
											+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
											+ event.getMember().getEffectiveName() + "] "
											+ e.getTargetException().getClass().getName() + ": "
											+ e.getTargetException().getMessage(),
									"⚠", event.getGuild());
						}
					}
				} else {
					LogHandler.logWarn("COMMAND",
							"[" + command + "][(" + PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
									+ event.getMember().getEffectiveName() + "] Accès refusé",
							"⚙", event.getGuild());
					Errors.errorMessage(event.getTextChannel(),
							event.getMember().getAsMention() + " Vous n'avez pas la permission d'utiliser la commande `"
									+ ConfigHandler.getConfig().getCommandChar() + command.toLowerCase() + "`."
									+ System.getProperty("line.separator")
									+ "Si vous pensez que c'est une erreur, veuillez contacter un administrateur.");
				}
			}

		}
	}

	public static List<Command> getCommands() {
		return commands;
	}
}
