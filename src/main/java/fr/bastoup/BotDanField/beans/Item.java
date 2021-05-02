package fr.bastoup.BotDanField.beans;

public class Item {
	private String kind;
	private String etag;
	private YoutubeId id;
	private Snippet snippet;
	
	public String getKind() {
		return kind;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public YoutubeId getId() {
		return id;
	}

	public void setId(YoutubeId id) {
		this.id = id;
	}

	public Snippet getSnippet() {
		return snippet;
	}

	public void setSnippet(Snippet snippet) {
		this.snippet = snippet;
	}
}
