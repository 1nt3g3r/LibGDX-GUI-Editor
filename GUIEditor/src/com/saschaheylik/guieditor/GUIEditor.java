package com.saschaheylik.guieditor;

import java.awt.Button;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;

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
	private List projectList;
	private Window newProjectForm;
	
	private Array<Project> projects;
	private Array<Window> projectWindows;
	private Gson gson;
	
	private String projectFolder = "projects";

	private Window createWndContainers() {

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

	private Window createWndTools() {
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

	private Window createWndLayouts() {
		Window window = new Window("Layouts", skin);

		// 10px space below objects
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		TextButton btnSave = new TextButton("Save", skin);
		window.add(btnSave);

		TextButton btnOpen = new TextButton("Open", skin);
		window.add(btnOpen);

		TextButton btnNew = new TextButton("New", skin);
		window.add(btnNew);

		TextButton btnClose = new TextButton("Close", skin);
		window.add(btnClose);

		window.pack();

		return window;
	}

	private Window createWndProjects() {
		Window window = new Window("Projects", skin);

		Table table = new Table(skin);
		
		table.defaults().spaceBottom(10);

		table.row().fill().expandX();
		TextButton btnNew = new TextButton("New", skin);
		table.add(btnNew);

		table.row().fill().expandX();
		TextButton btnRemove = new TextButton("Remove", skin);
		table.add(btnRemove);
		
		table.row().fill().expandX();
		TextButton btnSave = new TextButton("Save", skin);
		table.add(btnSave);

		table.row().fill().expandX();
		TextButton btnLoad = new TextButton("Load", skin);
		table.add(btnLoad);
		
		table.layout();
		
		projectList = new List(new String[] {}, skin);
		ScrollPane scrollPane = new ScrollPane(projectList, skin);
		SplitPane splitPane = new SplitPane(table, scrollPane, false, skin);
		
		window.row().fill().expandX();
		window.add(splitPane);
		window.setSize(150, 170);
		
		projectList.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					updateProjectWindows();
				}
				return false;
			}
		});
		
		btnRemove.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					removeSelectedProject();
				}
				return false;
			}
		});
		
		btnNew.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				// Add new Project
				if (event.isHandled())
					displayNewProjectForm();
				
				return false;
			}
		});
		
		btnSave.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled())
					saveSelectedProject();
				return false;
			}
		});

		return window;
	}
	
	private void saveSelectedProject(){
		try {
			Project project = getSelectedProject();
			String path = projectFolder + "/" + project.getTitle() + ".json";
			FileHandle file = Gdx.files.local(path);
			gson = new Gson();
			String projectJson = gson.toJson(project);
			file.writeString(projectJson, false);
			showInfo("Saved project as \"" + path + "\".");
		} catch (Exception e) {
			showError("Project couldn't be saved (none selected?)");
			e.printStackTrace();
		}
	}
	
	private void addProject(Project newProject) {
		projects.add(newProject);
		
		//Add Project title to list
		String[] listItems = projectList.getItems();
		String[] newListItems = new String[listItems.length+1];
		for (int i = 0; i < newListItems.length; i++) {
			if (i < newListItems.length-1) newListItems[i] = listItems[i];
			else newListItems[i] = newProject.getTitle();
		}
		projectList.setItems(newListItems);
		projectList.setSelection(newProject.getTitle());
		Window projectWindow = new Window(newProject.getTitle(), skin);
		projectWindow.setPosition(width/2 - projectWindow.getWidth()/2, height/2-projectWindow.getHeight()/2);
		projectWindows.add(projectWindow);
		stage.addActor(projectWindow);
		updateProjectWindows();
	}
	
	private void removeSelectedProject() {
		if (projectList.getItems().length == 0) return;
		int selectedProject = projectList.getSelectedIndex();
		String selectedProjectTitle = projectList.getSelection();
		//Find and remove project title from list
		String[] items = projectList.getItems();
		String[] newListItems = new String[items.length-1];
		boolean found = false;
		for (int i = 0; i < items.length; i++) {
			String item = items[i];	
			if (i == selectedProject) {
				found = true;
			} else {
				if (found)
					newListItems[i-1] = item;
				else
					newListItems[i] = item;
			}
		}
		projectList.setItems(newListItems);
		//Find and remove project window
		for (int i = 0; i < projectWindows.size; i++) {
			if (projectWindows.get(i).getTitle().compareTo(selectedProjectTitle) == 0) {
				projectWindows.get(i).remove();
				projectWindows.removeIndex(i);
			}
		}
		//Find and remove project
		for (Project project : projects) {
			if (project.getTitle().compareTo(selectedProjectTitle) == 0)
				projects.removeValue(project, false);
		}
	}
	
	private Project getSelectedProject() throws Exception {
		return getProject(projectList.getSelection());
	}
	
	private Project getProject(String title) throws Exception {
		for (Project project : projects) {
			if (project.getTitle().compareTo(title) == 0)
				return project;
		}
		throw new Exception("Project with title \"" + title + "\" not found in projectList.");
	}
	
	private int getProjectWindowIndex(String title) {
		for(int i = 0; i < projectWindows.size; i++) {
			Window projectWindow = projectWindows.get(i);
			if (projectWindow.getTitle().compareTo(title) == 0)
				return i;	
		}
		return -1;
	}
	
	private void updateProjectWindows() {
		String selection = projectList.getSelection();
		//Only display selected project
		for(int i = 0; i < projectWindows.size; i++) {
			Window projectWindow = projectWindows.get(i);
			if (projectWindow.getTitle().compareTo(selection) == 0)
				projectWindow.setVisible(true);
			else {
				projectWindow.setVisible(false);
			}
			
			//Update active project window position
			//projectWindow.setPosition(width/2 - projectWindow.getWidth()/2, height/2-projectWindow.getHeight()/2);
		}
	}
	
	private void displayNewProjectForm() {
		if (newProjectForm != null) {
			newProjectForm.setVisible(true);
			newProjectForm.setPosition(width/2-newProjectForm.getWidth()/2, height/2-newProjectForm.getHeight()/2);
			
			//Make sure the form is displayed above the active project window
			int activeProjectIndex = getProjectWindowIndex(projectList.getSelection());
			if (activeProjectIndex != -1) {
				Window activeProjectWindow = projectWindows.get(activeProjectIndex);
				newProjectForm.setZIndex(activeProjectWindow.getZIndex()+1);
			}
			return;
		}
		newProjectForm = new Window("New Project", skin);
		
		final TextField fieldTitle = new TextField("Title", skin);
		final TextField fieldDescription = new TextField("Description", skin);
		
		newProjectForm.row().fill().expandX();
		newProjectForm.add(fieldTitle);
		newProjectForm.row().fill().expandX();
		newProjectForm.add(fieldDescription);
		
		TextButton btnCancel = new TextButton("Cancel", skin);
		TextButton btnOK = new TextButton("OK", skin);
		
		btnCancel.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					newProjectForm.setVisible(false);
				}
				return false;
			}
		});
		
		btnOK.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					//Check if project name is unique
					boolean isUnique = true;
					String[] projectNames = projectList.getItems();
					String projectName = fieldTitle.getText();
					for (int i = 0; i < projectNames.length; i++) {
						if (projectNames[i].compareTo(projectName) == 0) isUnique = false;
					}
					
					//If yes, create new project
					if (isUnique) {
						Project newProject = new Project(projectName);
						newProject.setDescription(fieldDescription.getText());
					
						addProject(newProject);
					
						newProjectForm.setVisible(false);
					//Else, display an error message
					} else {
						showError("The project title has to be unique!");
					}
				}
				return false;
			}
		});
		
		newProjectForm.row().fill().expandX();
		
		SplitPane splitPane = new SplitPane(btnCancel, btnOK, false, skin);
		newProjectForm.add(splitPane);
		
		newProjectForm.setPosition(width/2-newProjectForm.getWidth()/2, height/2-newProjectForm.getHeight()/2);
		stage.addActor(newProjectForm);
	}
	
	private void showError(String message) {
		showDialog("Error", message);
	}
	
	private void showInfo(String message) {
		showDialog("Info", message);
	}

	private void showDialog(String title, String message) {
		new Dialog(title, skin, "dialog") {
			protected void result (Object object) {
			}
		}.text(message).button("OK", true).show(stage);
	}

	private Window createWndButtons() {
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
	
	private Window createWndText() {
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

	private Window createWndMiscellaneous() {
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
		projects = new Array<Project>();
		projectWindows = new Array<Window>();
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
		
		
		wndLayouts.setX(width/2 -wndLayouts.getWidth() - defaultGapSize);
		wndLayouts.setY(height - wndLayouts.getHeight()- defaultGapSize);
		wndProjects.setX(width/2 + defaultGapSize);
		wndProjects.setY(height - wndProjects.getHeight()- defaultGapSize);
		
		float leftColumnWidth = defaultGapSize*2 + wndTools.getWidth() + wndContainers.getWidth();
		float rightColumnWidth = Math.max(Math.max(wndButtons.getWidth(), wndText.getWidth()), 
													wndMiscellaneous.getWidth());
		float topColumnHeight = Math.max(wndLayouts.getHeight(), wndProjects.getHeight());
		float upperGap = defaultGapSize*2;
		//If layouts is colliding with left column or projects with right column
		if (wndLayouts.getX() < leftColumnWidth+defaultGapSize ||
				wndProjects.getX()+wndProjects.getWidth() > width - rightColumnWidth) {
			//Then leave a gap between upper and right/left columns
			upperGap += topColumnHeight;
		}
		
		//Left column
		wndContainers.setX(defaultGapSize + wndTools.getWidth() + defaultGapSize); 
		wndContainers.setY(height - wndContainers.getHeight() - upperGap);
		wndTools.setX(defaultGapSize); 
		wndTools.setY(height - (wndTools.getHeight() + upperGap));
		
		//Right column
		wndButtons.setY(height - wndButtons.getHeight() - upperGap);
		wndButtons.setX(width - wndButtons.getWidth() - defaultGapSize);
		wndText.setY(height - wndText.getHeight() - defaultGapSize - wndButtons.getHeight() - upperGap);
		wndText.setX(width - wndText.getWidth() - defaultGapSize);
		wndMiscellaneous.setX(width - wndMiscellaneous.getWidth() - defaultGapSize);
		wndMiscellaneous.setY(height - wndMiscellaneous.getHeight() - defaultGapSize 
						- wndButtons.getHeight() - defaultGapSize -
						wndText.getHeight() - upperGap);
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
		
		//updateProjectWindows();
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
