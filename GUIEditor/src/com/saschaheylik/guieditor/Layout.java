package com.saschaheylik.guieditor;

public class Layout {
	String title, description;
	
	public void setDescription(String description) { this.description = description; }
	public String getTitle() { return title; }
	public void setTitle(String newTitle) { title = newTitle; }
	public Layout(String title) { this.title = title; }
}
