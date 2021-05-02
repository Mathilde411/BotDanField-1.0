package fr.bastoup.BotDanField.beans;

import java.util.ArrayList;
import java.util.List;

public class Question {
	private String question;
	private String theme;
	private List<String> answers;
	
	public Question() {
		answers = new ArrayList<String>();
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void addAnswer(String answer) {
		answers.add(answer);
	}
}
