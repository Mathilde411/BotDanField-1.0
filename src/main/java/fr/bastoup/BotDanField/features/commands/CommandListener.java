package fr.bastoup.BotDanField.features.commands;

import fr.bastoup.BotDanField.features.config.ConfigHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContentRaw().startsWith(ConfigHandler.getConfig().getCommandChar())) {
			String command = event.getMessage().getContentRaw().substring(1);
			String label = null;
			String[] args = null;
			if (!command.startsWith(" ")) {
				if (command.contains(" ")) {
					int spacePos = command.indexOf(' ');
					label = command.substring(0, spacePos);
					args = command.substring(spacePos + 1).split(" ");
				} else {
					label = command;
				}
				event.getMessage().delete().queue();
				CommandHandler.executeCommand(label, event, args);
			}
		}
	}
}
