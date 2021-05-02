package fr.bastoup.BotDanField.features.game.trivia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.bastoup.BotDanField.beans.Answer;
import fr.bastoup.BotDanField.beans.Question;
import fr.bastoup.BotDanField.beans.TriviaStat;
import fr.bastoup.BotDanField.features.commands.CommandException;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.game.GeneralGameVariables;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Trivia {
	private static File triviaFile = null;
	private static List<Question> questions = new ArrayList<Question>();
	private static List<Answer> answers = new ArrayList<Answer>();
	private static boolean started = false;
	private static boolean acceptingAnswer = false;
	private static Long questionTimestamp = null;
	private static Map<String, TriviaStat> stats = new HashMap<String, TriviaStat>();
	private static StatsThread statsThread;

	public static void triviaStetup() {
		try {
			List<Question> temp_questions = new ArrayList<Question>();
			FileReader fileReader = new FileReader(getTriviaFile());
			BufferedReader reader = new BufferedReader(fileReader);
			String sCurrentLine = null;
			while ((sCurrentLine = reader.readLine()) != null) {
				String[] cases = sCurrentLine.split("[|]");
				if (cases.length >= 3) {
					Question quest = new Question();
					quest.setTheme(cases[0]);
					quest.setQuestion(cases[1].replace("%n", System.getProperty("line.separator")));
					for (int i = 2; i < cases.length; i++) {
						quest.addAnswer(cases[i]);
					}
					temp_questions.add(quest);
				}
			}
			reader.close();
			questions = temp_questions;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void startStatsThread() {
		if(statsThread == null || !statsThread.isAlive()) {
			statsThread = new StatsThread();
			statsThread.start();
		}
	}
	
	public static void stopStatsThread() {
		if(statsThread != null && statsThread.isAlive()) {
			statsThread.terminate();
		}
	}
	
	public static String triviamaj(MessageReceivedEvent event, String[] args) {
		MajThread maj = new MajThread();
		maj.start();
		return "Les questions ont été mises à jour !";
	}

	public static String trivia(MessageReceivedEvent event, String[] args) {
		if (!started && !GeneralGameVariables.GAME_EN_COURS) {
			if (!event.getChannel().getId().equalsIgnoreCase(ConfigHandler.getConfig().getGameChannel())) {
				EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "Trivia");
				builder.appendDescription(
						event.getMember().getAsMention() + " le trivia se passe dans le salon " + event.getGuild()
								.getTextChannelById(ConfigHandler.getConfig().getGameChannel()).getAsMention());
				event.getChannel().sendMessage(builder.build()).queue();
			}
			TriviaThread trivia = new TriviaThread();
			trivia.setJda(event.getJDA());
			trivia.setQuestion(questions.get(new Random().nextInt(questions.size())));
			trivia.start();
			setQuestionTimestamp(new Date().getTime());
			return "Une partie de Trivia a été lancée";
		} else {
			throw new CommandException("Une partie est déjà en cours", false);
		}
	}

	public static List<Answer> getAnswers() {
		return answers;
	}

	public static void addAnswer(String id, String answer, long timestamp) {
		Answer ans = new Answer();
		ans.setId(id);
		ans.setAnswer(answer);
		ans.setTimestamp(timestamp);
		answers.add(ans);
	}

	public static boolean isStarted() {
		return started;
	}

	public static void setStarted(boolean started) {
		Trivia.started = started;
	}

	public static boolean isAcceptingAnswer() {
		return acceptingAnswer;
	}

	public static void setAcceptingAnswer(boolean acceptingAnswer) {
		Trivia.acceptingAnswer = acceptingAnswer;
	}

	public static void clearAnswers() {
		answers.clear();
	}

	public static File getTriviaFile() {
		return triviaFile;
	}

	public static void setTriviaFile(File triviaFile) {
		Trivia.triviaFile = triviaFile;
	}

	public static Map<String, TriviaStat> getStats() {
		return stats;
	}

	public static void addStat(String id) {
		if (!stats.containsKey(id)) {
			stats.put(id, new TriviaStat(id));
		}
		stats.get(id).addAnswer(questionTimestamp);
	}

	public static void addWin(String id) {
		if (!stats.containsKey(id)) {
			return;
		}
		stats.get(id).addWin();
	}

	public static void clearStats() {
		stats.clear();
	}

	public static Long getQuestionTimestamp() {
		return questionTimestamp;
	}

	public static void setQuestionTimestamp(Long questionTimestamp) {
		Trivia.questionTimestamp = questionTimestamp;
	}
}
