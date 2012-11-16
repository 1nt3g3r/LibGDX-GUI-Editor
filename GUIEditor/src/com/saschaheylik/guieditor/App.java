package com.saschaheylik.guieditor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.saschaheylik.guieditor.screens.GUIEditorScreen;
import com.saschaheylik.guieditor.screens.ProjectScreen;

public class App implements ApplicationListener {

	private Screen screen, guiEditorScreen, projectScreen;
	private Skin skin;
	private boolean tabPressed = false;
	@Override
	public void create() {
		// TODO Auto-generated method stub
		guiEditorScreen = new GUIEditorScreen();
		projectScreen = new ProjectScreen();
		
		setScreen(guiEditorScreen);
		
	}

	@Override
	public void dispose () {
		if (screen != null) screen.hide();
	}

	@Override
	public void pause () {
		if (screen != null) screen.pause();
	}

	@Override
	public void resume () {
		if (screen != null) screen.resume();
	}

	@Override
	public void render () {
		if (Gdx.input.isKeyPressed(Keys.TAB)) {
			tabPressed = true;
		} else if (tabPressed) {
			tabPressed = false;
			switchScreen(); 
		}
		
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize (int width, int height) {
		if (screen != null) screen.resize(width, height);
	}
	
	public void switchScreen() {
		if (getScreen() == null) return;
		
		if (getScreen() == projectScreen)
			setScreen(guiEditorScreen);
		else
			setScreen(projectScreen);
	}

	/** Sets the current screen. {@link Screen#hide()} is called on any old screen, and {@link Screen#show()} is called on the new
	 * screen. */
	public void setScreen (Screen screen) {
		if (this.screen != null) this.screen.hide();
		this.screen = screen;
		screen.show();
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/** @return the currently active {@link Screen}. */
	public Screen getScreen () {
		return screen;
	}
}
