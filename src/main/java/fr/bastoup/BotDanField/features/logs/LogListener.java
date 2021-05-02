package fr.bastoup.BotDanField.features.logs;

import fr.bastoup.BotDanField.features.permissions.PermissionHandler;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LogListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		if(event.getMessage().getContentRaw().contains("addkey"))
			return;
		
		LogHandler.logInfo("message",
				"[#" + event.getTextChannel().getName() + "][("
						+ PermissionHandler.getMainGroup(event.getMember()).getPrefix() + ")"
						+ event.getMember().getEffectiveName() + "] " + event.getMessage().getContentRaw(),
				"💬", event.getGuild());
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		LogHandler.logInfo("server join", " " + event.getMember().getEffectiveName(), "➕", event.getGuild());
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		LogHandler.logInfo("server leave", " " + event.getMember().getEffectiveName(), "➖", event.getGuild());
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		LogHandler.logInfo("voice join",
				"[➕ " + event.getChannelJoined().getName() + "] " + event.getMember().getEffectiveName(), "📞",
				event.getGuild());
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		LogHandler.logInfo("voice leave",
				"[➖ " + event.getChannelLeft().getName() + "] " + event.getMember().getEffectiveName(), "📞",
				event.getGuild());
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		LogHandler.logInfo("voice move", "[" + event.getChannelLeft().getName() + " ➡ " + event.getChannelJoined().getName() + "]  " + event.getMember().getEffectiveName(), "📞", event.getGuild());
	}
}
