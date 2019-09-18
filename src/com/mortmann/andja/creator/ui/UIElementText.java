package com.mortmann.andja.creator.ui;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

@Root(strict=false,name="text")
public class UIElementText extends UIElement {
	
	@Element(required=false) public String text;
	@Element(required=false) public String hoverOver;
	
	public UIElementText(String name, UIElement parent) {
		super(name,parent);
	}
	public UIElementText(String name, UIElement parent ,UIElement element) {
		super(name,parent);
		childs = element.childs;
	}
	@Override
	public TitledPane GetPane() {
		gridpane = new GridPane();
		gridpane.add(GetStringSetter(true),0,0);
		gridpane.add(GetStringSetter(false),0,1);
		
		title = new TitledPane(name, gridpane);
		if(childs != null) {
			int x = 0;
			int y = 2;
			for(UIElement ui : childs) {
				gridpane.add(ui.GetPane(),x,y);
				x++;
				if(x==3) {
					x = 0;
					y ++;
				}
			}
		}
		
		title.setExpanded(false);
		CheckMissing();
		
		return title;
	}
	public UIElementText() {
		
	}
	private GridPane GetStringSetter(boolean isText) {
		GridPane grid = new  GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        col1.setMaxWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(200);
        col2.setMaxWidth(300);
        grid.getColumnConstraints().addAll(col1,col2);
		TextArea textField = new TextArea();
		textField.setMaxHeight(65);
		if(text!=null && isText){
			textField.setText(text);
		} else 
		if(hoverOver!=null) {
			textField.setText(hoverOver);
		}
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(isText)
					text = textField.getText();
				else
					hoverOver = textField.getText();
				GUI.Instance.changedCurrentTab();
				CheckMissing();
			}
		});
		if(isText) 
			grid.add(new Label("Text"), 0, 0);	
		else
			grid.add(new Label("HoverOver"),0, 0);	

		
		grid.add(textField, 1, 0);	
		return grid;
	}
	
	public void CheckMissing() {
		if(IsMissing()) {
		    ObservableList<String> styleClass = title.getStyleClass();
		    if(styleClass.contains("titledpane-error")==false)
		    	styleClass.add("titledpane-error");
		} else {
		    ObservableList<String> styleClass = title.getStyleClass();
		    if(styleClass.contains("titledpane-error"))
		    	styleClass.removeIf(x->x.equals("titledpane-error"));
		}
		if(parent!=null)
			parent.UpdateMissing();
	}
	@Override
	public boolean IsMissing() {
		return text == null || hoverOver == null || text.isEmpty() || hoverOver.isEmpty();
	}
	
}
