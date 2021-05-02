package fr.bastoup.BotDanField.features.game.trivia;

import java.util.List;

import fr.bastoup.BotDanField.beans.Answer;
import fr.bastoup.BotDanField.beans.Config;
import fr.bastoup.BotDanField.beans.Question;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.game.GeneralGameVariables;
import fr.bastoup.BotDanField.features.users.UserManagement;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class TriviaThread extends Thread {
	private JDA jda;
	private Question question;

	public TriviaThread() {
		super("Trivia");
	}

	@Override
	public void run() {
		Config config = ConfigHandler.getConfig();
		Trivia.setStarted(true);
		GeneralGameVariables.GAME_EN_COURS = true;
		EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "Trivia: " + question.getTheme());
		builder.appendDescription(question.getQuestion());
		jda.getGuildById(config.getTargetGuildId()).getTextChannelById(config.getGameChannel())
				.sendMessage(builder.build()).queue();
		Trivia.setAcceptingAnswer(true);
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Trivia.setAcceptingAnswer(false);
		String rep = "";
		Answer win = null;
		Member gagnant = null;
		List<Answer> answers = Trivia.getAnswers();
		for (Answer ans : answers) {
			for (String ans_verif : question.getAnswers()) {
				if (ans.getAnswer().equalsIgnoreCase(ans_verif)) {
					Member mem = jda.getGuildById(config.getTargetGuildId()).getMemberById(ans.getId());
					Trivia.addWin(mem.getUser().getId());
					if (mem != null && win != null && win.getTimestamp() > ans.getTimestamp()) {
						win = ans;
						gagnant = mem;
					} else if(mem != null && win == null) {
						win = ans;
						gagnant = mem;
					}
				}
			}
		}
		for (String str : question.getAnswers()) {
			rep = rep + str + ", ";
		}
		if(gagnant != null) {
			UserManagement.addMoney(gagnant.getUser().getIdLong(), (UserManagement.userHasRole(gagnant.getUser().getIdLong()) ? ConfigHandler.getConfig().getPremiumPrize() : ConfigHandler.getConfig().getTriviaPrize()));
		}
		rep = rep.substring(0, rep.trim().length() - 1);
		EmbedBuilder builder2 = Utils.prepareEmbedBuilder(ThemeEmbed.OK, "Trivia: Résultats");
		builder2.addField("Gagnant",
				win == null ? "Personne n'a trouvé"
						: (gagnant.getEffectiveName()) + " (" + (UserManagement.userHasRole(gagnant.getUser().getIdLong()) ? ConfigHandler.getConfig().getPremiumPrize() : ConfigHandler.getConfig().getTriviaPrize()) + " " + jda.getGuildById(config.getTargetGuildId())
								.getEmoteById(config.getMoneyEmoji()).getAsMention() + ")",
				true);
		builder2.addField("Réponse" + (question.getAnswers().size() > 1 ? "s" : ""), rep, true);
		jda.getGuildById(config.getTargetGuildId()).getTextChannelById(config.getGameChannel())
				.sendMessage(builder2.build()).queue();
		Trivia.clearAnswers();
		GeneralGameVariables.GAME_EN_COURS = false;
		Trivia.setStarted(false);
	}

	public JDA getJda() {
		return jda;
	}

	public void setJda(JDA jda) {
		this.jda = jda;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
}
