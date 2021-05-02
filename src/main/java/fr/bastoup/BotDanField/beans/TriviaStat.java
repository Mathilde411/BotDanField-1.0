package fr.bastoup.BotDanField.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TriviaStat {
	private String id;
	private long firstAnswer;
	private long lastAnswer;
	private int totalAnswer;
	private int wins;
	private List<Long> respTime;
	
	public TriviaStat(String userId) {
		id = userId;
		firstAnswer = new Date().getTime();
		lastAnswer = new Date().getTime();
		totalAnswer = 0;
		wins = 0;
		respTime = new ArrayList<Long>();
	}
	
	public void addAnswer(Long triviaStart) {
		long time = new Date().getTime();
		lastAnswer = time;
		respTime.add(time - triviaStart);
		totalAnswer++;
	}
	
	public void addWin() {
		if(wins < totalAnswer) {
			wins++;
		}
	}
	
	public long getInterval() {
		return (long) Math.floor((lastAnswer - firstAnswer)/1000);
	}
	
	public float getWinPercentage() {
		return (((float)wins)/((float)totalAnswer))*100f;
	}
	
	public float getAnswerPerMinute() {
		return (float)totalAnswer/((float)getInterval()/60f);
	}
	
	public String getId() {
		return id;
	}
	
	public int getTotalAnswer() {
		return totalAnswer;
	}
	
	public float getAverageResp() {
		return ((float)respTime.stream().mapToLong(Long::longValue).sum()/1000f)/respTime.size();
	}
	
	public float getVariance() {
		float aver = getAverageResp();
		float sum = 0;
		for (Long t : respTime) {
			sum += Math.abs(aver - (float)t/1000f);
		}
		sum /= respTime.size();
		return sum;
	}
}
