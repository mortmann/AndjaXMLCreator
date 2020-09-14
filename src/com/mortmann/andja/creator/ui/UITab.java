package com.mortmann.andja.creator.ui;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.saveclasses.BaseSave;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.history.ChangeHistory;

import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

@Root(strict=false,name="UI")
public class UITab extends Tab implements Tabable  {
	@ElementListUnion ({
        @ElementList(required=false,entry="element", inline=true, type=UIElement.class),
        @ElementList(required=false,entry="textelement", inline=true, type=UIElementText.class)
    })
	public ArrayList<UIElement> elements = new ArrayList<>();
	public GridPane grid;
	public ScrollPane scroll;
	public Language language;
	public UITab(HashMap<String, UIElement> map,Language lang) {
		language = lang;
		this.elements = new ArrayList<>(map.values());
		int x = 0;
		int y = 0;
		grid = new GridPane();
		grid.setGridLinesVisible(true);
		for(String name : map.keySet()) {
			TitledPane tp = map.get(name).GetPane();
			GridPane.setValignment(tp, VPos.TOP);
			grid.add(tp,x,y);
			x++;
			if(x==2) {
				x = 0;
				y ++;
			}
		}
		scroll = new ScrollPane();
		scroll.setContent(grid);
		setText(GetName());
		setContent(scroll);
		
		setOnCloseRequest(ac->{
			if(ChangeHistory.IsSaved(this)){
				return;
			}
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning!");
			String s = "Any unsaved data will be lost!";
			alert.setContentText(s);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				
			} else {
				ac.consume();
			}
		});
		setOnClosed(ac->{
			GUI.Instance.RemoveTab(this,this);
		});
	}
	public UITab() {
	}
	@Override
	public String GetName() {
		return language+"";
	}
	@Override
	public String GetID() {
		return ""+-1;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		
	}
	@Override
	public String GetButtonColor() {
		return null;
	}
	@Override
	public int compareTo(Tabable o) {
		return 0;
	}
	

	public boolean Save(){
		return BaseSave.Save("localization-"+ language +".xml", this);
	}
	
	public static UITab Load(Language language) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		String filename ="localization-"+ language +".xml";
        UITab tab = new UITab();
		try {
			tab = serializer.read(tab, Paths.get(BaseSave.saveFilePath, filename).toFile());
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
	public static HashMap<String,UIElement> LoadMissingUIData(Language language) {
		AnnotationStrategy as = new AnnotationStrategy();
		Serializer serializer = new Persister(as);
		ArrayList<String> strings = new ArrayList<>();		
		UILanguageLocalizations missings = new UILanguageLocalizations();
		try {
			serializer.read(missings, new File("Missing-UI-Localization-"+language));
		} catch (Exception e) {
			return null;
		}
		strings.addAll(missings.missingLocalization);
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
		return map;
	}
}
