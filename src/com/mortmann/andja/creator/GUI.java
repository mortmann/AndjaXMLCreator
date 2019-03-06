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
import java.lang.reflect.Field;
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

public class GUI {
	public enum Language {English, German}
	public static GUI Instance;
	private Stage mainWindow;
	private BorderPane mainLayout;
	private Scene scene;
	TabPane workTabs;
	TabPane dataTabs;

	Tab emptyTab;
	
	public ObservableMap<Integer,Structure> idToStructures;
	public ObservableMap<Integer,Fertility> idToFertility;
	public ObservableMap<Integer,ItemXML> idToItem;
	public ObservableMap<Integer,DamageType> idToDamageType;
	public ObservableMap<Integer,ArmorType> idToArmorType;
	public ObservableMap<Integer,Unit> idToUnit;
	public ObservableMap<Integer,Need> idToNeed;
	public ObservableMap<Integer, NeedGroup> idToNeedGroup;
	public ObservableMap<Integer, PopulationLevel> idToPopulationLevel;
	public ObservableMap<Integer, Effect> idToEffect;
	private ObservableMap<Integer, GameEvent> idToGameEvent;
	@SuppressWarnings("rawtypes")
	public HashMap<Class, ObservableMap<Integer, ? extends Tabable>> classToClassObservableMap;

	HashMap<Tab,Tabable> tabToTabable;
	
	@SuppressWarnings("rawtypes")
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
		
        tabToTabable = new HashMap<>();

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
	}

	private void LoadData(){
		Serializer serializer = new Persister(new AnnotationStrategy());
        Structures s = new Structures();
		try {
			serializer.read(s, new File("structures.xml"));
			for (Structure i : s.GetAllStructures()) {
				idToStructures.put(i.ID, i);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			idToStructures = FXCollections.observableHashMap();
		}        
		
        try {
			Items e = serializer.read(Items.class, new File("items.xml"));
			for (ItemXML i : e.items) {
				idToItem.put(i.ID, i);
			}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			UnitSave e = serializer.read(UnitSave.class, new File("units.xml"));
			for (Unit u : e.getAllUnits()) {
				idToUnit.put(u.ID, u);
			}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			Needs e = serializer.read(Needs.class, new File("needs.xml"));
			if(e.needs!=null)
			for (Need u : e.needs) {
				idToNeed.put(u.ID, u);
			}
			if(e.groupNeeds!=null)
			for (NeedGroup u : e.groupNeeds) {
				idToNeedGroup.put(u.ID, u);
			}
//			for(Need u : idToNeed.values()) {
//				u.UsageAmounts = new HashMap<>();
//				u.UsageAmounts.put(0, u.Peasent);
//				u.UsageAmounts.put(1, u.Citizen);
//				u.UsageAmounts.put(2, u.Patrician);
//				u.UsageAmounts.put(3, u.Nobleman);
//			}
			SaveNeeds();
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			CombatTypes e = serializer.read(CombatTypes.class, new File("combat.xml"));
			if(e.damageTypes!=null)
				for (DamageType u : e.damageTypes) {
					idToDamageType.put(u.ID, u);
				}
			if(e.armorTypes!=null)
				for (ArmorType u : e.armorTypes) {
					idToArmorType.put(u.ID, u);
				}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			Others e = serializer.read(Others.class, new File("other.xml"));
			if(e.populationLevels!=null)
				for (PopulationLevel u : e.populationLevels) {
					idToPopulationLevel.put(u.LEVEL, u);
				}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			Fertilities e = serializer.read(Fertilities.class, new File("fertilities.xml"));
			for (Fertility i : e.fertilities) {
				idToFertility.put(i.ID, i);
//				i.Name = new HashMap<>();
//				i.Name.put(Language.English.toString(), i.EN_Name);
//				i.Name.put(Language.German.toString(), i.DE_Name);
//				i.EN_Name = null;
//				i.DE_Name = null;
//				String[] clis = i.Climate.split(";");
//				i.Climate = null;
//				i.climates = new ArrayList<Climate>();
//				for (int x = 0; x<clis.length;x++) {
//					i.climates.add((Climate.values()[ Integer.parseInt(clis[x]) ]));
//				}
//				SaveFertilities();
			}
        } catch (Exception e) {
			e.printStackTrace();
        	idToFertility = FXCollections.observableHashMap();
		}
        try {
			Events e = serializer.read(Events.class, new File("events.xml"));
			if(e.effects!=null)
				for (Effect u : e.effects) {
					idToEffect.put(u.ID, u);
				}
			if(e.gameEvents!=null)
				for (GameEvent u : e.gameEvents) {
					idToGameEvent.put(u.ID, u);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void AddTab(Tabable c, Node content){
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
		} else {
			emptyTab = t;
		}
		workTabs.getTabs().add(t);
		workTabs.getSelectionModel().select(t);
		t.setContent(content);
		t.setOnClosed(x->{
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
	}
	
	private void SaveData() {
		SaveStructures();
		SaveItems();
		SaveFertilities();
		SaveCombat();
		SaveUnits();
		SaveNeeds();
	}
	


	@SuppressWarnings("unchecked")
	private void ClassAction(@SuppressWarnings("rawtypes") Class c){
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
		Tabable o = tabToTabable.get(curr);
		curr.setText(o.GetName());
		//check if its filled out all required
		ArrayList<Field> missingFields = CheckForMissingFields(o);
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
		Tabable exist = doesIDexistForTabable(o.GetID(),o);
		if(exist!=null && exist!=o){
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setTitle("ID already exists!");
			Tabable t = doesIDexistForTabable(o.GetID(),o);
			HashSet<Tabable> allTabs = new HashSet<>();
			for(ObservableMap<Integer, ? extends Tabable> map : classToClassObservableMap.values()) {
				allTabs.addAll(map.values());
			}

			allTabs.removeIf(x->x.DependsOnTabable(t)==null);
			String depends = "Other Structures depends on it!\nRemove dependencies from ";
			if(allTabs.size()==0){
				depends="";
			} else {
				a.setAlertType(AlertType.ERROR);
				for (Tabable st : allTabs) {
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
		boolean saved=false;
		if(o instanceof Structure){
			if(((Structure)o).ID==-1){
				return;
			}
			idToStructures.put(((Structure)o).ID,((Structure)o));
			saved = SaveStructures();
		}
		else if(o instanceof Item){
			if(((Item)o).ID==-1){
				return;
			}
			idToItem.put(((ItemXML)o).ID, ((ItemXML)o));
			saved = SaveItems();
		}
		else if(o instanceof Fertility){
			if(((Fertility)o).ID==-1){
				return;
			}
			idToFertility.put(((Fertility)o).ID, ((Fertility)o));
			saved = SaveFertilities();
		}
		else if(o instanceof Unit){
			if(((Unit)o).ID==-1){
				return;
			}
			idToUnit.put(((Unit)o).ID, ((Unit)o));
			saved = SaveUnits();
		}
		else if(o instanceof DamageType){
			if(((DamageType)o).ID==-1){
				return;
			}
			idToDamageType.put(((DamageType)o).ID, ((DamageType)o));
			saved = SaveCombat();
		}
		else if(o instanceof ArmorType){
			if(((ArmorType)o).ID==-1){
				return;
			}
			idToArmorType.put(((ArmorType)o).ID, ((ArmorType)o));
			saved = SaveCombat();
		}
		else if(o instanceof Need){
			if(((Need)o).ID==-1){
				return;
			}
			idToNeed.put(((Need)o).ID, ((Need)o));
			saved = SaveNeeds();
		}
		else if(o instanceof NeedGroup){
			if(((NeedGroup)o).ID==-1){
				return;
			}
			idToNeedGroup.put(((NeedGroup)o).ID, ((NeedGroup)o));
			saved = SaveNeeds();
		}
		else if(o instanceof PopulationLevel){
			if(((PopulationLevel)o).LEVEL<=-1){
				return;
			}
			idToPopulationLevel.put(((PopulationLevel)o).LEVEL, ((PopulationLevel)o));
			saved = SaveOthers();
		}
		else if(o instanceof Effect) {
			if(((Effect)o).ID<=-1){
				return;
			}
			idToEffect.put(((Effect)o).ID, ((Effect)o));
			saved = SaveEvents();
		}
		else if(o instanceof GameEvent) {
			if(((GameEvent)o).ID<=-1){
				return;
			}
			idToGameEvent.put(((GameEvent)o).ID, ((GameEvent)o));
			saved = SaveEvents();
		}
		if(saved){
			curr.setText(curr.getText().replaceAll("\\*", ""));
		} 
	}
	
	@SuppressWarnings("rawtypes")
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
	
	public boolean SaveStructures(){
        Serializer serializer = new Persister(new AnnotationStrategy());
        ArrayList<Structure> s = new ArrayList<>(idToStructures.values());
        Structures st = new Structures(s);
        try {
        	BackUPFileTEMP("structures.xml");
			serializer.write(st, new File("structures.xml"));
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
	public boolean SaveItems(){
		Serializer serializer = new Persister(new AnnotationStrategy());
        Items it = new Items(idToItem.values());
        try {
        	BackUPFileTEMP("items.xml");
			serializer.write(it, new File("items.xml"));
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
        	BackUPFileTEMP("fertilities.xml");
			serializer.write(ft, new File("fertilities.xml"));
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
        	BackUPFileTEMP("units.xml");
			serializer.write(ft, new File("units.xml"));
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
        CombatTypes ft = new CombatTypes(idToArmorType.values(),idToDamageType.values());
        try {
        	BackUPFileTEMP("combat.xml");
			serializer.write(ft, new File("combat.xml"));
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
        	BackUPFileTEMP("needs.xml");
			serializer.write(ft, new File("needs.xml"));
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
        	BackUPFileTEMP("events.xml");
			serializer.write(ft, new File("events.xml"));
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
        	BackUPFileTEMP("other.xml");
			serializer.write(ft, new File("other.xml"));
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
	@SuppressWarnings({ "rawtypes" })
	public ArrayList<Tabable> getStructureList(Class class1) {
		ArrayList<Tabable> list = new ArrayList<>();
		list.addAll( idToStructures.values());
		list.removeIf(x->x.getClass()!=class1);
		return list;
	}

	private void BackUPFile(String name){
		if(new File("temp_"+name).exists()){
    		if(new File("old_"+name).exists()){
    			new File("old_"+name).delete();
    		}
    		new File("temp_"+name).renameTo(new File("old_"+name));
    	}
	}
	private void BackUPFileTEMP(String name){
		if(new File(name).exists()){
    		if(new File("temp_"+name).exists()){
    			new File("temp_"+name).delete();
    		}
    		new File(name).renameTo(new File("temp_"+name));
    	}
	}
	public Tabable doesIDexistForTabable(int id, Tabable tab){
		@SuppressWarnings("rawtypes")
		Class c = tab.getClass();
		if(Structure.class.isAssignableFrom(c)){
			c = Structure.class;
		}
		if(classToClassObservableMap.containsKey(c) == false) {
			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
			return null;
		}
		return classToClassObservableMap.get(c).containsKey(id) ? classToClassObservableMap.get(c).get(id) : null;
	}
	public int getOneHigherThanMaxID(Tabable tab){
		if(Structure.class.isAssignableFrom(tab.getClass())){
			HashMap<Integer,Structure> temp = new HashMap<Integer,Structure>(idToStructures);
			temp.values().removeIf(x->x.getClass()!=tab.getClass()); 
			if(temp.keySet().isEmpty()){
				return -1;
			}
			int max = Collections.max(temp.keySet())+1;
			if(doesIDexistForTabable(max,tab)!=null){
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("IDs for this structure-type overlaps with the next one!");
				a.setContentText("ID set to the max of ALL! But that will intersect with reserved for anotherone!");
				a.show();
				return Collections.max(idToStructures.keySet())+1;
			}
			return max;
		} else
		if(classToClassObservableMap.containsKey(tab.getClass())) {
			if(classToClassObservableMap.get(tab.getClass()).isEmpty())
				return 0;
			int max = (int) Collections.max(classToClassObservableMap.get(tab.getClass()).keySet());
			return max+1;
		} else {
			System.out.println("Forgot to add the new Class to classToClassObservableMap!");
		}

		return -1;
	}

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

	@SuppressWarnings("rawtypes")
	public ObservableMap<Integer, ? extends Tabable> GetObservableList(Class classTabable) {
		if(classToClassObservableMap.containsKey(classTabable) == false) {
			System.out.println("WARNING YOU FORGOT TO ADD CLASS TO classToClassObservableMap!");
			return null;
		}
		if(Structure.class.isAssignableFrom(classTabable))
			classTabable = Structure.class;
		return classToClassObservableMap.get(classTabable);
	}
	
}
