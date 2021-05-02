package fr.bastoup.BotDanField.features.other;

import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Errors extends ListenerAdapter {

	public static void errorMessage(TextChannel chan, String message) {
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.ERROR, "ERREUR");
		build.setDescription(message);
		chan.sendMessage(build.build()).queue(m -> {
			m.addReaction("🚫").queue();
		});
	}
}
