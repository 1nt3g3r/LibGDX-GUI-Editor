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
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class GUIEditor implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage stage;
	private Skin skin;
	private float width, height;

	private Label label;
	private Window wndContainers, wndTools, wndLayouts;
	private Window wndProjects, wndButtons, wndText, wndMiscellaneous;
	private int defaultWindowWidth = 125;
	private int defaultGapSize = 5;

	public Window createWndContainers() {

		Window window = new Window("Containers", skin);

		// 10px space above objects
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		TextButton btnAddWindow = new TextButton("Add Window", skin);
		window.add(btnAddWindow);

		window.row().fill().expandX();
		TextButton btnAddTable = new TextButton("Add Table", skin);
		window.add(btnAddTable);

		window.row().fill().expandX();
		TextButton btnAddSplitPane = new TextButton("Add SplitPane", skin);
		window.add(btnAddSplitPane);

		window.row().fill().expandX();
		TextButton btnAddScrollPane = new TextButton("Add ScrollPane", skin);
		window.add(btnAddScrollPane);

		// window.setWidth(defaultWindowWidth);
		// window.setHeight(4*30 + 20);

		window.pack();

		return window;
	}

	public Window createWndTools() {
		Window window = new Window("Tools", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row();
		window.row().fill().expandX();
		TextButton btnRemove = new TextButton("Remove", skin);
		window.add(btnRemove);

		window.row().fill().expandX();
		TextButton btnMove = new TextButton("Move", skin);
		window.add(btnMove);

		window.row().fill().expandX();
		TextButton btnResize = new TextButton("Resize", skin);
		window.add(btnResize);

		window.row().fill().expandX();
		TextButton btnEdit = new TextButton("Edit", skin);
		window.add(btnEdit);

		window.pack();

		return window;
	}

	public Window createWndLayouts() {
		Window window = new Window("Layouts", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row();
		window.row().fill().expandX();
		TextButton btnSave = new TextButton("Save", skin);
		window.add(btnSave);

		window.row().fill().expandX();
		TextButton btnOpen = new TextButton("Open", skin);
		window.add(btnOpen);

		window.row().fill().expandX();
		TextButton btnNew = new TextButton("New", skin);
		window.add(btnNew);

		window.row().fill().expandX();
		TextButton btnClose = new TextButton("Close", skin);
		window.add(btnClose);

		window.pack();

		return window;
	}

	public Window createWndProjects() {
		Window window = new Window("Projects", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row();
		window.row().fill().expandX();
		TextButton btnSave = new TextButton("Save", skin);
		window.add(btnSave);

		window.row().fill().expandX();
		TextButton btnOpen = new TextButton("Open", skin);
		window.add(btnOpen);

		window.row().fill().expandX();
		TextButton btnNew = new TextButton("New", skin);
		window.add(btnNew);

		window.row().fill().expandX();
		TextButton btnClose = new TextButton("Close", skin);
		window.add(btnClose);

		window.pack();

		return window;
	}

	public Window createWndButtons() {
		Window window = new Window("Buttons", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row();
		window.row().fill().expandX();
		TextButton btnAddTextButton = new TextButton("Add TextButton", skin);
		window.add(btnAddTextButton);

		window.row().fill().expandX();
		TextButton btnAddImageButton = new TextButton("Add ImageButton", skin);
		window.add(btnAddImageButton);

		window.row().fill().expandX();
		TextButton btnAddTextButtonToggle = new TextButton(
				"Add TextButton (Toggle)", skin);
		window.add(btnAddTextButtonToggle);

		window.row().fill().expandX();
		TextButton btnAddImageButtonToggle = new TextButton(
				"Add ImageButton (Toggle)", skin);
		window.add(btnAddImageButtonToggle);

		window.pack();

		return window;
	}
	
	public Window createWndText() {
		Window window = new Window("Text", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		TextButton btnAddLabel = new TextButton("Add Label", skin);
		window.add(btnAddLabel);

		window.row().fill().expandX();
		TextButton btnAddTextField = new TextButton("Add TextField", skin);
		window.add(btnAddTextField);

		window.row().fill().expandX();
		TextButton btnAddPasswordField = new TextButton(
				"Add TextButton (Toggle)", skin);
		window.add(btnAddPasswordField);

		window.pack();

		return window;
	}

	public Window createWndMiscellaneous() {
		Window window = new Window("Miscellaneous", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		TextButton btnAddImage = new TextButton("Add Image", skin);
		window.add(btnAddImage);

		window.row().fill().expandX();
		TextButton btnAddSlider= new TextButton("Add Slider", skin);
		window.add(btnAddSlider);

		window.row().fill().expandX();
		TextButton btnAddSelectBox = new TextButton(
				"Add Dropdown", skin);
		window.add(btnAddSelectBox);

		window.pack();

		return window;
	}
	
	@Override
	public void create() {

		camera = new OrthographicCamera(1, height / width);
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage(width, height, true);
		Gdx.input.setInputProcessor(stage);

		// window.debug();

		wndContainers = createWndContainers();
		stage.addActor(wndContainers);

		wndTools = createWndTools();
		stage.addActor(wndTools);

		wndLayouts = createWndLayouts();
		stage.addActor(wndLayouts);

		wndProjects = createWndProjects();
		stage.addActor(wndProjects);

		wndButtons = createWndButtons();
		stage.addActor(wndButtons);
		
		wndText = createWndText();
		stage.addActor(wndText);
		
		wndMiscellaneous = createWndMiscellaneous();
		stage.addActor(wndMiscellaneous);
	}

	public void updateWindows() {
		//wndContainers.setWidth(123);
		//wndContainers.setHeight(160);
		//wndTools.setWidth(75);
		//wndLayouts.setWidth(65);
		
		wndContainers.setX(defaultGapSize + wndTools.getWidth() + defaultGapSize); 
		wndContainers.setY(height - wndContainers.getHeight() - defaultGapSize);
		wndTools.setX(defaultGapSize); 
		wndTools.setY(height - (wndTools.getHeight() + defaultGapSize));
		wndLayouts.setX(defaultGapSize);
		wndLayouts.setY(height - wndTools.getHeight() - defaultGapSize - wndLayouts.getHeight()- defaultGapSize);
		wndProjects.setX(defaultGapSize + wndLayouts.getWidth() + defaultGapSize);
		wndProjects.setY(height - wndTools.getHeight() - defaultGapSize - wndProjects.getHeight()- defaultGapSize);
		
		wndButtons.setY(height - wndButtons.getHeight() - defaultGapSize);
		wndButtons.setX(width - wndButtons.getWidth() - defaultGapSize);
		wndText.setY(height - wndText.getHeight() - defaultGapSize - wndButtons.getHeight() - defaultGapSize);
		wndText.setX(width - wndText.getWidth() - defaultGapSize);
		wndMiscellaneous.setX(width - wndMiscellaneous.getWidth() - defaultGapSize);
		wndMiscellaneous.setY(height - wndMiscellaneous.getHeight() - defaultGapSize 
						- wndButtons.getHeight() - defaultGapSize -
						wndText.getHeight() - defaultGapSize);
		}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		skin.dispose();
	}

	@Override
	public void render() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		updateWindows();

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

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
		updateWindows();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
