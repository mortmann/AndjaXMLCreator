package com.mortmann.andja.creator.ui;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.history.TextAreaHistory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

@Root(strict=false)
public class UISingleString extends UIElement {

	@Element(required=false) String value;
	
	public UISingleString(String key, UIElement parent) {
		super(key,parent);
	}

	public UISingleString(String key, UIElement parent, UIElement element) {
		super(key,parent);
		childs = element.childs;
	}
	@Override
	public Node GetPane() {
		gridpane = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        col1.setMaxWidth(100);
        gridpane.getColumnConstraints().addAll(col1);
		TextAreaHistory  textField = new TextAreaHistory();
		textField.setMaxHeight(65);
		textField.setStartText(value);
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				value = textField.getText();
				GUI.Instance.changedCurrentTab(true);
				CheckMissing();
			}
		});
		gridpane.add(new Label(name), 0, 0);	
		gridpane.add(textField, 1, 0);	
		return gridpane;
	}
	@Override
	public boolean IsMissing() {
		return value == null || value.isEmpty();
	}
	public void CheckMissing() {
		if(IsMissing()) {
		    ObservableList<String> styleClass = gridpane.getParent().getStyleClass();
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
}
