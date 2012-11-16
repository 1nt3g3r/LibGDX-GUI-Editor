package com.saschaheylik.guieditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Layout {
	private String title, description;
	private Array<Actor> actors;
	private boolean hasChanged = false;
	
	public boolean hasChanged() { return hasChanged; }
	public void changeAccepted() { hasChanged = false; }
	
	public Layout(String title) { 
		this.title = title; 
		actors = new Array<Actor>();
	}
	
	public void addActor(Actor actor) { actors.add(actor); hasChanged = true;}
	public Array<Actor> getActors() { return actors; }
	public void setActors(Array<Actor> newActors) { actors = newActors; }
	public void setDescription(String description) { this.description = description; }
	public String getTitle() { return title; }
	public void setTitle(String newTitle) { title = newTitle; }
}
