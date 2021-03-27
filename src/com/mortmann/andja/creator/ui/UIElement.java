package com.mortmann.andja.creator.ui;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.ElementListUnion;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

@Root(strict=false,name="element")
public class UIElement {
	@Attribute public String name;
	@ElementListUnion ({
        @ElementList(required=false,entry="element", inline=true, type=UIElement.class),
        @ElementList(required=false,entry="textelement", inline=true, type=UIElementText.class)
    })
	public ArrayList<UIElement> childs; 
	@Transient public UIElement parent;
	@Transient protected TitledPane title;
	@Transient protected GridPane gridpane;
	public UIElement(String name, UIElement parent) {
		this.name = name;
		this.parent = parent;
	}
	public UIElement() {
		
	}
	public UIElement GetUIElement(String name) {
		UIElement child = null;
		if(childs != null) {
			for(UIElement c : childs) {
				if(c.name.equalsIgnoreCase(name)) {
					child = c;
				}
			}
		} else {
			childs = new ArrayList<>();
		}
		if(child == null) {
			child = new UIElement(name,this);
			childs.add(child);
		}
		return child;
	}

	public void SetUIElementText(String key, String type) {
		boolean standard = type.equalsIgnoreCase("text") || type.equalsIgnoreCase("hover");
		UIElement child = GetUIElement(key);
		if(childs==null) {
			childs = new ArrayList<>();
		}
		if(standard) {
			if(child == null) {
			}
			else {
				childs.removeIf(x->x.name.equalsIgnoreCase(key));
			}
		} else {
			if(child == null) {
				child = new UISingleString(key,this);
			}
			else {
				childs.removeIf(x->x.name.equalsIgnoreCase(key));
				child = new UISingleString(key,this,child);
			}
		}
		childs.add(child);
	}
	
	public Node GetPane() {
		gridpane = new GridPane();
		title = new TitledPane(name, gridpane);

		int x = 0;
		int y = 0;
		for(UIElement ui : childs) {
			gridpane.add(ui.GetPane(),x,y);
			x++;
			if(x==1) {
				x = 0;
				y ++;
			}
		}
		title.setExpanded(false);
		UpdateMissing();
		return title;
	}
	public boolean IsMissing() {
		return title.getStyleClass().contains("titledpane-error");
	}
	
	public void UpdateMissing() {
		for(UIElement e : childs) {
			e.parent = this;
			if(e.IsMissing()) {
				if(title.getStyleClass().contains("titledpane-error") == false) {
					title.getStyleClass().remove("titledpane-noerror");
					title.getStyleClass().add("titledpane-error");
				}
				return;
			} 
		}
		if(title.getStyleClass().contains("titledpane-error")) {
			title.getStyleClass().remove("titledpane-error");
			title.getStyleClass().add("titledpane-noerror");
		}
		if(parent!=null)
			parent.UpdateMissing();
	}

	public boolean HasUIElementChild(String name) {
		for(UIElement c : childs) {
			if(c.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public void AddMissing(ArrayList<UIElement> newChilds) {
		if(newChilds == null)
			return;
		if(childs == null) {
			childs = new ArrayList<>();
		}
		for(UIElement old : childs) {
			for(UIElement newC : newChilds) {
				if(old.name.equalsIgnoreCase(newC.name)) {
					old.AddMissing(newC.childs);
					return;
				}
			}
		}
		childs.addAll(newChilds);
	}
	

}
