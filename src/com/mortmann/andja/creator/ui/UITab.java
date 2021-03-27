package com.mortmann.andja.creator.ui;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.saveclasses.BaseSave;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.history.ChangeHistory;
import com.mortmann.andja.creator.util.history.TextAreaHistory;

import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

@Root(strict=false,name="UILanguageLocalizations")
public class UITab extends Tab implements Tabable  {
//	@ElementListUnion ({
//        @ElementList(required=false,entry="element", inline=true, type=UIElement.class),
//        @ElementList(required=false,entry="textelement", inline=true, type=UIElementText.class)
//    })
//	public ArrayList<UIElement> elements = new ArrayList<>();
	@ElementList(name="localizationData", entry = "translationData")
	ArrayList<TranslationData> translations;
	ArrayList<TextAreaHistory> tabList = new ArrayList<TextAreaHistory>();
	public GridPane grid;
	public ScrollPane scroll;
	@Attribute
	public String language;
	public UITab(ArrayList<TranslationData> translations,String lang) {
		language = lang;
		this.translations = translations;
		int x = 0;
		int y = 0;
		grid = new GridPane();
		grid.setGridLinesVisible(true);
		for(TranslationData td : translations) {
			Node tp = new UIElementText(td).GetPane(tabList);
			tp.setUserData(td);
			GridPane.setValignment(tp, VPos.TOP);
			grid.add(tp,x,y);
			x++;
			if(x==3) {
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
		return BaseSave.Save(""+ language +"-ui.loc", this);
	}
	
	public static UITab Load(String language) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		
		String filename =""+ language +"-ui.loc";
		UITab empty = new UITab();
		try {
			empty = serializer.read(empty, Paths.get("Empty-ui.loc").toFile());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        UITab tab = new UITab();
		try {
			tab = serializer.read(tab, Paths.get(BaseSave.saveFilePath, filename).toFile());
		} catch (Exception e1) {
			e1.printStackTrace();
			tab = new UITab(new ArrayList<TranslationData>(), language);
		}    
		HashSet<TranslationData> datas = new HashSet<>(tab.translations);
		if(empty != null) {
			for(TranslationData data : empty.translations) {
				if(datas.contains(data) == false)
					tab.translations.add(data);
			}
		}
		tab.translations = (ArrayList<TranslationData>) tab.translations.stream().sorted((object1, object2) -> object1.id.compareTo(object2.id)).
                collect(Collectors.toList());;
		return new UITab(tab.translations, language);
	}

	public void onTabInput() {
		Scene s = GUI.Instance.getScene();
		Node n = s.focusOwnerProperty().get();
		if(n instanceof TextAreaHistory) {
			int index = tabList.indexOf(n);
			if(index == -1)
				return;
			tabList.get((index + 1) % tabList.size()).requestFocus();
		}
	}
	
}
