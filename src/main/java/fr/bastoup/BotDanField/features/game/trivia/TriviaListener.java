package fr.bastoup.BotDanField.features.game.trivia;

import java.util.Date;

import fr.bastoup.BotDanField.beans.Answer;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TriviaListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(Trivia.isAcceptingAnswer() && !event.getAuthor().isBot() && event.getChannel().getId().equalsIgnoreCase(ConfigHandler.getConfig().getGameChannel()) && !containsId(event.getAuthor().getId())) {
			Trivia.addAnswer(event.getMember().getUser().getId(), event.getMessage().getContentRaw(), new Date().getTime());
			Trivia.addStat(event.getMember().getUser().getId());
		}
	}

	private boolean containsId(String id) {
		for (Answer ans : Trivia.getAnswers()) {
			if(ans.getId().equalsIgnoreCase(id)) {
				return true;
			}
		}
		return false;
	}
}
