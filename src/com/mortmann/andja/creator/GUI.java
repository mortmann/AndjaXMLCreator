package com.mortmann.andja.creator;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.saveclasses.CombatTypes;
import com.mortmann.andja.creator.saveclasses.Events;
import com.mortmann.andja.creator.saveclasses.Fertilities;
import com.mortmann.andja.creator.saveclasses.Items;
import com.mortmann.andja.creator.saveclasses.Needs;
import com.mortmann.andja.creator.saveclasses.Others;
import com.mortmann.andja.creator.saveclasses.Structures;
import com.mortmann.andja.creator.saveclasses.UnitSave;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.ui.UIElement;
import com.mortmann.andja.creator.ui.UIElementText;
import com.mortmann.andja.creator.ui.UILanguageLocalizations;
import com.mortmann.andja.creator.ui.UITab;
import com.mortmann.andja.creator.unitthings.ArmorType;
import com.mortmann.andja.creator.unitthings.DamageType;
import com.mortmann.andja.creator.unitthings.Ship;
import com.mortmann.andja.creator.unitthings.Unit;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.MyInputHandler;
import com.mortmann.andja.creator.util.Tabable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventType;
@SuppressWarnings("rawtypes")
public class GUI {
	static final String saveFilePath = "Latest/"; 
	public enum Language {English, German}
	public static GUI Instance;
	private Stage mainWindow;
	private BorderPane mainLayout;
	private Scene scene;
	TabPane workTabs;
	TabPane dataTabs;

	Tab emptyTab;
	
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
	
	public HashMap<Language,UITab> languageToLocalization;
	public HashMap<Class, ObservableMap<String, ? extends Tabable>> classToClassObservableMap;
	HashMap<Tabable,Tab> tabableToTab;
	HashMap<Tab,Tabable> tabToTabable;
	HashMap<Tab,String> tabToID;

	HashMap<Class,DataTab> classToDataTab;
	
	
	public void start(Stage primaryStage) {
        Instance = this;
        primaryStage.addEventHandler(EventType.ROOT,new MyInputHandler());
        primaryStage.setTitle("Andja XML Creator Version 0.1 Unstable");
        scene = new Scene(new VBox(),1600,900);
        scene.getStylesheets().add("bootstrap3.css");
		mainWindow = primaryStage;
		mainLayout = new BorderPane();
		SetUpMenuBar();

		new TestMapGenerator();
		tabToID = new HashMap<>();
        tabToTabable = new HashMap<>();
        tabableToTab = new HashMap<>();
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
		workTabs.setMaxHeight(Double.MAX_VALUE);
		AddTab(null,mainLayout);
		
        GridPane hb = new GridPane();
        hb.add(dataTabs,0,0);
        hb.add(workTabs,1,0);
        ColumnConstraints col1 = new ColumnConstraints();
        int percentage = 26;
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
        for(Language l : Language.values())
        	LoadMissingUIData(l);
		
	}

	private void LoadData(){
		Serializer serializer = new Persister(new AnnotationStrategy());
        Structures s = new Structures();
		try {
			serializer.read(s, Paths.get(saveFilePath, "structures.xml").toFile());
			for (Structure i : s.GetAllStructures()) {
				idToStructures.put(i.GetID(), i);
			} 
		} catch (Exception e1) {
			e1.printStackTrace();
			idToStructures = FXCollections.observableHashMap();
		}        
		
        try {
			Items e = serializer.read(Items.class, Paths.get(saveFilePath, "items.xml").toFile());
			for (ItemXML i : e.items) {
				idToItem.put(i.GetID(), i);
			}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			UnitSave e = serializer.read(UnitSave.class, Paths.get(saveFilePath, "units.xml").toFile());
			for (Unit u : e.getAllUnits()) {
				idToUnit.put(u.GetID(), u);
			}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			Needs e = serializer.read(Needs.class, Paths.get(saveFilePath, "needs.xml").toFile());
			if(e.needs!=null)
			for (Need u : e.needs) {
				idToNeed.put(u.GetID(), u);
			}
			if(e.groupNeeds!=null)
			for (NeedGroup u : e.groupNeeds) {
				idToNeedGroup.put(u.GetID(), u);
			}

			SaveNeeds();
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			CombatTypes e = serializer.read(CombatTypes.class, Paths.get(saveFilePath, "combat.xml").toFile());
			if(e.damageTypes!=null)
				for (DamageType u : e.damageTypes) {
					idToDamageType.put(u.GetID(), u);
				}
			if(e.armorTypes!=null)
				for (ArmorType u : e.armorTypes) {
					idToArmorType.put(u.GetID(), u);
				}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			Others e = serializer.read(Others.class, Paths.get(saveFilePath, "other.xml").toFile());
			if(e.populationLevels!=null)
				for (PopulationLevel u : e.populationLevels) {
					idToPopulationLevel.put(""+u.LEVEL, u);
				}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			Fertilities e = serializer.read(Fertilities.class, Paths.get(saveFilePath, "fertilities.xml").toFile() );
			for (Fertility i : e.fertilities) {
				idToFertility.put(i.GetID(), i);
			}
        } catch (Exception e) {
			e.printStackTrace();
        	idToFertility = FXCollections.observableHashMap();
		}
        try {
			Events e = serializer.read(Events.class, Paths.get(saveFilePath, "events.xml").toFile());
			if(e.effects!=null)
				for (Effect u : e.effects) {
					idToEffect.put(u.GetID(), u);
				}
			if(e.gameEvents!=null)
				for (GameEvent u : e.gameEvents) {
					idToGameEvent.put(u.GetID(), u);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
        SaveData();
	}
	public UITab LoadLocalization(Language language) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		String filename ="localization-"+ language +".xml";
        UITab tab = new UITab();
		try {
			tab = serializer.read(tab, Paths.get(saveFilePath, filename).toFile());
		} catch (Exception e1) {
			e1.printStackTrace();
			tab = new UITab(new HashMap<>(), language);
		}    
		HashMap<String,UIElement> missing = LoadMissingUIData(language);
		if(missing == null)
			return tab;
		HashMap<String,UIElement> inTab = new HashMap<>(); 
		for(UIElement element : tab.elements) {
			inTab.put(element.name, element);
		}
		for(String name : missing.keySet()) {
			if(inTab.containsKey(name)) {
				inTab.get(name).AddMissing(missing.get(name).childs);
			} else {
				inTab.put(name, missing.get(name));
			}
		}
		return new UITab(inTab, language);
	}
	
	
	public HashMap<String,UIElement> LoadMissingUIData(Language language) {
		AnnotationStrategy as = new AnnotationStrategy();
		Serializer serializer = new Persister(as);
		ArrayList<String> strings = new ArrayList<>();		
//		for(Language l : Language.values()) {
		UILanguageLocalizations missings = new UILanguageLocalizations();
		try {
			serializer.read(missings, new File("Missing-UI-Localization-"+language));
		} catch (Exception e) {
//			continue;
			return null;
		}
		strings.addAll(missings.missingLocalization);
//		}
		HashMap<String,UIElement> map = new HashMap<>();
		for(String s : strings) {
			String[] parts = s.split("/");
			UIElement element = new UIElement(null,null);
			String key = parts[0];
			if(map.containsKey(key)) {
				element = map.get(key);
			} else {
				if(parts.length>2)
					element = new UIElement(key,null);
				else
					element = new UIElementText(key,null);
				map.put(key,element);
			}
			if(parts.length<=2) //when it is max 2 -> its already end node 
				continue;
			
			for (int i = 1; i < parts.length; i++) {
				key = parts[i];
				if( i == parts.length-2){
					element.SetUIElementText(key,parts[i+1]);
					break;
				} else {
					element = element.GetUIElement(key);
				}
			}
		}
//		UITab tab = new UITab(map);
//		Tab t = new Tab("Localization");
//		t.setContent(tab.scroll);
//		workTabs.getTabs().add(t);
		return map;
	}
	public void SaveLocalization(Language lang) {
		if(languageToLocalization==null)
			return; // should only happen when MANUEL programmed changes saved on startup
		UITab tab = languageToLocalization.get(lang);
		if(tab==null)
			return;
		Serializer serializer = new Persister(new AnnotationStrategy());
		String filename ="localization-"+ lang +".xml";
        try {
    		File file = Paths.get(saveFilePath, filename).toFile();
        	BackUPFileTEMP(file);
			serializer.write(tab, new File(filename));
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return;
		}
        BackUPFile(filename);
	}
	
	
	public void AddTab(Tabable c, Node content){
		if(tabableToTab.containsKey(c)) {
			workTabs.getSelectionModel().select(tabableToTab.get(c));
			return;
		}
		Tab t = new Tab("Empty");
		if(c!=null){
			t.setText(c.GetName());
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning!");
			String s = "Any unsaved data will be lost!";
			alert.setContentText(s);
			
			t.setOnCloseRequest(x->{
				if(t.getText().contains("*")==false){
					return;
				}
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					
				} else {
					x.consume();
				}
			});
			if(workTabs.getTabs().contains(emptyTab)){
				workTabs.getTabs().remove(emptyTab);
			}
			tabToTabable.put(t, c);
			tabableToTab.put(c, t);
			tabToID.put(t, c.GetID());
		} else {
			emptyTab = t;
		}
		workTabs.getTabs().add(t);
		workTabs.getSelectionModel().select(t);
		t.setContent(content);
		t.setOnClosed(x->{
			tabToTabable.remove(t);
			tabToID.remove(t);
			tabableToTab.remove(c);
			if(workTabs.getTabs().size()<1&&workTabs.getTabs().contains(emptyTab) == false){
				AddTab(null,mainLayout);
			}
		});
	}
	
	
	private void SetUpMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu f = new Menu("File");
        menuBar.getMenus().add(f);
		MenuItem files = new MenuItem("Save Files");
		files.setOnAction(x->{SaveData();});
		SeparatorMenuItem line = new SeparatorMenuItem();
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(x->{ System.exit(0); });
		f.getItems().addAll(files,line,exit);

        Menu mStructure = new Menu("New Structure");
        menuBar.getMenus().add(mStructure);
		
		MenuItem production = new MenuItem("Production");
		MenuItem needStructure = new MenuItem("NeedStructure");
		MenuItem farm = new MenuItem("Farm");
		MenuItem growable = new MenuItem("Growable");
		MenuItem home = new MenuItem("Home");
		MenuItem market = new MenuItem("Market");
		MenuItem warehouse = new MenuItem("Warehouse");
		MenuItem mine = new MenuItem("Mine");
		MenuItem road = new MenuItem("Road");
		MenuItem military = new MenuItem("MilitaryStructure");
		MenuItem service = new MenuItem("ServiceStructure");

		mStructure.getItems().addAll(production,needStructure,farm,growable,
										home,market,warehouse,mine,road,military,
										service);
		production.setOnAction(x->{ClassAction(Production.class);});
		needStructure.setOnAction(x->{ClassAction(NeedStructure.class);});
		farm.setOnAction(x->{ClassAction(Farm.class);});
		growable.setOnAction(x->{ClassAction(Growable.class);});
		home.setOnAction(x->{ClassAction(Home.class);});
		market.setOnAction(x->{ClassAction(Market.class);});
		warehouse.setOnAction(x->{ClassAction(Warehouse.class);});
		mine.setOnAction(x->{ClassAction(Mine.class);});
		road.setOnAction(x->{ClassAction(Road.class);});
		military.setOnAction(x->{ClassAction(MilitaryStructure.class);});
		service.setOnAction(x->{ClassAction(ServiceStructure.class);});
		
        Menu mUnit = new Menu("New Unit-Things");

        menuBar.getMenus().add(mUnit);
        MenuItem unit = new MenuItem("Unit");
		MenuItem armorType = new MenuItem("ArmorType");
		MenuItem damageType = new MenuItem("DamageType");
        MenuItem ship = new MenuItem("Ship");

		armorType.setOnAction(x-> {
        	ClassAction(ArmorType.class);
        });
		unit.setOnAction(x-> {
        	ClassAction(Unit.class);
        });
		damageType.setOnAction(x-> {
        	ClassAction(DamageType.class);
        });
		ship.setOnAction(x-> {
        	ClassAction(Ship.class);
        });
		mUnit.getItems().addAll(unit,ship,damageType,armorType);
        Menu mOther = new Menu("New Other");
        menuBar.getMenus().add(mOther);
		MenuItem item = new MenuItem("Item");
		MenuItem fertility = new MenuItem("Fertility");
		MenuItem need = new MenuItem("Need");
		MenuItem needGroup = new MenuItem("NeedGroup");
		MenuItem populationLevel = new MenuItem("PopulationLevel");

		need.setOnAction(x-> {
        	ClassAction(Need.class);
        });
		needGroup.setOnAction(x-> {
        	ClassAction(NeedGroup.class);
        });
		populationLevel.setOnAction(x-> {
        	ClassAction(PopulationLevel.class);
        });
		fertility.setOnAction(x-> {
        	ClassAction(Fertility.class);
        });
		item.setOnAction(x-> {
        	ClassAction(ItemXML.class);
        });
		mOther.getItems().addAll(item,fertility,need,needGroup,populationLevel);
		((VBox) scene.getRoot()).getChildren().addAll(menuBar);

        Menu mEvents = new Menu("Events");
        menuBar.getMenus().add(mEvents);
        
		MenuItem effect = new MenuItem("Effect");
		MenuItem gameEvent = new MenuItem("GameEvent");

		effect.setOnAction(x-> {
        	ClassAction(Effect.class);
        });
		gameEvent.setOnAction(x-> {
        	ClassAction(GameEvent.class);
        });
		mEvents.getItems().addAll(effect,gameEvent);
		
		Menu localization = new Menu("Localization");
		for(Language l : Language.values()) {
	        MenuItem localizationItem = new MenuItem(l+"");
	        localizationItem.setOnAction(x-> {
	        	LocalizationAction(l);
	        });
	        localization.getItems().add(localizationItem);
		}
        menuBar.getMenus().add(localization);

		
	}
	
	private void LocalizationAction(Language l) {
		UITab tab = LoadLocalization(l);
		if(tab == null)
			return;
		for(Tabable t : tabToTabable.values()) {
			if(t instanceof UITab) {
				if(((UITab)t).language == l)
					return;
			}
		}
		AddTab(tab, tab.scroll);
		languageToLocalization.put(l, tab);
//		Stage langWindow = new Stage();
//		VBox box = new VBox();
//		box.getChildren().add(tab.scroll);
//        Scene langscene = new Scene(box,1600,900);
//        langscene.getStylesheets().add("bootstrap3.css");
//        langWindow.setScene(langscene);
//        langWindow.show();
//        langWindow.setOnCloseRequest(x->{
//			
//		});

	}

	private void SaveData() {
		SaveStructures();
		SaveItems();
		SaveFertilities();
		SaveCombat();
		SaveUnits();
		SaveNeeds();
		for(Language l : Language.values()) {
			SaveLocalization(l);
		}
	}
	


	@SuppressWarnings("unchecked")
	private void ClassAction(Class c){
		try {
			WorkTab my = new WorkTab((Tabable) c.getConstructor().newInstance(),true);
			AddTab(my.getTabable(),my.getScrollPaneContent());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public ArrayList<Item> getItems() {
		ArrayList<Item> al = new ArrayList<>();
		for (ItemXML i : idToItem.values()) {
			al.add(new Item(i));
		}
		Collections.sort(al);
		return al;
	}
	public void SaveCurrentTab(){
		Tab curr = GetCurrentTab();
		Tabable currTabable = tabToTabable.get(curr);
		if(currTabable instanceof UITab) {
			SaveLocalization(((UITab)currTabable).language);
			curr.setText(curr.getText().replaceAll("\\*", ""));
			return;
		}
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
		Tabable exist = doesIDexistForTabable(currTabable.GetID(),currTabable);
		if(tabToID.get(curr)!=null && currTabable.GetID() != tabToID.get(curr)) {
			//ID Changed so we need to change all references
			//just go through all -- even tho it isnt optimal but easier for nows
			for(Tabable t : allTabables) {
				t.UpdateDependables(currTabable, tabToID.get(curr));
			}
			SaveData(); //changed stuff -- not gonna reload tabs
		}
		if(exist!=null && exist!=currTabable){
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setTitle("ID already exists!");
//			Tabable t = doesIDexistForTabable(o.GetID(),o);
			allTabables.removeIf(x->x.DependsOnTabable(currTabable)==null);


			String depends = "Other Structures depends on it!\nRemove dependencies from ";
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
		boolean saved=false;
		if(currTabable instanceof Structure){
			if(((Structure)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToStructures.put(((Structure)currTabable).GetID(),((Structure)currTabable));
			saved = SaveStructures();
		}
		else if(currTabable instanceof Item){
			if(((Item)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToItem.put(((ItemXML)currTabable).GetID(), ((ItemXML)currTabable));
			saved = SaveItems();
		}
		else if(currTabable instanceof Fertility){
			if(((Fertility)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToFertility.put(((Fertility)currTabable).GetID(), ((Fertility)currTabable));
			saved = SaveFertilities();
		}
		else if(currTabable instanceof Unit){
			if(((Unit)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToUnit.put(((Unit)currTabable).GetID(), ((Unit)currTabable));
			saved = SaveUnits();
		}
		else if(currTabable instanceof DamageType){
			if(((DamageType)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToDamageType.put(((DamageType)currTabable).GetID(), ((DamageType)currTabable));
			saved = SaveCombat();
		}
		else if(currTabable instanceof ArmorType){
			if(((ArmorType)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToArmorType.put(((ArmorType)currTabable).GetID(), ((ArmorType)currTabable));
			saved = SaveCombat();
		}
		else if(currTabable instanceof Need){
			if(((Need)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToNeed.put(((Need)currTabable).GetID(), ((Need)currTabable));
			saved = SaveNeeds();
		}
		else if(currTabable instanceof NeedGroup){
			if(((NeedGroup)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToNeedGroup.put(((NeedGroup)currTabable).GetID(), ((NeedGroup)currTabable));
			saved = SaveNeeds();
		}
		else if(currTabable instanceof PopulationLevel){
			if(((PopulationLevel)currTabable).LEVEL<=-1){
				return;
			}
			idToPopulationLevel.put(""+((PopulationLevel)currTabable).LEVEL, ((PopulationLevel)currTabable));
			saved = SaveOthers();
		}
		else if(currTabable instanceof Effect) {
			if(((Effect)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToEffect.put(((Effect)currTabable).GetID(), ((Effect)currTabable));
			saved = SaveEvents();
		}
		else if(currTabable instanceof GameEvent) {
			if(((GameEvent)currTabable).GetID().trim().isEmpty()){
				return;
			}
			idToGameEvent.put(((GameEvent)currTabable).GetID(), ((GameEvent)currTabable));
			saved = SaveEvents();
		}
		if(saved){
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

	public void changedCurrentTab() {
		if(GetCurrentTab().getText().contains("*")){
			return;
		}
		if(GetCurrentTab() == emptyTab){
			return;
		}
		GetCurrentTab().setText("*"+GetCurrentTab().getText());
	}
	public Tab GetCurrentTab(){
		return workTabs.getSelectionModel().getSelectedItem();
	}
	
	private boolean SaveStructures(){
        Serializer serializer = new Persister(new AnnotationStrategy());
        ArrayList<Structure> s = new ArrayList<>(idToStructures.values());
        
        Structures st = new Structures(s);
		File file = Paths.get(saveFilePath, "structures.xml").toFile();
        try {
        	BackUPFileTEMP(file);
			serializer.write(st, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
    	BackUPFile("structures.xml");
        return true;
        
	}
	private boolean SaveItems(){
		Serializer serializer = new Persister(new AnnotationStrategy());
        Items it = new Items(idToItem.values());
        try {
    		File file = Paths.get(saveFilePath, "items.xml").toFile();
        	BackUPFileTEMP(file);
			serializer.write(it, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        BackUPFile("items.xml");
		return true;
	}
	
	private boolean SaveFertilities() {
		Serializer serializer = new Persister(new AnnotationStrategy());
        Fertilities ft = new Fertilities(idToFertility.values());
        try {
    		File file = Paths.get(saveFilePath, "fertilities.xml").toFile();
        	BackUPFileTEMP(file);
			serializer.write(ft, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
    	BackUPFile("fertilities.xml");
        return true;
	}
	private boolean SaveUnits() {
		Serializer serializer = new Persister(new AnnotationStrategy());
        UnitSave ft = new UnitSave(idToUnit.values());
        try {
    		File file = Paths.get(saveFilePath, "units.xml").toFile();
        	BackUPFileTEMP(file);
			serializer.write(ft, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        BackUPFile("units.xml");
		return true;
	}

	private boolean SaveCombat() {
		Serializer serializer = new Persister(new AnnotationStrategy());
        CombatTypes ft = new CombatTypes(idToArmorType.values() , idToDamageType.values());
        try {
    		File file = Paths.get(saveFilePath, "combat.xml").toFile();
        	BackUPFileTEMP (file);
			serializer.write(ft, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        BackUPFile("combat.xml");
		return true;
	}
	private boolean SaveNeeds() {
		Serializer serializer = new Persister(new AnnotationStrategy());
		
		Needs ft = new Needs(idToNeed.values(), idToNeedGroup.values());
        try {
    		File file = Paths.get(saveFilePath, "needs.xml").toFile();
        	BackUPFileTEMP(file);
			serializer.write(ft, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        BackUPFile("needs.xml");
		return true;
	}
	private boolean SaveEvents(){
		Serializer serializer = new Persister(new AnnotationStrategy());
		Events ft = new Events(idToEffect.values(), idToGameEvent.values());
        try {
    		File file = Paths.get(saveFilePath, "events.xml").toFile();
        	BackUPFileTEMP(file);
			serializer.write(ft, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        BackUPFile("events.xml");
		return true;
	}
	private boolean SaveOthers(){
		Serializer serializer = new Persister(new AnnotationStrategy());
		Others ft = new Others(idToPopulationLevel.values());
        try {
    		File file = Paths.get(saveFilePath, "other.xml").toFile();
        	BackUPFileTEMP(file);
			serializer.write(ft, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        BackUPFile("other.xml");
		return true;
	}
	public ArrayList<Tabable> getStructureList(Class class1) {
		ArrayList<Tabable> list = new ArrayList<>();
		list.addAll( idToStructures.values());
		list.removeIf(x->x.getClass()!=class1);
		return list;
	}

	private void BackUPFile(String name){
		String backuppath = "old/";
		File tempf = Paths.get(backuppath, "temp_"+name).toFile();
		File oldf = Paths.get(backuppath, "old_"+name).toFile();
		if(tempf.exists()){
    		if(oldf.exists()){
    			oldf.delete();
    		}
    		tempf.renameTo(oldf);
    	}
	}
	private void BackUPFileTEMP(File file){
		String backuppath = "old/";
		File f = Paths.get(backuppath, file.getName()).toFile();
		if(f.exists()){
			if(Files.exists(Paths.get(backuppath))) {
				try {
					Files.createDirectory(Paths.get(backuppath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File tempf = Paths.get(backuppath, "temp_"+file.getName()).toFile();
    		if(tempf.exists()){
    			tempf.delete();
    		}
    		f.renameTo(tempf);
    	}
	}
	public Tabable doesIDexistForTabable(int id, Tabable tab){
		return null;
//		Class c = tab.getClass();
//		if(Structure.class.isAssignableFrom(c)){
//			c = Structure.class;
//		}
//		if(classToClassObservableMap.containsKey(c) == false) {
//			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
//			return null;
//		}
//		return classToClassObservableMap.get(c).containsKey(id) ? classToClassObservableMap.get(c).get(id) : null;
	}
	public Tabable doesIDexistForTabable(String valueSafe, Tabable tab) {
		Class c = tab.getClass();
		if(Structure.class.isAssignableFrom(c)){
			c = Structure.class;
		}
		if(classToClassObservableMap.containsKey(c) == false) {
			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
			return null;
		}
		return (Tabable) classToClassObservableMap.get(valueSafe);
	}
//	public int getOneHigherThanMaxID(Tabable tab){
//		if(Structure.class.isAssignableFrom(tab.getClass())){
//			HashMap<Integer,Structure> temp = new HashMap<Integer,Structure>(idToStructures);
//			temp.values().removeIf(x->x.getClass()!=tab.getClass()); 
//			if(temp.keySet().isEmpty()){
//				return -1;
//			}
//			int max = Collections.max(temp.keySet())+1;
//			if(doesIDexistForTabable(max,tab)!=null){
//				Alert a = new Alert(AlertType.INFORMATION);
//				a.setTitle("IDs for this structure-type overlaps with the next one!");
//				a.setContentText("ID set to the max of ALL! But that will intersect with reserved for anotherone!");
//				a.show();
//				return Collections.max(idToStructures.keySet())+1;
//			}
//			return max;
//		} else
//		if(classToClassObservableMap.containsKey(tab.getClass())) {
//			if(classToClassObservableMap.get(tab.getClass()).isEmpty())
//				return 0;
//			int max = (int) Collections.max(classToClassObservableMap.get(tab.getClass()).keySet());
//			return max+1;
//		} else {
//			System.out.println("Forgot to add the new Class to classToClassObservableMap!");
//		}
//
//		return -1;
//	}

	public Node getRoot() {
		return scene.getRoot();
	}

	public ArrayList<Unit> getUnits() {
		ArrayList<Unit> al = new ArrayList<>();
		for (Unit u : idToUnit.values()) {
			al.add(u);
		}
		Collections.sort(al);
		return al;
	}

	public ObservableMap<String, ? extends Tabable> GetObservableList(Class classTabable) {
		if(classToClassObservableMap.containsKey(classTabable) == false) {
			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
			return null;
		}
		if(Structure.class.isAssignableFrom(classTabable))
			classTabable = Structure.class;
		return classToClassObservableMap.get(classTabable);
	}

}
