package fr.bastoup.BotDanField.features.users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.bastoup.BotDanField.beans.KeyBundle;
import fr.bastoup.BotDanField.beans.User;
import fr.bastoup.BotDanField.beans.Warn;
import fr.bastoup.BotDanField.dao.DAOException;
import fr.bastoup.BotDanField.dao.UserDAO;
import fr.bastoup.BotDanField.features.commands.CommandException;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.features.keys.KeyManagement;
import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.ThemeEmbed;
import fr.bastoup.BotDanField.utils.TimeMarker;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UserManagementCommands {

	public static final String[] TIME_INDICATORS = new String[] { "A", "a", "M", "m", "s", "S", "j", "J", "h", "H",
			"mn", "MN", "Mn", "mN" };

	public static String id(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}
		Long id = Utils.getIdFromMentionEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}
		Long money = UserManagement.getUser(id).getMoney();
		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "ID");
		build.setThumbnail(member.getUser().getAvatarUrl());
		build.addField("ID", member.getUser().getId(), true);
		build.addField("Nom", member.getUser().getName(), true);
		build.addField("Discriminateur", "#" + member.getUser().getDiscriminator(), true);
		build.addField("Surnom", member.getEffectiveName(), true);
		build.addField("Argent",
				money.toString() + " "
						+ event.getGuild().getEmoteById(ConfigHandler.getConfig().getMoneyEmoji()).getAsMention(),
				true);
		build.addField("Averts actifs", Integer.toString(UserManagement.getUserActiveWarns(id.toString()).size()),
				true);
		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});
		return "Identité de " + member.getEffectiveName() + " envoyée";
	}

	public static String warn(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 2) {
			throw new CommandException("La commande a un argument manquant");
		}
		String id = Long.toString(Utils.getIdFromMentionEntry(args[0]));
		if (id == "-1") {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		if (!PermissionHandler.canTarget(event.getMember(), member))
			throw new CommandException("Vous n'avez pas la permission de viser " + member.getEffectiveName());

		String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

		if (UserManagement.permWarnUser(id, event.getAuthor().getId(), reason, event.getGuild())) {
			return member.getEffectiveName() + " a bien été averti";
		} else {
			throw new CommandException("L'avertissement n'a pas pu être donné", false);
		}
	}

	public static String tempwarn(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 4) {
			throw new CommandException("La commande a un argument manquant");
		}
		String id = Long.toString(Utils.getIdFromMentionEntry(args[0]));
		if (id == "-1") {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		if (!PermissionHandler.canTarget(event.getMember(), member))
			throw new CommandException("Vous n'avez pas la permission de viser " + member.getEffectiveName());

		long time = 0L;
		try {
			time = Long.parseLong(args[1]);
		} catch (NumberFormatException e) {
			throw new CommandException("Le temps doit être un nombre entier");
		}

		if (!Arrays.asList(TIME_INDICATORS).contains(args[2])) {
			throw new CommandException("L'indicateur doit être mn, h, j, s, m ou a");
		}

		switch (args[2].toLowerCase()) {
		case "mn":
			time = time * TimeMarker.MINUTE.getToSecond();
			break;
		case "h":
			time = time * TimeMarker.HOUR.getToSecond();
			break;
		case "j":
			time = time * TimeMarker.DAY.getToSecond();
			break;
		case "s":
			time = time * TimeMarker.WEEK.getToSecond();
			break;
		case "m":
			time = time * TimeMarker.MONTH.getToSecond();
			break;
		case "a":
			time = time * TimeMarker.YEAR.getToSecond();
			break;
		}

		String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

		if (UserManagement.warnUser(id, event.getAuthor().getId(), reason, time, event.getGuild())) {
			return member.getEffectiveName() + " a bien été averti";
		} else {
			throw new CommandException("L'avertissement n'a pas pu être donné", false);
		}
	}

	public static String delwarn(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			throw new CommandException("Le numéro de l'avertissement doit être un nombre");
		}

		Warn warn = UserManagement.getWarn(id);
		if (warn == null) {
			throw new CommandException("Cet avertissement n'existe pas");
		}

		if (warn.getWarnAuthorID() != null) {
			Member warned = event.getGuild().getMemberById(warn.getWarnedUserID());
			if (warned != null && !PermissionHandler.canTarget(event.getMember(), warned)) {
				throw new CommandException("Vous ne pouvez pas supprimer cet avertissement");
			}
		}

		if (UserManagement.deleteWarn(id, event.getMember())) {
			return "L'avertissement a bien été supprimé";
		} else {
			throw new CommandException("L'avertissement n'a pas pu être supprimé", false);
		}
	}

	public static String seewarns(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}
		Long id = Utils.getIdFromMentionEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "WARN");
		build.setTitle("Avertissements de " + member.getEffectiveName());
		for (Warn warn : UserManagement.getUserActiveWarns(id.toString())) {
			build.addField("\\🅰 N°" + warn.getId(), warn.getWarnReason(), false);
		}

		for (Warn warn : UserManagement.getUserInactiveWarns(id.toString())) {
			build.addField("N°" + warn.getId(), warn.getWarnReason(), false);
		}

		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});

		return "Avertissements de " + member.getEffectiveName() + " envoyés";
	}

	public static String seewarn(MessageReceivedEvent event, String[] args) {
		long time = new Date().getTime();
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}

		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			throw new CommandException("L'argument doit être un nombre");
		}

		Warn warn = UserManagement.getWarn(id);
		if (warn == null)
			throw new CommandException("L'id doit correspondre à un avertissement");

		User warned = UserManagement.getUser(Long.parseLong(warn.getWarnedUserID()));
		User warner = warn.getWarnAuthorID() == null ? null
				: UserManagement.getUser(Long.parseLong(warn.getWarnAuthorID()));
		Member mwarned = event.getGuild().getMemberById(warn.getWarnedUserID());
		Member mwarner = warn.getWarnAuthorID() == null ? null : event.getGuild().getMemberById(warn.getWarnAuthorID());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "WARN");
		build.setTitle("Avertissement n°" + Integer.toString(warn.getId()));
		build.addField("Averti", mwarned != null ? mwarned.getEffectiveName() : warned.getPseudo(), true);
		build.addField("Auteur", warn.getWarnAuthorID() == null ? "Automatique"
				: (mwarner != null ? mwarner.getEffectiveName() : warner.getPseudo()), true);
		build.addField("Actif",
				warn.getWarnTimeout() == null || warn.getWarnTimeout() + warn.getWarnDate() > time ? "Oui" : "Non",
				true);
		build.addField("Date", df.format(new Date(warn.getWarnDate())), true);
		build.addField("Durée",
				warn.getWarnTimeout() != null ? Utils.secondsToLiteral(warn.getWarnTimeout() / 1000) : "Permanent",
				true);
		build.addField("Temps restant",
				warn.getWarnTimeout() != null && warn.getWarnTimeout() + warn.getWarnDate() > time
						? Utils.secondsToLiteral(((warn.getWarnTimeout() + warn.getWarnDate()) - time) / 1000)
						: "Aucun",
				true);
		build.addField("Raison", warn.getWarnReason(), false);
		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});

		return "Le résumé de l'avertissement n°" + id + " a bien été envoyé";
	}

	public static String buyRole(MessageReceivedEvent event, String[] args) {
		UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
		User user;
		try {
			user = userDAO.get(event.getAuthor().getIdLong());
			if (user == null) {
				throw new CommandException("Erreur l'utilisateur n'exite pas en base", false);
			}
			if (user.getMoney() >= ConfigHandler.getConfig().getRoleCost()) {
				boolean ok = UserManagement.addBonusRole(event.getAuthor().getIdLong(), event.getGuild(),
						ConfigHandler.getConfig().getRoleTimeout());
				if (!ok) {
					throw new CommandException(" tu possède déjà le rôle !");
				}
				UserManagement.addMoney(event.getAuthor().getIdLong(), -1 * ConfigHandler.getConfig().getRoleCost());
			} else {
				throw new CommandException(" tu ne possède pas assez de DanCoins.");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return event.getMember().getEffectiveName() + " a acheté le rôle bonus";
	}

	public static String buyKey(MessageReceivedEvent event, String[] args) {
		UserDAO userDAO = InternalProperties.getDAOFactory().getUserDAO();
		User user;
		int n = 0;
		try {
			user = userDAO.get(event.getAuthor().getIdLong());
			if (user == null) {
				throw new CommandException("Erreur l'utilisateur n'exite pas en base", false);
			}
			
			Long to = UserManagement.getKeyTimeout(user.getId());
			if(to != null && to > 0) {
				throw new CommandException(" tu as déjà acheté une clé il y a trop peu de temps !" + System.getProperty("line.separator") + "Tu pourras acheter ta prochaine clé dans " + Utils.secondsToLiteral(to / 1000) + ".", true);
			}
			
			if (user.getMoney() >= ConfigHandler.getConfig().getKeyCost()) {
				UserManagement.addMoney(event.getAuthor().getIdLong(), -1 * ConfigHandler.getConfig().getKeyCost());
				KeyBundle key = KeyManagement.redeemKey(event.getGuild(), event.getAuthor());

				if (key.getKey() == null) {
					TextChannel chan = event.getGuild().getTextChannelById(ConfigHandler.getConfig().getKeyChannel());
					EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ACHAT CLÉ");
					MessageBuilder message = new MessageBuilder();
					build.setDescription(event.getAuthor().getAsMention() + " a acheté une clé DanField ! (Il faut lui envoyer et cocher ✅ après !)");
					message.append(event.getGuild().getRoleById(ConfigHandler.getConfig().getKeyRole()));
					message.setEmbed(build.build());
					chan.sendMessage(message.build()).queue(m -> {
						m.addReaction("✅").queue();
					});
				} else {
					n = key.getKeyInfo().getId();
					event.getAuthor().openPrivateChannel().queue(c -> {
						EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ACHAT CLÉ");
						build.setTitle("Voilà ta clé DanField");
						build.setDescription("Si tu as un problème pour l'utiliser regarde la FAQ et le topic Steam avant de contacter un Administrateur.");
						build.addField("N°", Integer.toString(key.getKeyInfo().getId()), true);
						build.addField("Clé", key.getKey(), true);
						c.sendMessage(build.build()).queue();
					});
					
					event.getGuild().getTextChannelById(ConfigHandler.getConfig().getKeyChannel());
					TextChannel chan = event.getGuild().getTextChannelById(ConfigHandler.getConfig().getKeyChannel());
					EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ACHAT CLÉ");
					build.setDescription("Une clé a été envoyée à " + event.getMember().getEffectiveName() + ".");
					build.addField("N°", Integer.toString(key.getKeyInfo().getId()), true);
					build.addField("User ID", event.getAuthor().getId(), true);
					chan.sendMessage(build.build()).queue();
				}
				
				UserManagement.setKeyTimeout(user.getId(), ConfigHandler.getConfig().getKeyTimeout());
				EmbedBuilder build2 = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "ACHAT CLÉ");
				build2.setDescription(event.getAuthor().getAsMention()
						+ " ta clé a bien été demandée ! Elle devrait t'être envoyée en Message Privé sous peu.");
				event.getTextChannel().sendMessage(build2.build()).queue();
			} else {
				throw new CommandException(" tu ne possède pas assez de DanCoins.");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return event.getMember().getEffectiveName() + " a acheté une clé" + (n == 0 ? "" : " (N° " + n + ")");
	}

	public static String giveRole(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}
		Long id = Utils.getIdFromMentionEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		boolean ok = UserManagement.addBonusRole(member.getUser().getIdLong(), event.getGuild(),
				ConfigHandler.getConfig().getRoleTimeout());
		if (!ok) {
			throw new CommandException(member.getEffectiveName() + " possède déjà le rôle", false);
		}
		return member.getEffectiveName() + " a reçu le rôle bonus";
	}

	public static String addMoney(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 2) {
			throw new CommandException("La commande a un argument manquant");
		}

		Long id = Utils.getIdFromMentionEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Long quantity = Utils.toInt(args[1]);

		if (quantity == null) {
			throw new CommandException("La quantité d'argent doit être un nombre");
		}

		UserManagement.addMoney(member.getUser().getIdLong(), quantity);
		return quantity + " DanCoins donnés à " + member.getEffectiveName();
	}

	public static String setMoney(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 2) {
			throw new CommandException("La commande a un argument manquant");
		}
		Long id = Utils.getIdFromMentionEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Member member = event.getGuild().getMemberById(id);

		if (member == null) {
			throw new CommandException("Vous devez utiliser la mention de la personne à viser");
		}

		Long quantity = Utils.toInt(args[1]);

		if (quantity == null) {
			throw new CommandException("La quantité d'argent doit être un nombre");
		}

		UserManagement.setMoney(member.getUser().getIdLong(), quantity);
		return "L'argent total de " + member.getEffectiveName() + " a été mis à " + quantity + " DanCoins";
	}

	public static String balance(MessageReceivedEvent event, String[] args) {
		Long money = UserManagement.getUser(event.getMember().getUser().getIdLong()).getMoney();

		EmbedBuilder build = Utils.prepareEmbedBuilder(ThemeEmbed.QUESTION, "MONEY");
		build.setTitle("Vous possédez:");
		build.setDescription(
				money + " " + event.getGuild().getEmoteById(ConfigHandler.getConfig().getMoneyEmoji()).getAsMention());
		event.getMember().getUser().openPrivateChannel().queue(c -> {
			c.sendMessage(build.build()).queue();
		});

		EmbedBuilder build2 = Utils.prepareEmbedBuilder(ThemeEmbed.NOTIF, "MONEY");
		build2.appendDescription(
				"📬 " + event.getMember().getAsMention() + " ton porte-monnaie t'a été envoyé en Message Privé !");
		event.getTextChannel().sendMessage(build2.build()).queue();
		return "Envoi du porte-monnaie";
	}

	public static String addRoleMoney(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 2) {
			throw new CommandException("La commande a un argument manquant");
		}

		Long id = Utils.getIdFromRoleEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Role role = event.getGuild().getRoleById(id);

		if (role == null) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Long quantity = Utils.toInt(args[1]);

		if (quantity == null) {
			throw new CommandException("La quantité d'argent doit être un nombre");
		}

		List<Member> memlist = event.getGuild().getMembersWithRoles(role);

		for (Member member : memlist) {
			UserManagement.addMoney(member.getUser().getIdLong(), quantity);
		}

		return quantity + " DanCoins donnés à " + memlist.size() + " utilisateurs.";
	}

	public static String addRoleChannel(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 2) {
			throw new CommandException("La commande a un argument manquant");
		}

		Long id = Utils.getIdFromRoleEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Role role = event.getGuild().getRoleById(id);

		if (role == null) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Long chanId = Utils.toInt(args[1]);

		if (chanId == null) {
			throw new CommandException("Vous devez utiliser l'ID du salon");
		}

		VoiceChannel channel = event.getGuild().getVoiceChannelById(chanId);

		if (channel == null) {
			throw new CommandException("Vous devez utiliser l'ID du salon");
		}

		List<Member> memlist = channel.getMembers();
		int mem = 0;

		for (Member member : memlist) {
			if (!member.getRoles().contains(role)) {
				event.getGuild().addRoleToMember(member, role).queue();
				mem++;
			}
		}

		return "Le rôle " + role.getName() + " a été ajouté à " + mem + " utilisateurs.";
	}

	public static String delRoleChannel(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 2) {
			throw new CommandException("La commande a un argument manquant");
		}

		Long id = Utils.getIdFromRoleEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Role role = event.getGuild().getRoleById(id);

		if (role == null) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Long chanId = Utils.toInt(args[1]);

		if (chanId == null) {
			throw new CommandException("Vous devez utiliser l'ID du salon");
		}

		VoiceChannel channel = event.getGuild().getVoiceChannelById(chanId);

		if (channel == null) {
			throw new CommandException("Vous devez utiliser l'ID du salon");
		}

		List<Member> memlist = channel.getMembers();
		int mem = 0;

		for (Member member : memlist) {
			if (member.getRoles().contains(role)) {
				event.getGuild().removeRoleFromMember(member, role).queue();
				mem++;
			}
		}

		return "Le rôle " + role.getName() + " a été retiré à " + mem + " utilisateurs.";
	}

	public static String delRoleAll(MessageReceivedEvent event, String[] args) {
		if (args == null || args.length < 1) {
			throw new CommandException("La commande a un argument manquant");
		}

		Long id = Utils.getIdFromRoleEntry(args[0]);
		if (id == -1) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		Role role = event.getGuild().getRoleById(id);

		if (role == null) {
			throw new CommandException("Vous devez utiliser la mention du rôle à viser");
		}

		List<Member> memlist = event.getGuild().getMembersWithRoles(role);

		for (Member member : memlist) {
			event.getGuild().removeRoleFromMember(member, role).queue();
		}

		return "Le rôle " + role.getName() + " a été retiré à " + memlist.size() + " utilisateurs.";
	}
}
