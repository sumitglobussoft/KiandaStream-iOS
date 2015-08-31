package com.kiandastream.model;

public class PlayingSongListmodel 
{
	private String song_name;
	private String song_id;
	private String song_artist;
	private String song_ablum;
	private String song_image;
	private boolean loved;
	
	private String song_artistid;
	private String songurl;
	
	public String getSong_artistid() {
		return song_artistid;
	}
	public void setSong_artistid(String song_artistid) {
		this.song_artistid = song_artistid;
	}
	public String getSongurl() {
		return songurl;
	}
	public void setSongurl(String songurl) {
		this.songurl = songurl;
	}
	public String getSong_name() {
		return song_name;
	}
	public void setSong_name(String song_name) {
		this.song_name = song_name;
	}
	public String getSong_id() {
		return song_id;
	}
	public void setSong_id(String song_id) {
		this.song_id = song_id;
	}
	public String getSong_artist() {
		return song_artist;
	}
	public void setSong_artist(String song_artist) {
		this.song_artist = song_artist;
	}
	public String getSong_ablum() {
		return song_ablum;
	}
	public void setSong_ablum(String song_ablum) {
		this.song_ablum = song_ablum;
	}
	public String getSong_image() {
		return song_image;
	}
	public void setSong_image(String song_image) {
		this.song_image = song_image;
	}
	public boolean isLoved() {
		return loved;
	}
	public void setLoved(boolean loved) {
		this.loved = loved;
	}
	

}
