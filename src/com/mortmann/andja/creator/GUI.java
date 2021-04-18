package com.mortmann.andja.creator;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.gamesettings.GameSettings;
import com.mortmann.andja.creator.gamesettings.GenerationInfo;
import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.saveclasses.BaseSave;
import com.mortmann.andja.creator.saveclasses.CombatTypes;
import com.mortmann.andja.creator.saveclasses.Events;
import com.mortmann.andja.creator.saveclasses.Fertilities;
import com.mortmann.andja.creator.saveclasses.Items;
import com.mortmann.andja.creator.saveclasses.Needs;
import com.mortmann.andja.creator.saveclasses.Others;
import com.mortmann.andja.creator.saveclasses.Structures;
import com.mortmann.andja.creator.saveclasses.UnitSave;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.ui.UITab;
import com.mortmann.andja.creator.unitthings.ArmorType;
import com.mortmann.andja.creator.unitthings.DamageType;
import com.mortmann.andja.creator.unitthings.Ship;
import com.mortmann.andja.creator.unitthings.Unit;
import com.mortmann.andja.creator.util.ClassAction;
import com.mortmann.andja.creator.util.ClassAction.ClassType;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.MyInputHandler;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.history.ChangeHistory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

@SuppressWarnings("rawtypes")
public class GUI {
	public static String[] Languages = null;
	public static GUI Instance;
	public Stage mainWindow;
	private Scene scene;
	TabPane workTabs;
	TabPane dataTabs;

	Tab emptyTab;
	Tab addTab;
	public ObservableMap<String,Structure> idToStructures;
	public ObservableMap<String,Fertility> idToFertility;
	public ObservableMap<String,ItemXML> idToItem;
	public ObservableMap<String,DamageType> idToDamageType;
	public ObservableMap<String,ArmorType> idToArmorType;
	public ObservableMap<String,Unit> idToUnit;
	public ObservableMap<String,Need> idToNeed;
	public ObservableMap<String, NeedGroup> idToNeedGroup;
	public ObservableMap<String, PopulationLevel> idToPopulationLevel;
	public ObservableMap<String, Effect> idToEffect;
	private ObservableMap<String, GameEvent> idToGameEvent;
	
	public HashMap<String,UITab> languageToLocalization;
	public HashMap<Class, ObservableMap<String, ? extends Tabable>> classToClassObservableMap;
	public HashMap<Class, GameSettings> gameSettingsClassToTab;
	HashMap<Tabable,Tab> tabableToTab;
	HashMap<Tab,Tabable> tabToTabable;
	HashMap<Tab,String> tabToID;
	HashMap<Class,DataTab> classToDataTab;
	HashMap<Tabable,WorkTab> tabableToWorkTab; 
	HashMap<Tabable,Tabable> originalToTemporary;
	private ArrayList<ClassAction> ClassActions;
	
	public void start(Stage primaryStage) {
        Instance = this;
		LoadSettings();
        primaryStage.addEventHandler(EventType.ROOT,new MyInputHandler());
        primaryStage.setTitle("Andja XML Creator Version 0.1 Unstable");
        scene = new Scene(new VBox(),1600,900);
        scene.getStylesheets().add("bootstrap3.css");
		mainWindow = primaryStage;
		Languages = BaseSave.GetLocalizationFileNames();
		
		new ChangeHistory();

		tabToID = new HashMap<>();
		tabableToWorkTab = new HashMap<>();
        tabToTabable = new HashMap<>();
        tabableToTab = new HashMap<>();
        originalToTemporary = new HashMap<>();
        //If it is a new Type create new OBSERVABLE MAP 
        //THEN you also need to create a save function & add it to SaveCurrentTab&SaveData!
        //ALSO need to be added to loadData Function
        idToStructures = FXCollections.observableHashMap();
        idToArmorType = FXCollections.observableHashMap();
        idToDamageType = FXCollections.observableHashMap();
        idToUnit = FXCollections.observableHashMap();
        idToFertility = FXCollections.observableHashMap();
        idToItem = FXCollections.observableHashMap();
        idToNeed = FXCollections.observableHashMap();
        idToNeedGroup = FXCollections.observableHashMap();
        idToPopulationLevel = FXCollections.observableHashMap();
        idToEffect = FXCollections.observableHashMap();
        idToGameEvent = FXCollections.observableHashMap();
        
        classToClassObservableMap = new HashMap<>();
        
        //FOR NEW Type of Class add a idToObject Map 
        classToClassObservableMap.put(Structure.class, idToStructures);
        classToClassObservableMap.put(ArmorType.class, idToArmorType);
        classToClassObservableMap.put(DamageType.class, idToDamageType);
        classToClassObservableMap.put(Unit.class, idToUnit);
        classToClassObservableMap.put(Fertility.class, idToFertility);
        classToClassObservableMap.put(Need.class, idToNeed);
        classToClassObservableMap.put(ItemXML.class, idToItem);
        classToClassObservableMap.put(NeedGroup.class, idToNeedGroup);
        classToClassObservableMap.put(PopulationLevel.class, idToPopulationLevel);
        classToClassObservableMap.put(Effect.class, idToEffect);
        classToClassObservableMap.put(GameEvent.class, idToGameEvent);
        //Then add a class Action for it
        ClassActions = new ArrayList<ClassAction>();
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Production", Production.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "NeedStructure", NeedStructure.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Farm", Farm.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Growable", Growable.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Home", Home.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Market", Market.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Warehouse", Warehouse.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Mine", Mine.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "Road", Road.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "MilitaryStructure", MilitaryStructure.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Structure, "ServiceStructure", ServiceStructure.class));

		ClassActions.add(new ClassAction(ClassAction.ClassType.Unit, "Unit", Unit.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Unit, "ServiceStructure", Ship.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Unit, "ArmorType", ArmorType.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Unit, "DamageType", DamageType.class));

		ClassActions.add(new ClassAction(ClassAction.ClassType.Others, "Item", ItemXML.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Others, "Fertility", Fertility.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Others, "Need", Need.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Others, "NeedGroup", NeedGroup.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Others, "PopulationLevel", PopulationLevel.class));

		ClassActions.add(new ClassAction(ClassAction.ClassType.Event, "Effect", Effect.class));
		ClassActions.add(new ClassAction(ClassAction.ClassType.Event, "GameEvent", GameEvent.class));
		
		SetUpMenuBar();
        LoadData();
        
        classToDataTab = new HashMap<>();
        dataTabs = new TabPane();
        DataTab<Structure> d1 = new DataTab<>("Structures",idToStructures, dataTabs);
        classToDataTab.put(Structures.class, d1);
        DataTab<ItemXML> d2 = new DataTab<>("Items",idToItem, dataTabs);
        classToDataTab.put(ItemXML.class, d2);
        DataTab<Fertility> d3 = new DataTab<>("Fertilities",idToFertility, dataTabs);
        classToDataTab.put(Fertility.class, d3);
        DataTab<Unit> d4 = new DataTab<>("Unit",idToUnit, dataTabs);
        classToDataTab.put(Unit.class, d4);
        DataTab<DamageType> d5 = new DataTab<>("DamageType",idToDamageType, dataTabs);
        classToDataTab.put(DamageType.class, d5);
        DataTab<ArmorType> d6 = new DataTab<>("ArmorType",idToArmorType, dataTabs);
        classToDataTab.put(ArmorType.class, d6);
        DataTab<Need> d7 = new DataTab<>("Need",idToNeed, dataTabs);
        classToDataTab.put(Need.class, d7);
        DataTab<NeedGroup> d8 = new DataTab<>("NeedGroup", idToNeedGroup, dataTabs);
        classToDataTab.put(NeedGroup.class, d8);
        DataTab<PopulationLevel> d9 = new DataTab<>("PopulationLevel", idToPopulationLevel, dataTabs);
        classToDataTab.put(PopulationLevel.class, d9);
        DataTab<Effect> d10 = new DataTab<>("Effect", idToEffect, dataTabs);
        classToDataTab.put(Effect.class, d10);
        DataTab<GameEvent> d11 = new DataTab<>("GameEvent", idToGameEvent, dataTabs);
        classToDataTab.put(GameEvent.class, d11);

		workTabs = new TabPane();
		workTabs.setOnMouseClicked(new EventHandler<MouseEvent>(){
	          @Override
	          public void handle(MouseEvent event) {
	        	  if(event.getButton() == MouseButton.MIDDLE && event.getEventType() == MouseEvent.MOUSE_RELEASED) {
	        		  EventHandler<Event> handler = ((Tab)event.getSource()).getOnClosed();
	        	        if (null != handler) {
	        	            handler.handle(null);
	        	        } else {
	        	        	workTabs.getTabs().remove(((Tab)event.getSource()));
	        	        }
	        	  }
	          }
	      });
		workTabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
				if(newTab == null)
					return;
				if(newTab == emptyTab)
					return;
				if(newTab instanceof WorkTab)
					ChangeHistory.SetCurrentTab(((WorkTab)newTab).getTabable());
				else 
					ChangeHistory.SetCurrentTab(newTab);
			}
		});
		workTabs.setMaxHeight(Double.MAX_VALUE);
		AddEmptyTab();		
        GridPane hb = new GridPane();
        hb.add(dataTabs,0,0);
        hb.add(workTabs,1,0);
        ColumnConstraints col1 = new ColumnConstraints();
        float percentage = 26.5f;
        col1.setPercentWidth(percentage);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(100-percentage);
        col2.setHgrow(Priority.ALWAYS);
        hb.getColumnConstraints().addAll(col1,col2);

        mainWindow.setScene(scene);
		mainWindow.show();
		((VBox) scene.getRoot()).getChildren().addAll(hb);
		VBox.setVgrow(workTabs, Priority.ALWAYS);
		mainWindow.setOnCloseRequest(x->{
			for(Tab t : workTabs.getTabs()){
				if(t.getText().contains("*")==false){
					continue;
				}
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Warning!");
				String s = "Any unsaved data will be lost!";
				alert.setContentText(s);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {

				} else {
					x.consume();
				}
			}
		});
		
        languageToLocalization = new HashMap<>();
	}
	public Node getRoot() {
		return scene.getRoot();
	}

	private void LoadData(){
		Structures.Load(idToStructures);
		Items.Load(idToItem);
		UnitSave.Load(idToUnit);
		Fertilities.Load(idToFertility);
		Needs.Load(idToNeed, idToNeedGroup);
		Events.Load(idToEffect, idToGameEvent);
		CombatTypes.Load(idToArmorType, idToDamageType);
		Others.Load(idToPopulationLevel);
//        HashSet<Tabable> allTabables = new HashSet<>();
//		for(ObservableMap<String, ? extends Tabable> map : classToClassObservableMap.values()) {
//			allTabables.addAll(map.values());
//		}
//		ArrayList<ItemXML> items = new ArrayList<>(idToItem.values());
//		for(ItemXML item : items) {
//			if(item.ID.contains(" ") || item.ID.contains("´") || item.ID.contains("'") || item.ID.contains("-") ) {
//				String newID = item.ID;
//				String id = item.ID;
//				newID = newID.replaceAll("[\\p\\s{Punct}&&[^_]]+", "");
//				item.ID = newID;
//				//ID Changed so we need to change all references
//				//just go through all -- even tho it isnt optimal but easier for nows
//				for(Tabable t : allTabables) {
//					t.UpdateDependables(item, id);
//				}
//				System.out.println("Updated: " + id + " to " + newID);
//				idToItem.remove(id);
//				idToItem.put(newID, item);
//			}
//		}
//        System.out.println(".-	sweasdr_asdw 	 !e$$%67 ".replaceAll("[\\p{Punct}\\s&&[^_]]+", ""));
//        SaveData();
	}
	public UITab LoadLocalization(String language) {
		return UITab.Load(language);
	}
	public void SaveLocalization(String lang) {
		if(languageToLocalization==null)
			return; // should only happen when MANUEL programmed changes saved on startup
		UITab tab = languageToLocalization.get(lang);
		if(tab==null)
			return;
        tab.Save();
	}
	
	
	public void AddWorkTab(Tabable tabable, boolean newAdded) {
		if(tabable!=null&&tabableToTab.containsKey(tabable)) {
			workTabs.getSelectionModel().select(tabableToTab.get(tabable));
			return;
		}
		if(newAdded == false) {
			Serializer serializer = new Persister(new AnnotationStrategy());
			StringWriter writer = new StringWriter();
			try {
				serializer.write(tabable, writer);
				Tabable newTabable = tabable.getClass().getDeclaredConstructor().newInstance();
				newTabable = serializer.read(newTabable, writer.toString());
				originalToTemporary.put(tabable, newTabable);
				tabable = newTabable;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		WorkTab workTab = new WorkTab(tabable, newAdded);
		ChangeHistory.AddObject(tabable);
		tabableToWorkTab.put(tabable, workTab);
		
		if(workTabs.getTabs().contains(emptyTab)){
			workTabs.getTabs().remove(emptyTab);
		}
		tabToTabable.put(workTab, tabable);
		tabableToTab.put(tabable, workTab);
		tabToID.put(workTab, tabable.GetID());
		
		workTabs.getTabs().add(workTab);
		workTabs.getSelectionModel().select(workTab);
		if(workTabs.getTabs().contains(addTab)) {
			workTabs.getTabs().sort(new Comparator<Tab>() {
				@Override
				public int compare(Tab o1, Tab o2) {
					if(o1 == addTab)
						return 1;
					if(o2 == addTab)
						return -1;
					return 0;
				}
				
			});
		} else
		if(workTabs.getTabs().contains(emptyTab) == false) {
			addTab = new Tab("+");
			addTab.setClosable(false);
			
			addTab.setOnSelectionChanged((x)->{
				if(addTab.isSelected()==false)
					return;
				Platform.runLater(()-> {
					workTabs.getTabs().remove(addTab);
					AddEmptyTab();
					workTabs.getSelectionModel().select(emptyTab);
				});
			});
			workTabs.getTabs().add(addTab);
		}
	}
	
	
	private void AddEmptyTab() {
		emptyTab = new Tab("Empty");
		emptyTab.setClosable(false);
		FlowPane flow = new FlowPane();

		HashMap<ClassAction.ClassType,FlowPane> typeToFlow = new HashMap<>();
		HashMap<FlowPane,TitledPane> typeToTitled = new HashMap<>();

		for(ClassAction ac : ClassActions) {
			if(ac.type == ClassType.Localization)
				continue;
			if(ac.type == ClassType.GameSettings)
				continue;
			if(ac.Class == null)
				continue;
			if(typeToFlow.containsKey(ac.type) == false) {
				FlowPane f = new FlowPane();
				f.setMinWidth(130 * 4);
				typeToFlow.put(ac.type, f);
				typeToTitled.put(f, new TitledPane(ac.type.toString(),f));
			}
			typeToFlow.get(ac.type).getChildren().add(SetupEmptyTabButton(ac.Class));
		}
		ArrayList<FlowPane> flows = new ArrayList<>(typeToFlow.values());
		flows.forEach(x->x.autosize());
		flows.sort((x,y)->{
			return Double.compare(y.getHeight(),x.getHeight());
		});
		for(int i = 0; i < flows.size(); i+=2) {
			FlowPane fp1 = flows.get(i);
			FlowPane fp2 = flows.get(i+1);
			fp1.autosize();
			fp2.setMinHeight(fp1.getHeight()+20);// +20 to fix size difference -- no clue why needed? maybe title?
			flow.getChildren().addAll(typeToTitled.get(fp1),typeToTitled.get(fp2));
		}

		emptyTab.setContent(flow);
		workTabs.getTabs().add(emptyTab);
	}
	public Button SetupEmptyTabButton(Class c) {
		Button b = new Button();
		b.setOnAction(x->{DoClassAction(c);});
		b.setText(c.getSimpleName());
		b.setMinSize(125, 125);
		b.setPrefSize(125, 125);
		b.setMaxSize(125, 125);
		return b;
	}
	private void SetUpMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu f = new Menu("File");
        menuBar.getMenus().add(f);
		MenuItem files = new MenuItem("Save Files");
		files.setOnAction(x->{SaveData();});
		MenuItem export = new MenuItem("Export Files to Game");
		export.setOnAction(x->{ExportData();});
		MenuItem settings = new MenuItem("Settings");
		settings.setOnAction(x->{ ShowSettings(); });
		SeparatorMenuItem line = new SeparatorMenuItem();
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(x->{ System.exit(0); });
		f.getItems().addAll(files,export,settings,line,exit); 
		
		for(String l : Languages) {
			ClassActions.add(new ClassAction(ClassAction.ClassType.Localization, l, l));
		}
		
		ClassActions.add(new ClassAction(ClassAction.ClassType.GameSettings, GenerationInfo.class.getSimpleName(), GenerationInfo.class));

		HashMap<ClassAction.ClassType,Menu> typeToMenu = new HashMap<>();
		for(ClassAction action : ClassActions) {
			if(typeToMenu.containsKey(action.type) == false) {
				typeToMenu.put(action.type, new Menu());
		        menuBar.getMenus().add(typeToMenu.get(action.type));
		        typeToMenu.get(action.type).setGraphic(new Label("New " + action.type.toString()));
			}
			MenuItem item = new MenuItem();
			Label label = new Label(action.Name);
			label.setTextFill(Color.BLACK);
			item.setGraphic(label);
			Platform.runLater(()->{
				Label l = (Label) typeToMenu.get(action.type).getGraphic();
				label.setMinWidth(l.getWidth());
			});
			
			if(action.Class != null ) {
				if(action.type != ClassAction.ClassType.GameSettings) {
					item.setOnAction(x->{DoClassAction(action.Class);});
				} else {
					item.setOnAction(x->{DoGameSettingAction(action.Class);});
				}
			} else {
				item.setOnAction(x->{LocalizationAction(action.language);});
			}
			typeToMenu.get(action.type).getItems().add(item);
		}
		
		((VBox) scene.getRoot()).getChildren().addAll(menuBar);
	}
	private void DoGameSettingAction(Class class1) {
		if(class1 != GenerationInfo.class) {
			System.out.println("ERROR -- not supported.");
			return;
		}
		if(gameSettingsClassToTab == null)
			gameSettingsClassToTab = new HashMap<Class, GameSettings>();
		if(workTabs.getTabs().contains(emptyTab)){
			workTabs.getTabs().remove(emptyTab);
		}
		GenerationInfo tab = GenerationInfo.Load();
		ChangeHistory.AddObject(tab);
		if(tab == null)
			return;
		for(Tabable t : tabToTabable.values()) {
			if(t.getClass() == class1) {
				return;
			}
		}
		workTabs.getTabs().add(tab);
		workTabs.getSelectionModel().select(tab);
		gameSettingsClassToTab.put(class1, tab);
		
	}
	private Settings ShowSettings() {
		LoadSettings();
		Settings.ShowSettingsDialog();
		File file = Paths.get("settings").toFile();
        try {
            Serializer serializer = new Persister(new AnnotationStrategy());
			file.createNewFile();
			Settings settings = new Settings();
			serializer.write(settings, file);
			return settings;
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Couldn't Save Settings!");
			e.printStackTrace();
			a.show();
			return null;
		}
	}
	
	private Settings LoadSettings() {
		File file = Paths.get("settings").toFile();
        Serializer serializer = new Persister(new AnnotationStrategy());
		if(file!=null&&file.exists()) {
			Settings outSettings = new Settings();
			try {
				serializer.read(outSettings, file);
				return outSettings;
			} catch (Exception e1) {
				file.delete();
				return null;
			} 
		}
		return null;
	}
	
	private void ExportData() {
		Settings settings = LoadSettings();
		if(settings == null) {
			settings = ShowSettings();
			if(settings == null)
				return;
		}
		File folder = new File(BaseSave.saveFilePath);
		File[] listOfFiles = folder.listFiles();
		try {
			for(File f : listOfFiles) {
				Path newPath = null;
				String ext = f.getName().substring(f.getName().indexOf("."));
				if(ext.equalsIgnoreCase(BaseSave.GameStateExtension)) {
					newPath = Paths.get(Settings.exportPath, "GameState", f.getName());
				} else
				if(ext.equalsIgnoreCase(BaseSave.LocalizationExtension)) {
					newPath = Paths.get(Settings.exportPath, "Localizations", f.getName());
				} else {
					System.out.println("Unkown File " + f.getName());
					continue;
				}
				File file = newPath.toFile();
				if(file.exists()) {
					Path backUP = Paths.get(Settings.exportPath, BaseSave.backuppath, f.getName());
					if(Files.exists(backUP.getParent()) == false) {
						backUP.getParent().toFile().mkdirs();
					}
					if(backUP.toFile().exists()) {
						backUP.toFile().delete();
					}
				    Files.copy(Paths.get(file.getPath()), backUP);
				    file.delete();
				}
			    Files.copy(Paths.get(f.getPath()), newPath);

			
			}
		} catch (IOException e) {
			e.printStackTrace();
		    Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Failure!");
			alert.setHeaderText("Export failed:");
			alert.setContentText(e.getCause().getLocalizedMessage());
			alert.showAndWait();
		    return;
		} 
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success!");
		alert.setHeaderText("Export was successfully executed!");
		alert.setContentText("Export to\n" + Settings.exportPath);
		alert.showAndWait();

	}

	private void LocalizationAction(String l) {
		if(workTabs.getTabs().contains(emptyTab)){
			workTabs.getTabs().remove(emptyTab);
		}
		UITab tab = LoadLocalization(l);
		ChangeHistory.AddObject(tab);
		if(tab == null)
			return;
		for(Tabable t : tabToTabable.values()) {
			if(t instanceof UITab) {
				if(((UITab)t).language.contentEquals(l.toString()))
					return;
			}
		}
		tabToTabable.put(tab, tab);
		workTabs.getTabs().add(tab);
		workTabs.getSelectionModel().select(tab);
		languageToLocalization.put(l, tab);
	}

	private void SaveData() {
		SaveStructures();
		SaveItems();
		SaveFertilities();
		SaveCombat();
		SaveUnits();
		SaveNeeds();
		SaveOthers();
		SaveEvents();
		for(String l : languageToLocalization.keySet()) {
			SaveLocalization(l);
		}
		SaveGameSettings();
	}
	
	private void SaveGameSettings() {
		if(gameSettingsClassToTab == null)
			return;
		for (GameSettings settings : gameSettingsClassToTab.values()) {
			settings.Save();
		}
	}
	private boolean SaveOthers() {
		Others o = new Others(idToPopulationLevel.values());
		return o.Save();
	}
	private boolean SaveNeeds() {
		Needs n = new Needs(idToNeed.values(), idToNeedGroup.values());
		return n.Save();
	}
	private boolean SaveUnits() {
		UnitSave us = new UnitSave(idToUnit.values()); 
		return us.Save();
	}
	private boolean SaveCombat() {
		CombatTypes ct = new CombatTypes(idToArmorType.values(), idToDamageType.values());
		return ct.Save();
	}
	private boolean SaveFertilities() {
		Fertilities f = new Fertilities(idToFertility.values());
		return f.Save();
	}
	private boolean SaveItems() {
		Items i = new Items(idToItem.values());
		return i.Save();
	}
	private boolean SaveStructures() {
		Structures s = new Structures(idToStructures.values());
		return s.Save();
	}
	private boolean SaveEvents() {
		Events e = new Events(idToEffect.values(), idToGameEvent.values());
		return e.Save();
	}
	@SuppressWarnings("unchecked")
	private void DoClassAction(Class c){
		try {
			AddWorkTab((Tabable) c.getConstructor().newInstance(), true);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void SaveCurrentTab(){
		Tab curr = GetCurrentTab();
		Tabable currTabable = tabToTabable.get(curr);
		if(currTabable instanceof UITab) {
			SaveLocalization(((UITab)currTabable).language);
			ChangeHistory.ObjectSaved(currTabable);
			curr.setText(curr.getText().replaceAll("\\*", ""));
			return;
		}
		if(curr instanceof GameSettings) {
			((GameSettings)curr).Save();
			ChangeHistory.ObjectSaved(curr);
			curr.setText(curr.getText().replaceAll("\\*", ""));
			return;
		}
		for(WorkTab tab : tabableToWorkTab.values()) {
			tab.UpdateMethods(null);
		}
//		if(tabableToWorkTab.get(currTabable)!=null)
//			tabableToWorkTab.get(currTabable).UpdateFields();
		
		curr.setText(currTabable.GetName());
		//check if its filled out all required
		ArrayList<Field> missingFields = CheckForMissingFields(currTabable);
		if(missingFields.isEmpty() == false){
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			String missingstring = "";
			for(Field f : missingFields) {
				missingstring += missingstring.isEmpty()? "" : ", ";
				missingstring += f.getName();
			}
			a.setContentText("Can´t save data! Fill all required data out!\n 	Following Fields are not filled out:\n"  + missingstring +"!");
			a.show();
			//its missing smth return error
			return;
		}
		
		HashSet<Tabable> allTabables = new HashSet<>();
		for(ObservableMap<String, ? extends Tabable> map : classToClassObservableMap.values()) {
			allTabables.addAll(map.values());
		}
		if(tabToID.get(curr)!=null && currTabable.GetID() != tabToID.get(curr)) {
			classToClassObservableMap.get(currTabable.getClass()).remove(tabToID.get(curr));
			//ID Changed so we need to change all references
			//just go through all -- even tho it isnt optimal but easier for nows
			for(Tabable t : allTabables) {
				t.UpdateDependables(currTabable, tabToID.get(curr));
			}
			SaveData(); //changed stuff -- not gonna reload tabs
		}
		boolean exist = doesIDexistForTabable(currTabable.GetID(),currTabable);
		if(exist){
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setTitle("ID already exists!");
//			Tabable t = doesIDexistForTabable(o.GetID(),o);
			allTabables.removeIf(x->x.DependsOnTabable(currTabable)==null);


			String depends = "Other depend on it!\nRemove dependencies from ";
			if(allTabables.size()==0){
				depends="";
			} else {
				a.setAlertType(AlertType.ERROR);
				for (Tabable st : allTabables) {
					depends +=st.toString() + ", ";
				}
				depends=depends.substring(0, depends.length()-2);
				depends+=".";
			}
			
			a.setContentText("Overwrite existing Data?\n"+depends);
			Optional<ButtonType> result = a.showAndWait();
			if(a.getAlertType()==AlertType.ERROR){
				return;
			}
			if (result.isPresent() && result.get() == ButtonType.OK) {
				//you want to overwrite data so dont do anything
			} else {
				//doesnt want to overwrite
				return;
			}
		}
		tabToID.put(curr, currTabable.GetID());
		if(currTabable.GetID().trim().isEmpty()){
			return;
		}
		boolean saved=false;
		if(currTabable instanceof Structure){
			idToStructures.put(((Structure)currTabable).GetID(),((Structure)currTabable));
			saved = SaveStructures();
		}
		else if(currTabable instanceof Item){
			idToItem.put(((ItemXML)currTabable).GetID(), ((ItemXML)currTabable));
			saved = SaveItems();
		}
		else if(currTabable instanceof Fertility){
			idToFertility.put(((Fertility)currTabable).GetID(), ((Fertility)currTabable));
			saved = SaveFertilities();
		}
		else if(currTabable instanceof Unit){
			idToUnit.put(((Unit)currTabable).GetID(), ((Unit)currTabable));
			saved = SaveUnits();
		}
		else if(currTabable instanceof DamageType){
			idToDamageType.put(((DamageType)currTabable).GetID(), ((DamageType)currTabable));
			saved = SaveCombat();
		}
		else if(currTabable instanceof ArmorType){
			idToArmorType.put(((ArmorType)currTabable).GetID(), ((ArmorType)currTabable));
			saved = SaveCombat();
		}
		else if(currTabable instanceof Need){
			idToNeed.put(((Need)currTabable).GetID(), ((Need)currTabable));
			saved = SaveNeeds();
		}
		else if(currTabable instanceof NeedGroup){
			idToNeedGroup.put(((NeedGroup)currTabable).GetID(), ((NeedGroup)currTabable));
			saved = SaveNeeds();
		}
		else if(currTabable instanceof PopulationLevel){
			idToPopulationLevel.put(""+((PopulationLevel)currTabable).LEVEL, ((PopulationLevel)currTabable));
			saved = SaveOthers();
		}
		else if(currTabable instanceof Effect) {
			idToEffect.put(((Effect)currTabable).GetID(), ((Effect)currTabable));
			saved = SaveEvents();
		}
		else if(currTabable instanceof GameEvent) {
			idToGameEvent.put(((GameEvent)currTabable).GetID(), ((GameEvent)currTabable));
			saved = SaveEvents();
		}
		if(saved)
			ChangeHistory.ObjectSaved(currTabable);
		if(ChangeHistory.IsSaved(currTabable) && saved){
			curr.setText(curr.getText().replaceAll("\\*", ""));
		} 
	}
	
	private ArrayList<Field> CheckForMissingFields(Tabable t) {
		ArrayList<Field> missings = new ArrayList<>();
		Field[] fs = t.getClass().getFields();
		for (Field field : fs) {
			if(field.isAnnotationPresent(FieldInfo.class)==false){
				continue;
			}
			FieldInfo fi = field.getAnnotation(FieldInfo.class);
			if(fi.required()==false){
				continue;
			}
			try {
				Object o = field.get(t);
				if(o == null){
					missings.add(field);
				}
				if(field.getType()==Integer.class){
					if((int)o==-1){
						missings.add(field);
					}
				}
				if(Collection.class.isAssignableFrom(field.getType())){
					if(((Collection)o).isEmpty()){
						missings.add(field);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return missings;
	}

	public void changedCurrentTab(boolean isSaved) {
		if(isSaved) {
			GetCurrentTab().setText(GetCurrentTab().getText().replaceAll("\\*", ""));
		} else {
			if(GetCurrentTab().getText().contains("*")){
				return;
			}
			if(GetCurrentTab() == emptyTab){
				return;
			}
			GetCurrentTab().setText("*"+GetCurrentTab().getText());
		}
	}
	public Tab GetCurrentTab(){
		return workTabs.getSelectionModel().getSelectedItem();
	}
	
	public boolean doesIDexistForTabable(String valueSafe, Tabable tab) {
		Class c = tab.getClass();
		if(Structure.class.isAssignableFrom(c)){
			c = Structure.class;
		}
		if(Unit.class.isAssignableFrom(c)){
			c = Unit.class;
		}
		if(classToClassObservableMap.containsKey(c) == false) {
			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
			return false;
		}
		Tabable exist = (Tabable) classToClassObservableMap.get(c).get(valueSafe);
		return exist!=null && originalToTemporary.get(exist)!=tab;
	}
	
	public void RemoveTab(Tab tab, Object object) {
		tabToTabable.remove(tab);
		tabToID.remove(tab);
		if(object instanceof Tabable) {
			tabableToTab.remove((Tabable)object);
		}
		ChangeHistory.RemoveObject(object, true);
		if(workTabs.getTabs().size()<1&&workTabs.getTabs().contains(emptyTab) == false){
			AddEmptyTab();
		}
	}

	public void UpdateCurrentTab() {
		Tab tab = workTabs.getSelectionModel().getSelectedItem();
		if(tab instanceof UITab)
			return;
		if(tabToTabable.containsKey(tab))
			tabableToWorkTab.get(tabToTabable.get(tab)).UpdateMethods(null);
	}
	
	public ObservableMap<String, ? extends Tabable> GetObservableList(Class classTabable) {
		if(classToClassObservableMap.containsKey(classTabable) == false) {
			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
			return null;
		}
		if(Structure.class.isAssignableFrom(classTabable))
			classTabable = Structure.class;
		if(Unit.class.isAssignableFrom(classTabable)){
			classTabable = Unit.class;
		}
		return classToClassObservableMap.get(classTabable);
	}
	
	public Need[] GetNeedsRequiringOutput(Item output) {
		if(output == null) {
			return null;
		}
		ArrayList<Need> all = new ArrayList<Need>(idToNeed.values());
		all.removeIf(n->n.item == null||n.item.GetID().contentEquals(output.GetID())==false);
		return all.toArray(new Need[all.size()]);
	}
	public ArrayList<OutputStructure> GetOutputStructures(Item item) {
		ArrayList<Structure> all = new ArrayList<>(idToStructures.values());
		all.removeIf(x->x instanceof OutputStructure == false);
		@SuppressWarnings("unchecked")
		ArrayList<OutputStructure> outs = (ArrayList)all;//new ArrayList<>(all.to);
		outs.removeIf(x-> x.output == null || Arrays.asList(x.output).stream().anyMatch(y->y.GetID() == item.GetID()) == false);
		return outs;
	}
	public ArrayList<Structure> getStructureList(Class class1) {
		ArrayList<Structure> list = new ArrayList<>();
		list.addAll(idToStructures.values());
		list.removeIf(x->x.getClass()!=class1);
		return list;
	}
	public ArrayList<Tabable> getTabableList(Class class1) {
		return new ArrayList<>(classToClassObservableMap.get(class1).values());
	}
	public ArrayList<Unit> getUnits() {
		ArrayList<Unit> al = new ArrayList<>();
		for (Unit u : idToUnit.values()) {
			al.add(u);
		}
		Collections.sort(al);
		return al;
	}
	public ArrayList<Item> getItems() {
		ArrayList<Item> al = new ArrayList<>();
		for (ItemXML i : idToItem.values()) {
			al.add( new Item(i) );
		}
		al.sort(new Comparator<Item>(){
            public int compare(Item i1,Item i2){
    			return i1.GetID().compareTo(i2.GetID());

          }});
		return al;
	}

	public boolean doesIDexistForTabable(int value, Tabable t) {
		return doesIDexistForTabable(value+"", t);
	}
	public Scene getScene() {
		return scene;
	}
	
}