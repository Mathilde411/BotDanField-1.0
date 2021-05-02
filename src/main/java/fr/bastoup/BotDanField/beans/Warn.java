package fr.bastoup.BotDanField.beans;


import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.InternalProperties;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.entities.Member;

public class Warn {
	private int id;
	private String warnedUser;
	private String warnAuthor;
	private String warnReason;
	private long warnDate;
	private Long warnTimeout;
	
	public Warn() {}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWarnedUserID() {
		return warnedUser;
	}

	public void setWarnedUserID(String warned_user) {
		this.warnedUser = warned_user;
	}
	
	public String getWarnedName() {
		if(InternalProperties.getJDA() != null) {
			net.dv8tion.jda.api.entities.User usr = InternalProperties.getJDA().getUserById(warnedUser);
			if(usr != null) {
				Member mem = InternalProperties.getJDA().getGuildById(ConfigHandler.getConfig().getTargetGuildId()).getMember(usr);
				if(mem != null) {
					return mem.getEffectiveName();
				} else {
					return usr.getName();
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getWarnAuthorID() {
		return warnAuthor;
	}

	public void setWarnAuthorID(String warnAuthor) {
		this.warnAuthor = warnAuthor;
	}
	
	public String getWarnAuthorName() {
		if(warnAuthor == null)
			return "BotDanField";
		if(InternalProperties.getJDA() != null) {
			net.dv8tion.jda.api.entities.User usr = InternalProperties.getJDA().getUserById(warnAuthor);
			if(usr != null) {
				Member mem = InternalProperties.getJDA().getGuildById(ConfigHandler.getConfig().getTargetGuildId()).getMember(usr);
				if(mem != null) {
					return mem.getEffectiveName();
				} else {
					return usr.getName();
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getWarnReason() {
		return warnReason;
	}

	public void setWarnReason(String warnReason) {
		this.warnReason = warnReason;
	}

	public long getWarnDate() {
		return warnDate;
	}

	public void setWarnDate(long warnDate) {
		this.warnDate = warnDate;
	}
	
	public String getWarnDateFormated() {
		DateFormat format = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("FR", "fr"));
		return format.format(new Date(warnDate));
	}
	
	public Long getWarnTimeout() {
		return warnTimeout;
	}

	public void setWarnTimeout(Long warnTimeout) {
		if(warnTimeout != null && warnTimeout <= 0L)
			this.warnTimeout = null;
		else
			this.warnTimeout = warnTimeout;
	}
	
	public String getWarnTimeoutFormated() {
		if(warnTimeout != null && warnTimeout > 0) {
			return Utils.secondsToLiteral(warnTimeout/1000);
		} else {
			return "Permanent";
		}
	}
}
