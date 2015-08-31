package com.kiandastream.database;

public class ModelUserDatas {

	String songid;
	String songname;
	String songpath;
	String songimagepath;
	String songartistname;
	 


	public String getSongid() {
		return songid;
	}



	public void setSongid(String songid) {
		this.songid = songid;
	}



	public String getSongname() {
		return songname;
	}



	public void setSongname(String songname) {
		this.songname = songname;
	}



	public String getSongpath() {
		return songpath;
	}



	public void setSongpath(String songpath) {
		this.songpath = songpath;
	}



	public String getSongimagepath() {
		return songimagepath;
	}



	public void setSongimagepath(String songimagepath) {
		this.songimagepath = songimagepath;
	}



	public String getSongartistname() {
		return songartistname;
	}



	public void setSongartistname(String songartistname) {
		this.songartistname = songartistname;
	}



	@Override
	public String toString() {
		return "\nModelUserDatas [songid=" + songid + ", songname=" + songname
				+ ", songpath=" + songpath +", songimagepath=" + songimagepath +", songartistname=" + songartistname + "]";
	}

}
