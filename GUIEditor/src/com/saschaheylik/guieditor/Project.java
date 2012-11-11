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
	
	public boolean addLayout(Layout newLayout) {
		for (Layout layout: layouts) {
			if (layout.title.compareTo(newLayout.title) == 0)
				return true;
		}
		layouts.add(newLayout);
		return false;
	}
	
	public void removeLayout(Layout layout) {
		layouts.removeValue(layout, false);
	}
	
	public Array<Layout> getLayouts() {
		return layouts;
	}
	
	public void setTitle(String newTitle) { title = newTitle; }
	public void setDescription(String description) { this.description = description; }
	public String getTitle() { return title; }
}
