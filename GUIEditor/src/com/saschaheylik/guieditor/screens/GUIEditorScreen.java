package com.saschaheylik.guieditor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.saschaheylik.guieditor.Layout;
import com.saschaheylik.guieditor.Project;

public class GUIEditorScreen implements Screen {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage stage;
	private Skin skin;
	private float width, height;

	private Window wndContainers, wndTools, wndLayouts;
	private Window wndProjects, wndButtons, wndText, wndMiscellaneous;
	private int gapSize = 5;
	private List listProjects, listProjectFiles, listLayouts;
	private Window wndNewProject, wndLoadProject;
	private Window wndNewLayout, wndInfo;

	private Array<Project> projects;

	private String projectFolder = "projects";
	private boolean layoutChanged = false;
	
	@Override
	public void hide() {}

	private void saveSelectedProject() {
		Project project = null;
		try {
			project = getSelectedProject();
		} catch (Exception e) {
			showError("Project couldn't be saved (none selected?)");
			return;
		}
		if (project != null){
			project.save(projectFolder);
			showInfo("Saved project as \"" + projectFolder + "/" + project.getTitle() + "\"" + ".");
		} else 
			showError("Couldn't save project.");
	}
	
	private boolean yesNoDialog(String title, String message) {
		new Dialog(title, skin, "dialog") {
			protected void result(Object object) {
			}
		}.text(message).button("No",false).button("Yes", true).show(stage);
		return false;
	}
	
	private void overrideProject(Project newProject) {
		//Find and replace old project with same title
		for (Project project : projects) {
			if (project.getTitle().compareTo(newProject.getTitle()) == 0) {
				projects.removeValue(project, false);
				projects.add(newProject);
				onProjectChanged();
			}
		}
	}

	private void addProject(Project newProject) {
		if (!isProjectTitleUnique(newProject.getTitle())) {
			//showError("Cannot add project because title is already in use!");
			if (yesNoDialog("Override project?", 
					"Are you sure you want to override project\""+newProject.getTitle()+"\" ?"))
				overrideProject(newProject);
			return;
		}
		
		projects.add(newProject);

		// Add Project title to list
		String[] listItems = listProjects.getItems();
		String[] newListItems = new String[listItems.length + 1];
		for (int i = 0; i < newListItems.length; i++) {
			if (i < newListItems.length - 1)
				newListItems[i] = listItems[i];
			else
				newListItems[i] = newProject.getTitle();
		}
		listProjects.setItems(newListItems);
		listProjects.setSelection(newProject.getTitle());
		onProjectChanged();
	}

	private void removeSelectedProject() {
		if (listProjects.getItems().length == 0)
			return;
		int selectedProject = listProjects.getSelectedIndex();
		String selectedProjectTitle = listProjects.getSelection();
		// Find and remove project title from list
		String[] items = listProjects.getItems();
		String[] newListItems = new String[items.length - 1];
		boolean found = false;
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			if (i == selectedProject) {
				found = true;
			} else {
				if (found)
					newListItems[i - 1] = item;
				else
					newListItems[i] = item;
			}
		}
		listProjects.setItems(newListItems);
		
		// Find and remove project
		for (Project project : projects) {
			if (project.getTitle().compareTo(selectedProjectTitle) == 0)
				projects.removeValue(project, false);
		}
		
		onProjectChanged();
	}

	private Project getSelectedProject() throws Exception {
		return getProject(listProjects.getSelection());
	}

	private Project getProject(String title) throws Exception {
		for (Project project : projects) {
			if (project.getTitle().compareTo(title) == 0)
				return project;
		}
		throw new Exception("Project with title \"" + title
				+ "\" not found in projectList.");
	}

	private void showError(String message) {
		showDialog("Error", message);
	}

	private void showInfo(String message) {
		showDialog("Info", message);
	}

	private void showDialog(String title, String message) {
		new Dialog(title, skin, "dialog") {
			protected void result(Object object) {
			}
		}.text(message).button("OK", true).show(stage);
	}
	
	private Window createWndInfo() {
		Window window = new Window("Info", skin);
		Label infoLabel = new Label("  Press [TAB] to switch between Editor and Project", skin);

		window.setSize(infoLabel.getWidth()+2*gapSize, 50);
		window.row().expandX().fill();
		window.addActor(infoLabel);
		
		return window;
	}

	@Override
	public void show() {

		camera = new OrthographicCamera(1, height / width);
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage(width, height, true);
		Gdx.input.setInputProcessor(stage);
		
		projects = new Array<Project>();

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
		
		wndInfo = createWndInfo();
		stage.addActor(wndInfo);
		
		}

	public void updateWindows() {
		// wndContainers.setWidth(123);
		// wndContainers.setHeight(160);
		// wndTools.setWidth(75);
		// wndLayouts.setWidth(65);

		wndLayouts.setX(width / 2 - wndLayouts.getWidth() - gapSize);
		wndLayouts.setY(height - wndLayouts.getHeight() - gapSize);
		wndProjects.setX(width / 2 + gapSize);
		wndProjects.setY(height - wndProjects.getHeight() - gapSize);

		float leftColumnWidth = gapSize * 2 + wndTools.getWidth()
				+ wndContainers.getWidth();
		float rightColumnWidth = Math.max(
				Math.max(wndButtons.getWidth(), wndText.getWidth()),
				wndMiscellaneous.getWidth());
		float topColumnHeight = Math.max(wndLayouts.getHeight(),
				wndProjects.getHeight());
		float upperGap = gapSize * 2;
		// If layouts is colliding with left column or projects with right
		// column
		if (wndLayouts.getX() < leftColumnWidth + gapSize
				|| wndProjects.getX() + wndProjects.getWidth() > width
						- rightColumnWidth) {
			// Then leave a gap between upper and right/left columns
			upperGap += topColumnHeight;
		}

		// Left column
		wndContainers.setX(gapSize + wndTools.getWidth()
				+ gapSize);
		wndContainers.setY(height - wndContainers.getHeight() - upperGap);
		wndTools.setX(gapSize);
		wndTools.setY(height - (wndTools.getHeight() + upperGap));

		// Right column
		wndButtons.setY(height - wndButtons.getHeight() - upperGap);
		wndButtons.setX(width - wndButtons.getWidth() - gapSize);
		wndText.setY(height - wndText.getHeight() - gapSize
				- wndButtons.getHeight() - upperGap);
		wndText.setX(width - wndText.getWidth() - gapSize);
		wndMiscellaneous.setX(width - wndMiscellaneous.getWidth()
				- gapSize);
		wndMiscellaneous.setY(height - wndMiscellaneous.getHeight()
				- gapSize - wndButtons.getHeight() - gapSize
				- wndText.getHeight() - upperGap);
		
		wndInfo.setPosition(gapSize, gapSize);
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		skin.dispose();
	}

	private void onProjectChanged() {
		onLayoutChanged();
	}
	
	private void onLayoutChanged() {
		updateListLayouts();
		layoutChanged = true;
	}
	
	public boolean layoutHasChanged() { return layoutChanged; }
	public void layoutChangeAccepted() { layoutChanged = false; }
	
	public Layout getSelectedLayout() {
		String selectedLayoutTitle = listLayouts.getSelection();
		
		try {
			for (Layout layout : getSelectedProject().getLayouts()){
				if (layout.getTitle().compareTo(selectedLayoutTitle) == 0)
					return layout;
			}
		} catch (Exception e) {
			return null;
		}
		
		return null;
	}
	
	@Override
	public void render(float delta) {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		updateWindows();

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		stage.act(Math.min(delta, 1 / 30f));
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
		
		btnAddTextButton.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					Layout selectedLayout = getSelectedLayout();
					if (selectedLayout != null) {
						selectedLayout.addActor(new TextButton("Test", skin));
						showInfo("TextButton added.");
						layoutChanged = true;
					}
				}
				return false;
			}
		});

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
		TextButton btnAddSlider = new TextButton("Add Slider", skin);
		window.add(btnAddSlider);

		window.row().fill().expandX();
		TextButton btnAddSelectBox = new TextButton("Add Dropdown", skin);
		window.add(btnAddSelectBox);

		window.pack();

		return window;
	}

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
		window.defaults().spaceTop(10);
		
		window.row().fill().expandX();
		TextButton btnNew = new TextButton("New", skin);
		
		TextButton btnDelete = new TextButton("Delete", skin);
		
		listLayouts = new List(new String [] {}, skin);
		listLayouts.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled())
					layoutChanged = true;
				return false;
			}
		});
		
		window.add(new SplitPane(new ScrollPane(listLayouts), new SplitPane(btnNew, btnDelete, false, skin), true, skin));

		btnNew.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					displayWndNewLayout();
				}
				return false;
			}
		});

		btnDelete.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					deleteSelectedLayout();
				}
				return false;
			}
		});

		
		window.pack();
		window.setHeight(170);

		return window;
	}
	
	private void updateListLayouts() {
		if (projects.size > 0) {
			try {
					Array<Layout> layouts = getSelectedProject().getLayouts();
					String layoutTitles[] = new String[layouts.size];
					for (int i = 0; i < layouts.size; i++) {
						layoutTitles[i] = layouts.get(i).getTitle();
					}
					listLayouts.setItems(layoutTitles);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				showError("getSelectedProject() failed");
				e.printStackTrace();
				return;
			}
		}
	}
	
	private void displayWndNewLayout() {
		if (wndNewLayout != null) {
			wndNewLayout.setVisible(true);
			wndNewLayout.toFront();
			return;
		}
		
		wndNewLayout = new Window("New Layout", skin);
		wndNewLayout.defaults().spaceBottom(10);
		
		wndNewLayout.row().fill().expandX();
		final TextField fieldLayoutTitle = new TextField("Title", skin);
		wndNewLayout.add(fieldLayoutTitle);
		
		wndNewLayout.row().fill().expandX();
		final TextField fieldLayoutDescription = new TextField("Description", skin);
		wndNewLayout.add(fieldLayoutDescription);
		
		TextButton btnCancel = new TextButton("Cancel", skin);
		TextButton btnOK = new TextButton("OK", skin);
		wndNewLayout.row().fill().expandX();
		wndNewLayout.add(new SplitPane(btnCancel, btnOK, false, skin));
		
		btnCancel.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				// TODO Auto-generated method stub
				if (event.isHandled()) {
					wndNewLayout.setVisible(false);
				}
				return false;
			}
		});
		
		btnOK.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					Layout layout = new Layout(fieldLayoutTitle.getText());
					layout.setDescription(fieldLayoutDescription.getText());
					addLayoutToSelectedProject(layout);
				}
				return false;
			}
		});
		
		centerWindow(wndNewLayout);
		stage.addActor(wndNewLayout);
		wndNewLayout.toFront();
	}
	
	private void addLayoutToSelectedProject(Layout layout) {
		Project project;
		try {
			project = getSelectedProject();
			if (project.addLayout(layout))
				showError("Layout title must be unique!");
			else { 
				wndNewLayout.setVisible(false);
				onLayoutChanged();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			showError("No Project selected.");
		}
	}
	
	private void deleteSelectedLayout() {
		if (listLayouts.getItems().length == 0)
			return;
		String selectedLayoutTitle = listLayouts.getSelection();
		
		// Find and remove layout
		try {
			for (Layout layout : getSelectedProject().getLayouts()) {
				if (layout.getTitle().compareTo(selectedLayoutTitle) == 0)
					getSelectedProject().removeLayout(layout);
			}
		} catch (Exception e) {
			showError(e.getMessage());
		}
		
		onLayoutChanged();
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

		listProjects = new List(new String[] {}, skin);
		ScrollPane scrollPane = new ScrollPane(listProjects, skin);
		SplitPane splitPane = new SplitPane(table, scrollPane, false, skin);

		window.row().fill().expandX();
		window.add(splitPane);
		window.setSize(150, 170);

		listProjects.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					onProjectChanged();
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

		btnLoad.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled())
					displayLoadProjectForm();
				return false;
			}
		});

		return window;
	}

	private void displayNewProjectForm() {
		if (wndNewProject != null) {
			wndNewProject.setVisible(true);
			centerWindow(wndNewProject);
			wndNewProject.toFront();
			return;
		}
		wndNewProject = new Window("New Project", skin);

		final TextField fieldTitle = new TextField("Title", skin);
		final TextField fieldDescription = new TextField("Description", skin);

		wndNewProject.row().fill().expandX();
		wndNewProject.add(fieldTitle);
		wndNewProject.row().fill().expandX();
		wndNewProject.add(fieldDescription);

		TextButton btnCancel = new TextButton("Cancel", skin);
		TextButton btnOK = new TextButton("OK", skin);

		btnCancel.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					wndNewProject.setVisible(false);
				}
				return false;
			}
		});

		btnOK.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					// Check if project name is unique

					// If yes, create new project
					String projectTitle = fieldTitle.getText();
						Project newProject = new Project(projectTitle);
						newProject.setDescription(fieldDescription.getText());

						addProject(newProject);

						wndNewProject.setVisible(false);
						// Else, display an error message
				}
				return false;
			}
		});

		wndNewProject.row().fill().expandX();

		SplitPane splitPane = new SplitPane(btnCancel, btnOK, false, skin);
		wndNewProject.add(splitPane);

		stage.addActor(wndNewProject);
		centerWindow(wndNewProject);
		wndNewProject.toFront();
	}
	
	private boolean isProjectTitleUnique(String title) {
		String[] projectNames = listProjects.getItems();
		for (int i = 0; i < projectNames.length; i++) {
			if (projectNames[i].compareTo(title) == 0)
				return false;
		}
		return true;
	}

	private void centerWindow(Window window) {
		window.setPosition(width / 2 - window.getWidth() / 2, height / 2
				- window.getHeight() / 2);
	}

	private void displayLoadProjectForm() {
		FileHandle[] projectFiles = Gdx.files.internal(projectFolder).list();
		
		if (projectFiles.length == 0) { 
			showInfo("Project folder is empty, nothing to load.");
			return;
		}

		if (wndLoadProject != null) {
			wndLoadProject.setVisible(true);
			centerWindow(wndLoadProject);
			listProjectFiles = new List(projectFiles, skin);
			return;
		}

		wndLoadProject = new Window("Load Project", skin);
		listProjectFiles = new List(projectFiles, skin);
		wndLoadProject.add(new ScrollPane(listProjectFiles));
		wndLoadProject.row().fill().expandX();

		TextButton btnCancel = new TextButton("Cancel", skin);
		TextButton btnLoad = new TextButton("Load", skin);
		wndLoadProject.add(new SplitPane(btnCancel, btnLoad, false, skin));
		btnLoad.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					String projectFilePath = listProjectFiles.getSelection();
					Project project = Project.load(projectFilePath);
					if (project != null) {
						addProject(project);
						wndLoadProject.setVisible(false);
					} else showError("Couldn't load project!");
				}
				return false;
			}
		});
		btnCancel.addListener(new EventListener() {

			@Override
			public boolean handle(Event event) {
				if (event.isHandled()) {
					wndLoadProject.setVisible(false);
				}
				return false;
			}
		});
		stage.addActor(wndLoadProject);
		centerWindow(wndLoadProject);
		wndLoadProject.toFront();
	}
}
