package com.saschaheylik.guieditor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.saschaheylik.guieditor.screens.GUIEditorScreen;
import com.saschaheylik.guieditor.screens.LayoutScreen;

public class App implements ApplicationListener {

	private GUIEditorScreen guiEditorScreen;
	private LayoutScreen layoutScreen;
	private boolean tabPressed = false;
	private String activeScreen;
	@Override
	public void create() {
		layoutScreen = new LayoutScreen();
		layoutScreen.show();
		layoutScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		guiEditorScreen = new GUIEditorScreen();
		guiEditorScreen.show();
		guiEditorScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		activeScreen = "guiEditor";
	}

	@Override
	public void dispose () {
		guiEditorScreen.hide();
		layoutScreen.hide();
	}

	@Override
	public void pause () {
		guiEditorScreen.pause();
		layoutScreen.pause();
	}

	@Override
	public void resume () {
		guiEditorScreen.resume();
		layoutScreen.resume();
	}
	
	private void update() {
		if (guiEditorScreen.layoutHasChanged()) {
			layoutScreen.setLayout(guiEditorScreen.getSelectedLayout());
			guiEditorScreen.layoutChangeAccepted();
		}
		
		if (Gdx.input.isKeyPressed(Keys.TAB)) {
			tabPressed = true;
		} else if (tabPressed) {
			tabPressed = false;
			switchScreen(); 
		}
	}

	@Override
	public void render () {
		update();
		
		if (activeScreen.compareTo("guiEditor") == 0) guiEditorScreen.render(Gdx.graphics.getDeltaTime());
		if (activeScreen.compareTo("layout") == 0) layoutScreen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize (int width, int height) {
		guiEditorScreen.resize(width, height);
		layoutScreen.resize(width, height);
	}
	
	public void switchScreen() {
		if (activeScreen.compareTo("guiEditor") == 0) {
			activeScreen = "layout";
			Layout layout = layoutScreen.getLayout();
			if (layout != null)
				Gdx.graphics.setTitle(layout.getTitle());
			else
				Gdx.graphics.setTitle("Layout");
		}
		else {
			activeScreen = "guiEditor";
			Gdx.graphics.setTitle("libGDX GUI-Editor");
		}
	}
}
