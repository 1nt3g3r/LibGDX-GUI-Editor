package com.saschaheylik.guieditor;

public class Project {
	private String title, description;
	private Layout[] layouts;
	
	public Project(String newTitle) {
		this.title = newTitle;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
}
