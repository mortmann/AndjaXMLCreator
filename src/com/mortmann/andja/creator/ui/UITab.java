package com.mortmann.andja.creator.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.Tabable;

import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
@Root(strict=false,name="UI")
public class UITab implements Tabable {
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
	}
	public UITab() {
	}
	@Override
	public String GetName() {
		return language+"";
	}
	@Override
	public int GetID() {
		return -1;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}

}
