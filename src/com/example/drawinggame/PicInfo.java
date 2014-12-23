package com.example.drawinggame;

public class PicInfo {
	private static PicInfo instance = null;
	private String pathToImages;
	private String image0;
	private String image1;
	private String image2;
	private String image3;
	private String guess0;
	private String guess1;
	private String guess2;
	private String guess3;
	protected PicInfo(){
		
	}
	
	public static PicInfo getInstance(){
		if(instance == null){
			instance = new PicInfo();
		}
		return instance;
	}
	
	public void setImage(long path2, int num){
		String path = String.valueOf(path2)+".jpg";
		if(num == 0) image0 = path;
		else if(num == 1) image1 = path;
		else if(num == 2) image2 = path;
		else if(num == 3) image3 = path;
	}
	
	public void setGuess(String guess, int num){
		if(num == 0) guess0 = guess;
		else if(num == 1) guess1 = guess;
		else if(num == 2) guess2 = guess;
		else if(num == 3) guess3 = guess;
	}
	
	public String getImage(int num){
		if(num == 0) return image0;
		else if (num == 1) return image1;
		else if (num == 2) return image2;
		else if (num == 3) return image3;
		return null;
	}
	public String getGuess(int num){
		if(num == 0) return guess0;
		else if (num == 1) return guess1;
		else if (num == 2) return guess2;
		else if (num == 3) return guess3;
		return null;
	}
	
	public void setPath(String path){
		pathToImages = path;
	}
	
	public String getPath(){
		return pathToImages;
	}
}
