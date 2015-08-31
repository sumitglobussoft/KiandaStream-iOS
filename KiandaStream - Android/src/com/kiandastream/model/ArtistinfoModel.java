package com.kiandastream.model;

public class ArtistinfoModel 
{
	private String ablum_name;
	private String artist_id;
	private String artist_name;
	private String loved_count;
	private String title;
	private String id;
	private String trending_rank_today;
	private String imageurl;
	private int type;
	private boolean isloved;
	public boolean isIsloved() {
		return isloved;
	}
	public void setIsloved(boolean isloved) {
		this.isloved = isloved;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getAblum_name() {
		return ablum_name;
	}
	public void setAblum_name(String ablum_name) {
		this.ablum_name = ablum_name;
	}
	public String getArtist_id() {
		return artist_id;
	}
	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}
	public String getArtist_name() {
		return artist_name;
	}
	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}
	public String getLoved_count() {
		return loved_count;
	}
	public void setLoved_count(String loved_count) {
		this.loved_count = loved_count;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrending_rank_today() {
		return trending_rank_today;
	}
	public void setTrending_rank_today(String trending_rank_today) {
		this.trending_rank_today = trending_rank_today;
	}
	
	
}
