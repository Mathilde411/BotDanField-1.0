package fr.bastoup.BotDanField;

import java.io.File;

import javax.security.auth.login.LoginException;

import fr.bastoup.BotDanField.beans.Argument;
import fr.bastoup.BotDanField.beans.Command;
import fr.bastoup.BotDanField.dao.DAOConfigurationException;
import fr.bastoup.BotDanField.dao.DAOFactory;
import fr.bastoup.BotDanField.features.commands.CommandHandler;
import fr.bastoup.BotDanField.features.commands.CommandListener;
import fr.bastoup.BotDanField.features.commands.OtherCommands;
import fr.bastoup.BotDanField.features.commands.custom.CustomCommandHandler;
import fr.bastoup.BotDanField.features.commands.custom.CustomCommandsCommands;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.game.bet.Bet;
import fr.bastoup.BotDanField.features.game.trivia.Trivia;
import fr.bastoup.BotDanField.features.game.trivia.TriviaListener;
import fr.bastoup.BotDanField.features.keys.KeyCommands;
import fr.bastoup.BotDanField.features.keys.KeyManagement;
import fr.bastoup.BotDanField.features.logs.LogHandler;
import fr.bastoup.BotDanField.features.logs.LogListener;
import fr.bastoup.BotDanField.features.moderation.AntiAd;
import fr.bastoup.BotDanField.features.other.Errors;
import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import fr.bastoup.BotDanField.features.users.UserListener;
import fr.bastoup.BotDanField.features.users.UserManagement;
import fr.bastoup.BotDanField.features.users.UserManagementCommands;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotDanField {

	private BotDanField() {
	}

	public static BotDanField newInstance(File cfg, File trivia) {
		ConfigHandler.setConfigFile(cfg);
		Trivia.setTriviaFile(trivia);
		InternalProperties.setBot(new BotDanField());
		return InternalProperties.getBot();
	}

	public static void main(String[] args) {
		BotDanField.newInstance(new File(InternalProperties.getConfigFile()), new File(InternalProperties.getTriviaFile())).startBot();
	}

	public void startBot() {
		try {
			System.out.println("Initiating BotDanField...");
			System.out.print("Config: ");
			ConfigHandler.configBuilder();
			System.out.println("OK");
			System.out.print("Database: ");
			try {
				InternalProperties.setDAOFactory(DAOFactory.getInstance());
				System.out.println("OK");
			} catch (DAOConfigurationException e) {
				System.out.println("ERROR");
				e.printStackTrace();
				return;
			}
			System.out.print("Permissions: ");
			PermissionHandler.Initialize();
			System.out.println("OK");
			System.out.print("Jeux: ");
			Trivia.triviaStetup();
			Trivia.startStatsThread();
			Bet.betStetup();
			System.out.println("OK");
			System.out.print("Bot Instance: ");
			JDA jda = new JDABuilder(AccountType.BOT).setToken(ConfigHandler.getConfig().getToken()).addEventListeners(new LogListener(), new CommandListener(), new Errors(), new UserListener(), new TriviaListener(), new AntiAd()).build().awaitReady();
			InternalProperties.setJDA(jda);
			jda.getPresence().setActivity(Activity.playing("démarrer..."));
			System.out.println("OK");
			System.out.print("Commands: ");
			addCommands();
			CustomCommandHandler.init();
			System.out.println("OK");
			System.out.print("User Management: ");
			Guild guild = jda.getGuildById(ConfigHandler.getConfig().getTargetGuildId());
			if (guild != null) {
				UserManagement.updateUsers(guild);
			} else {
				jda.shutdown();
				System.out.println("ERROR");
				return;
			}
			jda.getPresence().setStatus(OnlineStatus.ONLINE);
			jda.getPresence().setActivity(Activity.listening(ConfigHandler.getConfig().getCommandChar() + "help"));;
			UserManagement.startUserThread();
			KeyManagement.startKeyThread();
			System.out.println("OK");
			System.out.println("BotDanField is initiated !");
			LogHandler.logInfo("START", "Le bot a bien démarré", "🔵", guild);
		} catch (LoginException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
	}

	private void addCommands() {
		Class<?>[] paramTypes = { MessageReceivedEvent.class, String[].class };
		try {
			CommandHandler.addCommand(new Command("help", OtherCommands.class.getDeclaredMethod("help", paramTypes),
					new Argument[] {}, "base", "Montre l'aide sur les commandes du bot.", true));
			CommandHandler.addCommand(new Command("stop", OtherCommands.class.getDeclaredMethod("stop", paramTypes),
					new Argument[] {}, "admin", "Permet de stopper le bot correctement.", true));
			CommandHandler.addCommand(new Command("vote", OtherCommands.class.getDeclaredMethod("vote", paramTypes),
					new Argument[] { new Argument("vote", false) }, "fun",
					"Permet de faire un vote grâce au bot avec ✅  ou ❌ comme réponse par réaction.", true));
			CommandHandler.addCommand(new Command("clear", OtherCommands.class.getDeclaredMethod("clear", paramTypes),
					new Argument[] { new Argument("nombre", true) }, "mod",
					"Permet de supprimer des messages dans un canal texte.", true));
			CommandHandler.addCommand(new Command("liens", OtherCommands.class.getDeclaredMethod("links", paramTypes),
					new Argument[] {}, "base", "Permet d'afficher les liens vers les réseaux sociaux de Dan.", true));
			CommandHandler
					.addCommand(new Command("nlot", OtherCommands.class.getDeclaredMethod("nouvLoterie", paramTypes),
							new Argument[] { new Argument("message", true) }, "lotery",
							"Permet d'envoyer une notification comme quoi une nouvelle loterie est disponible", true));
			CommandHandler
					.addCommand(new Command("id", UserManagementCommands.class.getDeclaredMethod("id", paramTypes),
							new Argument[] { new Argument("mention", false) }, "mod",
							"Permet d'obtenir des informations sur un utilisateur", true));
			CommandHandler.addCommand(
					new Command("addmoney", UserManagementCommands.class.getDeclaredMethod("addMoney", paramTypes),
							new Argument[] { new Argument("mention", false), new Argument("quantité", false) }, "money",
							"Permet d'ajouter de l'argent à un utilisateur", true));
			CommandHandler.addCommand(
					new Command("setmoney", UserManagementCommands.class.getDeclaredMethod("setMoney", paramTypes),
							new Argument[] { new Argument("mention", false), new Argument("quantité", false) }, "money",
							"Permet de modifier l'argent d'un utilisateur", true));
			CommandHandler
					.addCommand(new Command("warn", UserManagementCommands.class.getDeclaredMethod("warn", paramTypes),
							new Argument[] { new Argument("mention", false), new Argument("raison", false) }, "warn",
							"Permet de donner un avertissement permanent a un utilisateur.", true));
			CommandHandler.addCommand(
					new Command("tempwarn", UserManagementCommands.class.getDeclaredMethod("tempwarn", paramTypes),
							new Argument[] { new Argument("mention", false), new Argument("temps", false),
									new Argument("indicateur", false), new Argument("raison", false) },
							"warn", "Permet de donner un avertissement temporaire a un utilisateur.", true));
			CommandHandler.addCommand(
					new Command("delwarn", UserManagementCommands.class.getDeclaredMethod("delwarn", paramTypes),
							new Argument[] { new Argument("n°", false) }, "warn",
							"Permet de donner supprimer un avertissement.", true));
			CommandHandler.addCommand(
					new Command("seewarns", UserManagementCommands.class.getDeclaredMethod("seewarns", paramTypes),
							new Argument[] { new Argument("mention", false) }, "warn",
							"Permet de voir les avertissements d'un utilisateur", true));
			CommandHandler.addCommand(
					new Command("seewarn", UserManagementCommands.class.getDeclaredMethod("seewarn", paramTypes),
							new Argument[] { new Argument("n°", false) }, "warn",
							"Permet de voir les détails d'un avertissement", true));
			CommandHandler.addCommand(
					new Command("balance", UserManagementCommands.class.getDeclaredMethod("balance", paramTypes),
							new Argument[] {}, "base", "Permet de voir son argent.", true));
			CommandHandler.addCommand(new Command("buyrole",
					UserManagementCommands.class.getDeclaredMethod("buyRole", paramTypes), new Argument[] {}, "base",
					"Permet d'acheter le rôle bonus pendant "
							+ Utils.secondsToLiteral(ConfigHandler.getConfig().getRoleTimeout() * 60) + " pour "
							+ ConfigHandler.getConfig().getRoleCost() + " DanCoins.",
					true));
			CommandHandler.addCommand(new Command("buykey",
					UserManagementCommands.class.getDeclaredMethod("buyKey", paramTypes), new Argument[] {}, "base",
					"Permet d'acheter une clé DanField pour " + ConfigHandler.getConfig().getKeyCost()
							+ " DanCoins tous les "
							+ Utils.secondsToLiteral(ConfigHandler.getConfig().getKeyTimeout() * 60) + ".",
					true));
			CommandHandler.addCommand(
					new Command("giverole", UserManagementCommands.class.getDeclaredMethod("giveRole", paramTypes),
							new Argument[] { new Argument("mention", false) }, "role",
							"Permet de donner rôle bonus à la personne mentionnée.", true));
			CommandHandler.addCommand(new Command("say", OtherCommands.class.getDeclaredMethod("say", paramTypes),
					new Argument[] { new Argument("phrase", false) }, "fun", "Permet de faire dire des phrases au bot.",
					true));
			CommandHandler.addCommand(new Command("trivia", Trivia.class.getDeclaredMethod("trivia", paramTypes),
					new Argument[] {}, "base", "Permet de lancer une partie de trivia pour tenter de gagner "
							+ ConfigHandler.getConfig().getTriviaPrize() + " DanCoins.",
					true));
			CommandHandler.addCommand(new Command("bet", Bet.class.getDeclaredMethod("bet", paramTypes),
					new Argument[] { new Argument("pari", false) }, "base",
					"Permet de lancer une partie de roue de la fortune pour tenter de multiplier sa mise en DanCoins.",
					true));
			CommandHandler.addCommand(new Command("betstats", Bet.class.getDeclaredMethod("betStats", paramTypes),
					new Argument[] {}, "games", "Permet de connaitre la probablité des multiplicateurs.", true));
			CommandHandler.addCommand(new Command("triviamaj", Trivia.class.getDeclaredMethod("triviamaj", paramTypes),
					new Argument[] {}, "games", "Permet de mettre à jour les questions du Trivia.", true));
			CommandHandler.addCommand(new Command("ban", OtherCommands.class.getDeclaredMethod("ban", paramTypes),
					new Argument[] { new Argument("mention", false), new Argument("raison", true) }, "ban",
					"Permet de bannir un utilisateur à parir de son id", true));
			CommandHandler.addCommand(new Command("adbypass", AntiAd.class.getDeclaredMethod("adbypass", paramTypes),
					new Argument[] { new Argument("mention", false) }, "antiad",
					"Permet d'ajouter quelqu'un à la liste de contournement pub.", true));
			CommandHandler
					.addCommand(new Command("adunbypass", AntiAd.class.getDeclaredMethod("adunbypass", paramTypes),
							new Argument[] { new Argument("mention", false) }, "antiad",
							"Permet de retirer quelqu'un de la liste de contournement pub.", true));
			CommandHandler
					.addCommand(new Command("antiadstate", AntiAd.class.getDeclaredMethod("antiadstate", paramTypes),
							new Argument[] { new Argument("état demandé", true) }, "antiad",
							"Permet de savoir l'état de l'anti-pub et de le changer.", true));
			CommandHandler.addCommand(new Command("addrolemoney",
					UserManagementCommands.class.getDeclaredMethod("addRoleMoney", paramTypes),
					new Argument[] { new Argument("role", false), new Argument("montant", false) }, "money",
					"Permet d'ajouter des DanCoins à tout un rôle", true));
			CommandHandler.addCommand(new Command("addrolechannel",
					UserManagementCommands.class.getDeclaredMethod("addRoleChannel", paramTypes),
					new Argument[] { new Argument("role", false), new Argument("channel ID", false) }, "role",
					"Permet d'ajouter un rôle à tout un salon", true));
			CommandHandler.addCommand(new Command("delrolechannel",
					UserManagementCommands.class.getDeclaredMethod("delRoleChannel", paramTypes),
					new Argument[] { new Argument("role", false), new Argument("channel ID", false) }, "role",
					"Permet de retirer un rôle à tout un salon", true));
			CommandHandler.addCommand(
					new Command("delroleall", UserManagementCommands.class.getDeclaredMethod("delRoleAll", paramTypes),
							new Argument[] { new Argument("role", false) }, "role",
							"Permet d'ajouter un rôle à tout le serveur", true));
			CommandHandler
					.addCommand(new Command("members", OtherCommands.class.getDeclaredMethod("members", paramTypes),
							new Argument[] {}, "base", "Permet de voir le nombre de personnes sur le serveur", true));

			CommandHandler.addCommand(new Command("seekeys", KeyCommands.class.getDeclaredMethod("seeKeys", paramTypes),
					new Argument[] { new Argument("mention", false) }, "keys",
					"Permet de voir toutes les clés d'une personne", true));
			CommandHandler.addCommand(new Command("seekey", KeyCommands.class.getDeclaredMethod("seeKey", paramTypes),
					new Argument[] { new Argument("N°", false) }, "keys", "Permet de voir le détail d'une clé", true));
			CommandHandler.addCommand(new Command("seekeybykey",
					KeyCommands.class.getDeclaredMethod("seeKeyByKey", paramTypes),
					new Argument[] { new Argument("clé", false) }, "keys", "Permet de voir le détail d'une clé", true));
			CommandHandler.addCommand(new Command("addkeys", KeyCommands.class.getDeclaredMethod("addKeys", paramTypes),
					new Argument[] { new Argument("Clés", false) }, "keys", "Permet d'ajouter des clés", true));
			CommandHandler.addCommand(new Command("panel", OtherCommands.class.getDeclaredMethod("panel", paramTypes),
					new Argument[] {}, "web", "Permet d'avoir le lien du panel.", true));
			CommandHandler.addCommand(new Command("addcommand",
					CustomCommandsCommands.class.getDeclaredMethod("addCustomCommand", paramTypes), new Argument[] {
							new Argument("label", false), new Argument("action", false), new Argument("effet", false) },
					"admin", "Permet d'ajouter une commande customisée", true));
			CommandHandler.addCommand(new Command("delcommand",
					CustomCommandsCommands.class.getDeclaredMethod("removeCustomCommand", paramTypes),
					new Argument[] { new Argument("label", false) }, "admin",
					"Permet de retirer une commande customisée", true));
			CommandHandler.addCommand(new Command("addcommandroles",
					CustomCommandsCommands.class.getDeclaredMethod("addRoles", paramTypes),
					new Argument[] { new Argument("label", false), new Argument("roles", false) }, "admin",
					"Permet d'ajouter des rôles à une commande customisée", true));
			CommandHandler.addCommand(new Command("delcommandroles",
					CustomCommandsCommands.class.getDeclaredMethod("removeRoles", paramTypes),
					new Argument[] { new Argument("label", false), new Argument("roles", false) }, "admin",
					"Permet de retirer des rôles à une commande customisée", true));
			CommandHandler.addCommand(new Command("commandlist",
					CustomCommandsCommands.class.getDeclaredMethod("commandList", paramTypes), new Argument[] {},
					"admin", "Permet de lister les commandes customisées", true));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void stopBot() {
		JDA jda = InternalProperties.getJDA();
		LogHandler.logInfo("START", "Le bot va s'arrêter", "🔴",
				jda.getGuildById(ConfigHandler.getConfig().getTargetGuildId()));
		UserManagement.stopUserThread();
		Trivia.stopStatsThread();
		jda.shutdown();
	}
}
