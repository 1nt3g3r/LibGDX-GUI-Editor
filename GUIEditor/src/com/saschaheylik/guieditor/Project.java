package com.saschaheylik.guieditor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;

public class Project {
	private String title, description;
	private Array<Layout> layouts;
	
	public Project(String newTitle) {
		title = newTitle;
		layouts  = new Array<Layout>();
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int save(String path) {return 0;}
}
