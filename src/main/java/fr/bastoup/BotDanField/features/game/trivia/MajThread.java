package fr.bastoup.BotDanField.features.game.trivia;

public class MajThread extends Thread {
	public MajThread() {
		super("TriviaMaj");
	}
	
	@Override
	public void run() {
		Trivia.triviaStetup();
	}
}
