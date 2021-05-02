package fr.bastoup.BotDanField.utils;

import java.awt.Color;

public enum ThemeEmbed {
	OK("https://cdn.pixabay.com/photo/2013/07/12/12/17/check-145512_960_720.png", new Color(28, 243, 68)),
	QUESTION("https://cdn.pixabay.com/photo/2014/03/24/17/16/question-mark-295272_960_720.png", new Color(249, 122, 37)),
	NOTIF("https://cdn.discordapp.com/attachments/256867226638876672/401874236236365824/exclamation-mark-308416_960_720.png", new Color(249, 122, 37)),
	ERROR("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcReXT-WkJ0xCMRhg20Cb2gQykX8Dov93Vhfo-JeZ7-jVhdSmf8F1g", new Color(255, 31, 23)),
	YTB("https://yt3.ggpht.com/a-/AAuE7mAu_-wIFvVO-HT01aQiwmI4GHd_aEXw3HQ-OA=s900-mo-c-c0xffffffff-rj-k-no", new Color(205,32,31));
	
	private String url;
	private Color color;
	
	ThemeEmbed(String url, Color color) {
		this.url = url;
		this.color = color;
	}
	
	@Override
	public String toString() {
		return url;
	}
	
	public Color getColor() {
		return color;
	}
}
