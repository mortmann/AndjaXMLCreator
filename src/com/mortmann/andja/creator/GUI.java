package com.mortmann.andja.creator;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.util.MyInputHandler;
import com.mortmann.andja.creator.util.NotClosableTab;
import com.mortmann.andja.creator.util.Tabable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class GUI {
	public enum Language {English, German}
	public static GUI Instance;
	private Stage mainWindow;
	private BorderPane mainLayout;
	private Scene scene;
	TabPane workTabs;
	TabPane dataTabs;

	Tab emptyTab;
	
	HashMap<Integer,Structure> idToStructures;
	HashMap<Integer,Fertility> idToFertility;
	public HashMap<Integer,ItemXML> idToItem;

	HashMap<Tab,Object> tabToObject;
	
	public void start(Stage primaryStage) {
        Instance = this;
        primaryStage.addEventHandler(EventType.ROOT,new MyInputHandler());
        tabToObject = new HashMap<>();
        scene = new Scene(new VBox(),1600,900);
        scene.getStylesheets().add("bootstrap3.css");
        
		mainWindow = primaryStage;
		mainLayout = new BorderPane();
		SetUpMenuBar();
        Serializer serializer = new Persister(new AnnotationStrategy());
        Structures s = new Structures();
		try {
			serializer.read(s, new File("structures.xml"));
			for (Structure i : s.GetAllStructures()) {
				idToStructures.put(i.ID, i);
			}
		} catch (Exception e1) {
			//e1.printStackTrace();
			idToStructures = new HashMap<>();
		}
        
        idToFertility = new HashMap<>();
        idToItem = new HashMap<>();
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
			Fertilities e = serializer.read(Fertilities.class, new File("fertilities.xml"));
			for (Fertility i : e.fertilities) {
//				items.add(new Item(i));
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
        	idToFertility = new HashMap<>();
		}
        
		mainWindow.setScene(scene);
		mainWindow.show();
        
        dataTabs = new TabPane();
        new DataTab("Structures",idToStructures, dataTabs);
        new DataTab("Items",idToItem, dataTabs);
        new DataTab("Fertilities",idToFertility, dataTabs);

        
        
        
		workTabs = new TabPane();
		workTabs.setMaxHeight(Double.MAX_VALUE);
		AddTab(null,mainLayout);
		
        GridPane hb = new GridPane();
        hb.add(dataTabs,0,0);
        hb.add(workTabs,1,0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(21);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(79);
        col2.setHgrow(Priority.ALWAYS);
        hb.getColumnConstraints().addAll(col1,col2);
        
		((VBox) scene.getRoot()).getChildren().addAll(hb);
		VBox.setVgrow(workTabs, Priority.ALWAYS);
	
		
	}

	public void AddTab(Object c, Node content){
		Tab t = new Tab("Empty");
		if(c!=null){
			t.setText("*"+c.getClass().getSimpleName());
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning!");
			String s = "Any unsaved data will be lost!";
			alert.setContentText(s);
			alert.setOnCloseRequest(x->{
				System.out.println("Close");
			});
			t.setOnCloseRequest(x->{
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					
				} else {
					x.consume();
				}
			});
			if(workTabs.getTabs().contains(emptyTab)){
				workTabs.getTabs().remove(emptyTab);
			}
			tabToObject.put(t, c);
		} else {
			emptyTab = t;
		}
		workTabs.getTabs().add(t);
		t.setContent(content);
		t.setOnClosed(x->{
			if(workTabs.getTabs().contains(emptyTab) == false){
				AddTab(null,mainLayout);
			}
		});
	}
	
	
	private void SetUpMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu f = new Menu("File");
        menuBar.getMenus().add(f);
		MenuItem files = new MenuItem("Save Files");
		files.setOnAction(x->{saveData();});
		SeparatorMenuItem line = new SeparatorMenuItem();
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(x->{ System.exit(0); });
		f.getItems().addAll(files,line,exit);

        Menu mStructure = new Menu("New Structure");
        menuBar.getMenus().add(mStructure);
		
		MenuItem production = new MenuItem("Production");
		MenuItem needsBuilding = new MenuItem("NeedsBuilding");
		MenuItem farm = new MenuItem("Farm");
		MenuItem growable = new MenuItem("Growable");
		MenuItem home = new MenuItem("Home");
		MenuItem market = new MenuItem("Market");
		MenuItem warehouse = new MenuItem("Warehouse");
		MenuItem mine = new MenuItem("Mine");
		MenuItem road = new MenuItem("Road");
		mStructure.getItems().addAll(production,needsBuilding,farm,growable,home,market,warehouse,mine,road);
		production.setOnAction(x->{ClassAction(Production.class);});
		needsBuilding.setOnAction(x->{ClassAction(NeedsBuilding.class);});
		farm.setOnAction(x->{ClassAction(Farm.class);});
		growable.setOnAction(x->{ClassAction(Growable.class);});
		home.setOnAction(x->{ClassAction(Home.class);});
		market.setOnAction(x->{ClassAction(Market.class);});
		warehouse.setOnAction(x->{ClassAction(Warehouse.class);});
		mine.setOnAction(x->{ClassAction(Mine.class);});
		road.setOnAction(x->{ClassAction(Road.class);});

        Menu mOther = new Menu("New Other");
        menuBar.getMenus().add(mOther);
		MenuItem item = new MenuItem("Item");
		MenuItem fertility = new MenuItem("Fertility");
		fertility.setOnAction(x-> {
        	ClassAction(Fertility.class);
        });
		item.setOnAction(x-> {
        	ClassAction(ItemXML.class);
        });
		mOther.getItems().addAll(item,fertility);
		((VBox) scene.getRoot()).getChildren().addAll(menuBar);

		
	}
	
	private void saveData() {
		SaveStructures();
		SaveItems();
		SaveFertilities();
	}
	
	@SuppressWarnings("unchecked")
	private void ClassAction(@SuppressWarnings("rawtypes") Class c){
		try {
			WorkTab my = new WorkTab((Tabable) c.getConstructor().newInstance());
			AddTab(my.getObject(),my.getScrollPaneContent());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public ArrayList<Item> getItems() {
		return new ArrayList<Item>(idToItem.values());
	}
	public void SaveCurrentTab(){
		Tab curr = GetCurrentTab();
		Object o = tabToObject.get(curr);
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
		if(saved){
			curr.setText(curr.getText().replaceAll("\\*", ""));
		}
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
	private Tab GetCurrentTab(){
		return workTabs.getSelectionModel().getSelectedItem();
	}
	
	public boolean SaveStructures(){
        Serializer serializer = new Persister(new AnnotationStrategy());
        ArrayList<Structure> s = new ArrayList<>(idToStructures.values());
        Structures st = new Structures(s);
        try {
			serializer.write(st, new File("structures.xml"));
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
        return true;
        
	}
	public boolean SaveItems(){
		Serializer serializer = new Persister(new AnnotationStrategy());
        Items it = new Items(idToItem.values());
        try {
			serializer.write(it, new File("items.xml"));
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
		return true;
	}
	
	private boolean SaveFertilities() {
		Serializer serializer = new Persister(new AnnotationStrategy());
        Fertilities ft = new Fertilities(idToFertility.values());
        try {
			serializer.write(ft, new File("fertilities.xml"));
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
			return false;
		}
		return true;
	}
	public ArrayList<Structure> getStructureList() {
		return new ArrayList<Structure>(idToStructures.values());
	}
}
