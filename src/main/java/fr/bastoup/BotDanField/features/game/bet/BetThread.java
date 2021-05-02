package fr.bastoup.BotDanField.features.game.bet;

import java.util.Map;
import java.util.Random;

import fr.bastoup.BotDanField.beans.Config;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.game.GeneralGameVariables;
import fr.bastoup.BotDanField.features.users.UserManagement;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class BetThread extends Thread {
	private JDA jda;
	private Member mem;
	private long money;

	public BetThread() {
		super("Bet");
	}

	@Override
	public void run() {
		Config config = ConfigHandler.getConfig();
		Bet.setStarted(true);
		GeneralGameVariables.GAME_EN_COURS = true;
		EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "Pari");
		builder.appendDescription("Tirage au sort...");
		builder.setImage("https://media1.tenor.com/images/1442baf5b66b2edf9634add92d999c58/tenor.gif?itemid=11209519");
		Message mss = jda.getGuildById(config.getTargetGuildId()).getTextChannelById(config.getGameChannel())
				.sendMessage(builder.build()).complete();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		float nbr = new Random().nextFloat();
		float sum = 0;
		float choose = 0;
		Map<Float, Float> prb = Bet.getProbas();
		
		for( Float p : prb.keySet() ) {
			choose = p;
			sum += prb.get(p);
			if(sum >= nbr)
				break;
		}
		long score = (long) Math.floor(choose*((float)money));
		long finalScore = score - money;
		UserManagement.addMoney(mem.getUser().getIdLong(), score);
		
		mss.delete().queue();
		
		builder = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "Pari");
		builder.setTitle(mem.getEffectiveName() + ": tirage au sort terminé !");
		builder.addField("Multiplicateur", "x" + choose, true);
		builder.addField("Pari", money + " " + jda.getGuildById(ConfigHandler.getConfig().getTargetGuildId()).getEmoteById(ConfigHandler.getConfig().getMoneyEmoji()).getAsMention(), true);
		builder.addField((finalScore >= 0 ? "Gains" : "Pertes"), Math.abs(finalScore) + " " + jda.getGuildById(ConfigHandler.getConfig().getTargetGuildId()).getEmoteById(ConfigHandler.getConfig().getMoneyEmoji()).getAsMention(), true);
		jda.getGuildById(config.getTargetGuildId()).getTextChannelById(config.getGameChannel())
				.sendMessage(builder.build()).queue();
		GeneralGameVariables.GAME_EN_COURS = false;
		Bet.setStarted(false);
	}

	public JDA getJda() {
		return jda;
	}

	public void setJda(JDA jda) {
		this.jda = jda;
	}

	public Member getMember() {
		return mem;
	}

	public void setMember(Member mem) {
		this.mem = mem;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}
}
