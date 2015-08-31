package com.kiandastream.musicplayer;

import android.media.MediaPlayer;

public interface UpdateMusicPlayer 
{
	public void updatebufferingstatus(MediaPlayer mp, int percent);
	public void onError(MediaPlayer mp, int what, int extra);
	public void onCompletion(MediaPlayer arg0);
	public void onPreparation(MediaPlayer arg0);
	public void startUpdating(MediaPlayer player);
	public void stopUpdating(MediaPlayer player);
	public void playlistend();
}
