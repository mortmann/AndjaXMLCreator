package com.mortmann.andja.creator;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.util.MyInputHandler;

import javafx.event.EventType;

public class GUI {
	public enum Language {English, German}
	public static GUI Instance;
	private Stage mainWindow;
	private BorderPane mainLayout;
	private Scene scene;
	TabPane tabs;
	ArrayList<ItemXML> items;
	
	HashMap<Integer,Structure> idToStructures;
	HashMap<Integer,Fertility> idToFertility;
	HashMap<Integer,Item> idToItem;

	HashMap<Tab,Object> tabToObject;
	
	ArrayList<Structure> Structures;
	
	public void start(Stage primaryStage) {
        Instance = this;
        primaryStage.addEventHandler(EventType.ROOT,new MyInputHandler());
        tabToObject = new HashMap<>();
        scene = new Scene(new VBox(),1600,900);
		mainWindow = primaryStage;
		mainLayout = new BorderPane();
		SetUpMenuBar();
        Serializer serializer = new Persister(new AnnotationStrategy());
        idToStructures = new HashMap<>();
        idToFertility = new HashMap<>();
        idToItem = new HashMap<>();

        File source = new File("items.xml");
        try {
			Items e = serializer.read(Items.class, source);
			items = e.items;
		} catch (Exception e) {
			e.printStackTrace();
		}
        HBox hb = new HBox();
		mainWindow.setScene(scene);
		mainWindow.show();
        mainLayout.setTop(hb);
        
		tabs = new TabPane();
		tabs.setMaxHeight(Double.MAX_VALUE);
		AddTab(null,mainLayout);
		((VBox) scene.getRoot()).getChildren().addAll(tabs);
		VBox.setVgrow(tabs, Priority.ALWAYS);
	
		
	}
	private void AddTab(Object c, Node content){
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
			if(tabs.getTabs().size()==1){
				if(tabToObject.size()==0){
					tabs.getTabs().remove(0);
				}
			}
			tabToObject.put(t, c);
		}
		tabs.getTabs().add(t);
		t.setContent(content);
		t.setOnClosed(x->{
			if(tabs.getTabs().size()==0){
				AddTab(null,mainLayout);
			}
		});
	}
	
	
	private void SetUpMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu f = new Menu("File");
        menuBar.getMenus().add(f);
		MenuItem files = new MenuItem("Save Files");
		files.setOnAction(x->{System.out.println("SAVE");});
		f.getItems().addAll(files);

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
	
	private void ClassAction(@SuppressWarnings("rawtypes") Class c){
		MyTab my = new MyTab(c);
		AddTab(my.getObject(),my.getScrollPaneContent());
	}
	public ArrayList<ItemXML> getItems() {
		return items;
	}
	public void setItems(ArrayList<ItemXML> items) {
		this.items = items;
	}
	
	public void SaveCurrentTab(){
		Tab curr = GetCurrentTab();
		curr.setText(curr.getText().replaceAll("\\*", ""));
		Object o = tabToObject.get(curr);
		if(o instanceof Structure){
			if(((Structure)o).ID==-1){
				return;
			}
			idToStructures.put(((Structure)o).ID, ((Structure)o));
		}
		else if(o instanceof Item){
			if(((Item)o).ID==-1){
				return;
			}
			idToItem.put(((Item)o).ID, ((Item)o));
		}
		else if(o instanceof Fertility){
			if(((Fertility)o).ID==-1){
				return;
			}
			idToFertility.put(((Fertility)o).ID, ((Fertility)o));
		}
	}
	public void changedCurrentTab() {
		if(GetCurrentTab().getText().contains("*")){
			return;
		}
		GetCurrentTab().setText("*"+GetCurrentTab().getText());
	}
	private Tab GetCurrentTab(){
		return tabs.getSelectionModel().getSelectedItem();
	}
	
}
