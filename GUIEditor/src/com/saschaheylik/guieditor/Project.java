package com.saschaheylik.guieditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;

public class Project {
	private String title, description;
	private Array<Layout> layouts;
	
	public static Project load(String path) {
		
		//See if a .project file exists in the project folder (path)
		FileHandle[] projectFiles = Gdx.files.local(path).list();
		boolean projectFileExists = false;
		for (FileHandle handle : projectFiles) {
			if (handle.path().contains(".project")) {
				projectFileExists = true;
				path = handle.path();
			}
		}
		if (!projectFileExists)
			return null;
		
		//Extract project title
		int lastProjectTitleIndex = path.indexOf(".project");
		int firstProjectTitleIndex = 0;
		for (int i = lastProjectTitleIndex; i > 0; i--) {
			if (path.substring(i, i+1).compareTo("/") == 0) {
				firstProjectTitleIndex = i+1;
				break;
			}
		}
		//Couldn't extract project title
		if (firstProjectTitleIndex == 0)
			return null;
		String title = path.substring(firstProjectTitleIndex, lastProjectTitleIndex);
		String description = "";
		
		String projectString = Gdx.files.local(path).readString();
		String[] lines = projectString.split("\n");
		for (String line : lines) {
			if (line.contains("description:")) {
				int ldi = line.lastIndexOf("description:");
				int firstQuote = 0;
				int lastQuote = 0;
				for (int i = ldi; i < line.length(); i++) {
					if (line.substring(i, i+1).compareTo("\"") == 0) {
						if (firstQuote == 0)
							firstQuote = i;
						else
							lastQuote = i;
					}
				}
				description = line.substring(firstQuote+1, lastQuote);
			}
		}
		
		Project project = new Project(title);
		project.setDescription(description);
		
		return project;
	}

	public void save(String path) {
		//FileHandle file = Gdx.files.local(path);
		//file.writeString(project.toString(), false);
		
		path += "/" + title;
		
		//If the project folder does not exist, create it.
		if (!Gdx.files.local(path).exists()) {
			Gdx.files.local(path).mkdirs();
		}
		
		//Save main project file
		String projectFile = "description: \"" + description + "\"\n";
		Gdx.files.local(path + "/" + title + ".project").writeString(projectFile, false);
		
		//Save layouts
		for (Layout layout : layouts) {
			layout.save(path);
		}
	}
	
	public Project(String newTitle) {
		title = newTitle;
		layouts  = new Array<Layout>();
	}
	
	public boolean addLayout(Layout newLayout) {
		for (Layout layout: layouts) {
			if (layout.getTitle().compareTo(newLayout.getTitle()) == 0)
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
