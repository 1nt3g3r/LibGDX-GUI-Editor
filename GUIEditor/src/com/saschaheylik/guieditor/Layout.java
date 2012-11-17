package com.saschaheylik.guieditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Layout {
	private String title, description;
	private Array<Actor> actors;
	private boolean hasChanged = false;
	
	public boolean hasChanged() { return hasChanged; }
	public void changeAccepted() { hasChanged = false; }
	
	public static Layout load(String path) {
		return null;
	}
	
	public void save(String path) {
		//Save description
		String layoutString = "description: \"" + description + "\"\n";
		
		//Save actors
		layoutString += "actors: {\n";
		for (Actor actor : actors) {
			layoutString += "    " + actor.getClass().getName() + " \"" + actor.getName() + "\"\n";
		}
		layoutString += "}";
		
		//Write to file
		Gdx.files.local(path + "/" + title + ".layout").writeString(layoutString, false);
	}
	
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
