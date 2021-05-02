package fr.bastoup.BotDanField.features.keys;

import fr.bastoup.BotDanField.beans.SiteError;

public class WebsiteException extends Exception {
	
	private static final long serialVersionUID = 150231588645927562L;
	private SiteError err;

	public WebsiteException(SiteError err) {
		this.err = err;
	}

	public SiteError getError() {
		return err;
	}
}
