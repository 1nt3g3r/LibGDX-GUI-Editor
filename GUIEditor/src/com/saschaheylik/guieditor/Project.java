package com.saschaheylik.guieditor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class Project {
	private String title, description;
	private Layout[] layouts;
	private Window window;
	
	public Window getWindow() {
		return window;
	}
	
	public Project(String newTitle) {
		title = newTitle;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
}
