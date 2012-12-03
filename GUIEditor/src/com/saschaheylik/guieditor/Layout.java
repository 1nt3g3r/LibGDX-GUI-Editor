package com.saschaheylik.guieditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Layout {
	private String title, description;
	private Array<Actor> actors;
	private boolean hasChanged = false;

	public boolean hasChanged() {
		return hasChanged;
	}

	public void changeAccepted() {
		hasChanged = false;
	}

	public static Layout load(String path) {
		// Extract title from path
		int lastLayoutTitleIndex = path.indexOf(".layout");
		int firstLayoutTitleIndex = 0;
		for (int i = lastLayoutTitleIndex; i > 0; i--) {
			if (path.substring(i, i + 1).compareTo("/") == 0) {
				firstLayoutTitleIndex = i + 1;
				break;
			}
		}
		// Couldn't extract layout title
		if (firstLayoutTitleIndex == 0)
			return null;
		String extractedTitle = path.substring(firstLayoutTitleIndex,
				lastLayoutTitleIndex);

		// Load data
		String layoutString = Gdx.files.local(path).readString();
		String[] lines = layoutString.split("\n");
		// Extract description
		String extractedDescription = "";
		for (String line : lines) {
			if (line.contains("description:")) {
				int lastDescriptionIndex = line.lastIndexOf("description:");
				int firstQuote = 0;
				int lastQuote = 0;
				for (int i = lastDescriptionIndex; i < line.length(); i++) {
					if (line.substring(i, i + 1).compareTo("\"") == 0) {
						if (firstQuote == 0)
							firstQuote = i;
						else
							lastQuote = i;
					}
				}
				extractedDescription = line.substring(firstQuote + 1, lastQuote);
			}
		}

		Layout layout = new Layout(extractedTitle);
		layout.setDescription(extractedDescription);

		return layout;
	}

	public void save(String path) {
		// Save description
		String layoutString = "description: \"" + description + "\"\n";

		// Save actors
		layoutString += "actors: {\n";
		for (Actor actor : actors) {
			layoutString += "    " + actor.getClass().getName() + " \""
					+ actor.getName() + "\"\n";
		}
		layoutString += "}";

		// Write to file
		Gdx.files.local(path + "/" + title + ".layout").writeString(
				layoutString, false);
	}

	public Layout(String title) {
		this.title = title;
		actors = new Array<Actor>();
	}

	public void addActor(Actor actor) {
		actors.add(actor);
		hasChanged = true;
	}

	public Array<Actor> getActors() {
		return actors;
	}

	public void setActors(Array<Actor> newActors) {
		actors = newActors;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}
}
