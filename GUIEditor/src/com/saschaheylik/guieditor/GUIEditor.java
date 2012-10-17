package com.saschaheylik.guieditor;

import java.awt.Button;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GUIEditor implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage stage;
	private Skin skin;
	private float width, height;
	private Table table;
	
	@Override
	public void create() {		
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, height/width);
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage(width, height, true);
		Gdx.input.setInputProcessor(stage);
		
		table = new Table();
		
		table.row();
		table.add(new Label("Test", skin));
		
		table.row();
		table.add(new TextButton("TestButton", skin));
		
		table.layout();
		
		table.setX(width/2);
		table.setY(height/2);
		
		stage.addActor(table);
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		skin.dispose();
	}
	
	@Override
	public void render() {		
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		Table.drawDebug(stage);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
