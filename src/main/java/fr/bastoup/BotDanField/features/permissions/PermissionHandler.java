package fr.bastoup.BotDanField.features.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.bastoup.BotDanField.beans.Group;
import fr.bastoup.BotDanField.features.config.ConfigHandler;
import fr.bastoup.BotDanField.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class PermissionHandler {

	private static List<Group> groups = new ArrayList<Group>();
	private static List<String> defaultPerms = new ArrayList<String>();
	private static Group defaultGroup;

	public static void Initialize() {
		Map<String, Map<String, String>> groupList = ConfigHandler.getConfig().getGroups();
		Map<String, List<String>> perms = ConfigHandler.getConfig().getPermissions();
		for (String g : groupList.keySet()) {
			Map<String, String> fields = groupList.get(g);
			String id = fields.get("id");
			Long rank = Utils.toInt(fields.get("rank"));
			String logPrefix = fields.get("logPrefix");
			List<String> permissions = getPermissions(g, perms, groupList);
			Group group = new Group(g, id, Integer.parseInt(rank.toString()), logPrefix, permissions);
			groups.add(group);
		}
		setDefaultPerms(perms.get("default"));
		setDefaultGroup(new Group("default", "none", 0, "usr", defaultPerms));
	}

	private static List<String> getPermissions(String groupName, Map<String, List<String>> perms,
			Map<String, Map<String, String>> groupList) {
		Map<String, String> group = null;
		if (groupName != "default") {
			group = groupList.get(groupName);
		}
		List<String> permissions;
		if (group != null && group.containsKey("inheritance")) {
			permissions = getPermissions(group.get("inheritance"), perms, groupList);
		} else {
			permissions = new ArrayList<String>();
		}
		if (perms.containsKey(groupName)) {
			List<String> groupPerms = perms.get(groupName);
			for (String p : groupPerms) {
				if (!permissions.contains(p)) {
					permissions.add(p);
				}
			}
		}
		return permissions;
	}
	
	public static boolean canTarget(Member shooter, Member target) {
		return (!target.getUser().isBot() && ((getMainGroup(shooter).getRank() > getMainGroup(target).getRank() && !target.isOwner()) || shooter.isOwner()));
	}
	
	public static boolean hasPermission(Member member, String perm) {
		boolean notDefault = false;
		List<String> permissions = new ArrayList<String>();
		for (Role r : member.getRoles()) {
			for (Group g : groups) {
				if(g.getId().equals(r.getId())) {
					notDefault = true;
					for (String p : g.getPermissions()) {
						if(!permissions.contains(p.toLowerCase())) {
							permissions.add(p.toLowerCase());
						}
					}
				}
			}
		}
		if(!notDefault) {
			permissions = defaultPerms;
		}
		return permissions.contains(perm.toLowerCase()) || permissions.contains("*");
	}
	
	public static Group getMainGroup(Member member) {
		Group group = getDefaultGroup();
		for (Role r : member.getRoles()) {
			for (Group g : groups) {
				if(r.getId().equals(g.getId()) && group.getRank() < g.getRank()) {
					group = g;
				}
			}
		}
		return group;
	}
	
	public static List<Group> getGroups(Member member) {
		List<Group> groupsTmp = new ArrayList<Group>();
		groupsTmp.add(getDefaultGroup());
		for (Role r : member.getRoles()) {
			for (Group g : groups) {
				if(r.getId().equals(g.getId())) {
					groupsTmp.add(g);
				}
			}
		}
		return groupsTmp;
	}

	private static void setDefaultPerms(List<String> defaultPerms) {
		PermissionHandler.defaultPerms = defaultPerms;
	}

	public static Group getDefaultGroup() {
		return defaultGroup;
	}

	private static void setDefaultGroup(Group defaultGroup) {
		PermissionHandler.defaultGroup = defaultGroup;
	}
}
