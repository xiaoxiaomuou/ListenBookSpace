package com.hwang.listenbook;

import android.app.Application;

public class App extends Application{

	public boolean isPlaying=false;
	
	public boolean isDownLoading =false;
	
	public String currentPlay ="";
	
	
	
	public String getCurrentPlay() {
		return currentPlay;
	}

	public void setCurrentPlay(String currentPlay) {
		this.currentPlay = currentPlay;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isDownLoading() {
		return isDownLoading;
	}

	public void setDownLoading(boolean isDownLoading) {
		this.isDownLoading = isDownLoading;
	}
	
	
}
