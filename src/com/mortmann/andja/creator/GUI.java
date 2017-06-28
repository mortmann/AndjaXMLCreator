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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
import javafx.collections.transformation.FilteredList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.VPos;

public class GUI {
	public enum Language {English, German}
	public static GUI Instance;
	private Stage mainWindow;
	private BorderPane mainLayout;
	private Scene scene;
	TabPane workTabs;
	TabPane dataTabs;

	Tab emptyTab;
	
	ArrayList<Structure> idToStructures;
	HashMap<Integer,Fertility> idToFertility;
	public HashMap<Integer,ItemXML> idToItem;

	HashMap<Tab,Object> tabToObject;
	
	ArrayList<Node> allStructureNodeList;

	ArrayList<Item> items;
	private ArrayList<Node> allItemNodeList;

	public void start(Stage primaryStage) {
        Instance = this;
        primaryStage.addEventHandler(EventType.ROOT,new MyInputHandler());
        tabToObject = new HashMap<>();
        scene = new Scene(new VBox(),1600,900);
        scene.getStylesheets().add("textfield.css");
        scene.getStylesheets().add("bootstrap3.css");
        
		mainWindow = primaryStage;
		mainLayout = new BorderPane();
		SetUpMenuBar();
        Serializer serializer = new Persister(new AnnotationStrategy());
        Structures s = new Structures();
		try {
			serializer.read(s, new File("structures.xml"));
			idToStructures = s.GetAllStructures();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        idToFertility = new HashMap<>();
        idToItem = new HashMap<>();
        items = new ArrayList<>();
        File source = new File("items.xml");
        try {
			Items e = serializer.read(Items.class, source);
			for (ItemXML i : e.items) {
				items.add(new Item(i));
				idToItem.put(i.ID, i);
			}
//			serializer.write(e,new File( "items.xml" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
		mainWindow.setScene(scene);
		mainWindow.show();
        
        dataTabs = new TabPane();
        createStructuresTab();
        createItemsTab();        
        
        
		workTabs = new TabPane();
		workTabs.setMaxHeight(Double.MAX_VALUE);
		AddTab(null,mainLayout);
		
        GridPane hb = new GridPane();
        hb.add(dataTabs,0,0);
        hb.add(workTabs,1,0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(15);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(85);
        col2.setHgrow(Priority.ALWAYS);
        hb.getColumnConstraints().addAll(col1,col2);
        
		((VBox) scene.getRoot()).getChildren().addAll(hb);
		VBox.setVgrow(workTabs, Priority.ALWAYS);
	
		
	}
	private void createStructuresTab(){
		ScrollPane sp = new ScrollPane();
		VBox v = new VBox();
		
	    FlowPane flow = new FlowPane();
	    flow.setPadding(new Insets(0, 7, 15, 5));
	    flow.setVgap(3);
	    flow.setHgap(3);
		for (Structure structure : idToStructures) {
			
			Button b = new Button();
			Structure s = structure;
			MyTab my = new MyTab(s);
			b.setOnAction(x->AddTab(s,my.getScrollPaneContent()));
			b.setText((String) s.toString());
			b.setMinSize(50, 50);
			b.setMaxSize(50, 50);
			b.setTooltip(new Tooltip(s.toString()));
			b.setWrapText(true);
			b.setId(s.toString());
			b.setTextAlignment(TextAlignment.CENTER);
			b.setTextOverrun(OverrunStyle.ELLIPSIS);
			flow.getChildren().add(b);
		}
//		for (int i = 0; i < 200; i++) {
//			String s = i+"-sadasdsdasda";
//			Button b = new Button(s);
//			b.setMinSize(50, 50);
//			b.setMaxSize(50, 50);
//			b.setTooltip(new Tooltip(s));
//			b.setWrapText(true);
//			b.setId(i+"test");
//			b.setTextAlignment(TextAlignment.CENTER);
//			b.setTextOverrun(OverrunStyle.ELLIPSIS);
//			flow.getChildren().add( b );
//		}
		allStructureNodeList = new ArrayList<>(flow.getChildren());
		sp.setContent(flow);
		sp.setMaxHeight(Double.MAX_VALUE);
		sp.setMaxWidth(Double.MAX_VALUE);
	    flow.setPrefWrapLength(210); // preferred width allows for two columns
	    TextField search = new TextField("");
	    search.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(search.getText().isEmpty()||search.getText().trim().isEmpty()){
					flow.getChildren().clear();
					flow.getChildren().addAll(allStructureNodeList);
					return;
				}
				ObservableList<Node> ns = FXCollections.observableArrayList(allStructureNodeList);
				ns.removeIf(x->x.getId().contains(search.getText())==false);
				flow.getChildren().clear();
				flow.getChildren().addAll(ns);
			}
		});
	    v.getChildren().add(search);
	    v.getChildren().add(sp);
		NotClosableTab nct = new NotClosableTab("Structures");
		nct.setContent(v);
        dataTabs.getTabs().add(nct);

	}
	private void createItemsTab(){
		ScrollPane sp = new ScrollPane();
		VBox v = new VBox();
		
	    FlowPane flow = new FlowPane();
	    flow.setPadding(new Insets(0, 7, 15, 5));
	    flow.setVgap(3);
	    flow.setHgap(3);
	    ArrayList<ItemXML> sort = new ArrayList<>(idToItem.values());
	    Collections.sort(sort);
		for (ItemXML it : sort) {
			
			Button b = new Button();
			ItemXML s = it;
			MyTab my = new MyTab(s);
			b.setOnAction(x->AddTab(s,my.getScrollPaneContent()));
			b.setText(s.toString());
			b.setMinSize(50, 50);
			b.setMaxSize(50, 50);
			b.setTooltip(new Tooltip(s.toString()));
			b.setWrapText(true);
			b.setTextAlignment(TextAlignment.CENTER);
			b.setTextOverrun(OverrunStyle.ELLIPSIS);
			b.setId(s.toString());

			flow.getChildren().add(b);
		}
//		for (int i = 0; i < 200; i++) {
//			String s = i+"-sadasdsdasda";
//			Button b = new Button(s);
//			b.setMinSize(50, 50);
//			b.setMaxSize(50, 50);
//			flow.getChildren().add( b );
//		}
		allItemNodeList = new ArrayList<>(flow.getChildren());
		sp.setContent(flow);
		sp.setMaxHeight(Double.MAX_VALUE);
		sp.setMaxWidth(Double.MAX_VALUE);
	    flow.setPrefWrapLength(210); // preferred width allows for two columns
	    TextField search = new TextField("");
	    search.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(search.getText().isEmpty()||search.getText().trim().isEmpty()){
					flow.getChildren().clear();
					flow.getChildren().addAll(allItemNodeList);
					return;
				}
				ObservableList<Node> ns = FXCollections.observableArrayList(allItemNodeList);
				ns.removeIf(x->x.getId().contains(search.getText())==false);
				flow.getChildren().clear();
				flow.getChildren().addAll(ns);
			}
		});
	    v.getChildren().add(search);
	    v.getChildren().add(sp);
		NotClosableTab nct = new NotClosableTab("Items");
		nct.setContent(v);
        dataTabs.getTabs().add(nct);

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
	
	@SuppressWarnings("unchecked")
	private void ClassAction(@SuppressWarnings("rawtypes") Class c){
		try {
			MyTab my = new MyTab((Tabable) c.getConstructor().newInstance());
			AddTab(my.getObject(),my.getScrollPaneContent());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	
	public void SaveCurrentTab(){
		Tab curr = GetCurrentTab();
		Object o = tabToObject.get(curr);
		boolean saved=false;
		if(o instanceof Structure){
			if(((Structure)o).ID==-1){
				return;
			}
//			idToStructures.put(((Structure)o).ID, ((Structure)o));
			idToStructures.add(((Structure)o));
			saved = SaveStructures();
			
		}
		else if(o instanceof Item){
			if(((Item)o).ID==-1){
				return;
			}
			idToItem.put(((ItemXML)o).ID, ((ItemXML)o));
		}
		else if(o instanceof Fertility){
			if(((Fertility)o).ID==-1){
				return;
			}
			idToFertility.put(((Fertility)o).ID, ((Fertility)o));
		}
		if(saved){
			curr.setText(curr.getText().replaceAll("\\*", ""));
		}
	}
	public void changedCurrentTab() {
		if(GetCurrentTab().getText().contains("*")){
			return;
		}
		GetCurrentTab().setText("*"+GetCurrentTab().getText());
	}
	private Tab GetCurrentTab(){
		return workTabs.getSelectionModel().getSelectedItem();
	}
	
	public boolean SaveStructures(){
        Serializer serializer = new Persister(new AnnotationStrategy());
        Structures st = new Structures(idToStructures);
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
	
	
	
}
