package com.moxiuyuer.smartphoto.bean;

import java.io.Serializable;

public class SongInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7944318231369849066L;

	private String name;//歌曲名
	
	private String artist;//歌手
	
	private String songUrl;//路径
	
	private int duration;//总时长
	
	private String time;//以00:00显示
	
	//开始剪切时间
	private int startCutTime;
	//停止剪切时间
	private int stopCutTime;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getSongUrl() {
		return songUrl;
	}

	public void setSongUrl(String songUrl) {
		this.songUrl = songUrl;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		
			int m = (int)(duration/60/1000);
			int s = (int)(duration/1000) % 60;
			String mm = m <10 ? String.valueOf("0" + m) : String.valueOf(m);
			String ss = s <10 ? String.valueOf("0" + s) : String.valueOf(s);
			time = mm + ":" + ss;
			
		this.duration = duration;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}


	public int getStartCutTime() {
		return startCutTime;
	}

	public void setStartCutTime(int startCutTime) {
		this.startCutTime = startCutTime;
	}

	public int getStopCutTime() {
		return stopCutTime;
	}

	public void setStopCutTime(int stopCutTime) {
		this.stopCutTime = stopCutTime;
	}
	
}
