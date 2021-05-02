package fr.bastoup.BotDanField.features.game.bet;

import java.util.HashMap;
import java.util.Map;

import fr.bastoup.BotDanField.beans.User;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.dao.UserDAO;
import fr.bastoup.BotDanField.features.commands.CommandException;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.game.GeneralGameVariables;
import fr.bastoup.BotDanField.features.users.UserManagement;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Bet {
	private static Map<Float, Float> probas = null;
	private static boolean started = false;

	public static void betStetup() {
		Map<Float, Float> probaMap = new HashMap<Float, Float>();
		Map<String, Map<String, Float>> rew = ConfigHandler.getConfig().getBetReward();
		int sum = 0;

		for (String k : rew.keySet()) {
			Map<String, Float> tmp = rew.get(k);
			Float chances = tmp.get("chances");
			Float reward = tmp.get("reward");
			probaMap.put(reward, chances);
			sum += chances;
		}

		for (Float r : probaMap.keySet()) {
			Float ch = probaMap.get(r);
			probaMap.put(r, ch / ((float) sum));
		}

		setProbas(probaMap);
	}
	
	public static String betStats(MessageReceivedEvent event, String[] args) {
		EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "Bet Stats");
		for (Float r : probas.keySet()) {
			builder.addField("x" + r.toString(), (Math.floor(probas.get(r)*10000f)/100f) + "%", true);
		}
		event.getAuthor().openPrivateChannel().queue(c -> {
			c.sendMessage(builder.build()).queue();
		});
		return "Stats envoyées";
	}
	
	public static String bet(MessageReceivedEvent event, String[] args) {
		
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}
		
		long pari = 0L;
		try {
			pari = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			throw new CommandException("Le pari doit être un nombre entier positif");
		}
		
		if(pari <= 0L) {
			throw new CommandException("Le pari doit être un nombre entier positif");
		}
		
		if (!started && !GeneralGameVariables.GAME_EN_COURS) {
			Map<String, Long> maxmin = ConfigHandler.getConfig().getBetLimits();
			
			if(pari > maxmin.get("maxi") || pari < maxmin.get("mini")) {
				throw new CommandException("Le pari doit être compris entre "  + maxmin.get("mini") + " et " + maxmin.get("maxi"));
			}
			
			UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
			User user = null;
			try {
				user = userDAO.get(event.getAuthor().getIdLong());
				if (user == null) {
					throw new CommandException("Erreur l'utilisateur n'exite pas en base", false);
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
			
			if (user.getMoney() < pari) {
				throw new CommandException("Vous ne possédez pas assez de DanCoins");
			}
			
			UserManagement.addMoney(event.getAuthor().getIdLong(), -1*pari);
			if (!event.getChannel().getId().equalsIgnoreCase(ConfigHandler.getConfig().getGameChannel())) {
				EmbedBuilder builder = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "Pari");
				builder.appendDescription(event.getMember().getAsMention() + " le pari se passe dans le salon " + event
						.getGuild().getTextChannelById(ConfigHandler.getConfig().getGameChannel()).getAsMention());
				event.getChannel().sendMessage(builder.build()).queue();
			}
			BetThread bet = new BetThread();
			bet.setJda(event.getJDA());
			bet.setMember(event.getMember());
			bet.setMoney(pari);
			bet.start();
			return "Une partie de Roue de la Fortune a été lancée";
		} else {
			throw new CommandException("Une partie est déjà en cours", false);
		}
	}

	public static Map<Float, Float> getProbas() {
		return probas;
	}

	public static void setProbas(Map<Float, Float> probas) {
		Bet.probas = probas;
	}

	public static boolean isStarted() {
		return started;
	}

	public static void setStarted(boolean started) {
		Bet.started = started;
	}

}
