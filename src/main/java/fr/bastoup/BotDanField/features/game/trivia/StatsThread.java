package fr.bastoup.BotDanField.features.game.trivia;

import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class StatsThread extends Thread {
	private boolean running;
	
	public StatsThread() {
		this.running = false;
	}

	@Override
	public void run() {
		running = true;
		while(running) {
			Integer percLimit = ConfigHandler.getConfig().getTriviaStats().get("winSwitch");
			Integer messLimit = ConfigHandler.getConfig().getTriviaStats().get("messageSwitch");
			Integer cooldown = ConfigHandler.getConfig().getTriviaStats().get("threadTimer");
			
			Trivia.getStats().forEach((k,s) -> {
				if(messLimit <= s.getTotalAnswer() && percLimit <= s.getWinPercentage()) {
					Member mem = InternalProperties.getJDA().getGuildById(ConfigHandler.getConfig().getTargetGuildId()).getMemberById(k);
					EmbedBuilder embed = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ANTI-CHEAT");
					embed.setDescription((mem != null ? mem.getEffectiveName() : k) + " est suspecté de triche au Trivia sur les dernières " + cooldown + " minutes !");
					embed.addField("Réponses", Integer.toString(s.getTotalAnswer()), true);
					embed.addField("Ratio", String.format("%.2f", s.getWinPercentage()) + "%", true);
					embed.addField("Vitesse Moyenne", String.format("%.2f", s.getAnswerPerMinute()) + " rep/min", true);
					embed.addField("Temps de Réponse Moyen", String.format("%.1f", s.getAverageResp()) + " secondes", true);
					embed.addField("Variance du Temps de Réponse", String.format("%.1f", s.getVariance()) + " secondes", true);
					TextChannel chan = InternalProperties.getJDA().getGuildById(ConfigHandler.getConfig().getTargetGuildId()).getTextChannelById(ConfigHandler.getConfig().getBanChannel());
					chan.sendMessage(embed.build()).queue();
				}
			});
			Trivia.clearStats();
			
			try {
				Thread.sleep(cooldown * 60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void terminate() {
		if(running)
			running = false;
	}
}
