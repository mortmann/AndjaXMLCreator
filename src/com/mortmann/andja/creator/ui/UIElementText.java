package com.mortmann.andja.creator.ui;


import java.util.ArrayList;
import java.util.Arrays;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.history.TextAreaHistory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class UIElementText {
	enum StringType { Text, Tooltip, Value}
	TranslationData data;
	
	private GridPane gridpane;
	
	private ArrayList<TextAreaHistory> tabList;
	TextAreaHistory last;
	public UIElementText(TranslationData data) {
		this.data = data;
	}
	
	public Node GetPane(ArrayList<TextAreaHistory> tabList) {
		this.tabList = tabList;
		gridpane = new GridPane();
		gridpane.add(new Label(data.id), 0, 0);
		if((data.valueIsMainTranslation == null || data.valueIsMainTranslation == false) 
			&& (data.onlyToolTip == null || data.onlyToolTip == false)) {
			gridpane.add(GetStringSetter(StringType.Text , -1), 0, 1);
		}
		gridpane.add(GetStringSetter(StringType.Tooltip, -1), 0, 2);
		if(data.valueCount != null && data.valueCount>0) {
			gridpane.add(GetStringArraySetter(), 0, 3);
		}
		CheckMissing();
		return gridpane;
	}
	private Node GetStringArraySetter() {
		GridPane grid = new  GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        col1.setMaxWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(200);
        col2.setMaxWidth(300);
        grid.getColumnConstraints().addAll(col1,col2);
        if(data.values!= null) {
	        for (int i = 0; i < data.values.length; i++) {
	        	grid.add(GetStringSetter(StringType.Value, i), 0,i);
			}
	        if(data.values.length == data.valueCount) 
	        	return grid;
		} 
        Button button = new Button("+ Value");
        button.setOnAction(s -> {
        	int value = 0;
        	if(data.values != null) {
        		value = data.values.length;
        		if(value == data.valueCount) {
        			return;
        		}
        		data.values = Arrays.copyOf(data.values, value+1); 
        	} else {
        		data.values = new String[1];
        	}
        	grid.add(GetStringSetter(StringType.Value, value), 0, value);
        	if(value+1 == data.valueCount) {
    			gridpane.getChildren().remove(button);
    		}
//        	grid.getChildren().remove(button);
//        	grid.add(button, 0, grid.getRowCount()+1);
        });
        GridPane.setHalignment(button, HPos.CENTER);
        gridpane.add(button, 0, gridpane.getRowCount()+1);
		return grid;
	}

	public UIElementText() {
		
	}
	private GridPane GetStringSetter(StringType strType, int valueIndex) {
		GridPane grid = new  GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        col1.setMaxWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(200);
        col2.setMaxWidth(275);
        grid.getColumnConstraints().addAll(col1,col2);
		TextAreaHistory textField = new TextAreaHistory();
		if(last == null)
			tabList.add(textField);
		else {
			int index = tabList.indexOf(last);
			tabList.add(index+1, textField);
		}
		last = textField;
		textField.setMaxHeight(65);
		switch (strType) {
			case Tooltip:
				if(data.toolTipTranslation!=null) {
					textField.setStartText(data.toolTipTranslation);
				}
				break;
			case Text:
				if(data.translation !=null){
					textField.setStartText(data.translation);
				}
				break;
			case Value:
				if(data.values[valueIndex] !=null){
					textField.setStartText(data.values[valueIndex]);
				}
				break;
			default:
				break;
		}
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				switch (strType) {
					case Tooltip:
						data.toolTipTranslation = textField.getText();	
						break;
					case Text:					
						data.translation = textField.getText();	
						break;
					case Value:
						data.values[valueIndex] = textField.getText();	
						break;
					default:
						break;
				}
				GUI.Instance.changedCurrentTab(true);
				CheckMissing();
			}
		});
		switch (strType) {
			case Tooltip:
				grid.add(new Label("Tooltip"),0, 0);	
				break;
			case Text:					
				grid.add(new Label("Text"), 0, 0);	
				break;
			case Value:
				grid.add(new Label("Value " + (valueIndex+1)),0, 0);	
				break;
			default:
				break;
		}
		grid.add(textField, 1, 0);	
		return grid;
	}
	
	public void CheckMissing() {
		if(IsMissing()) {
		    ObservableList<String> styleClass = gridpane.getStyleClass();
		    if(styleClass.contains("titledpane-error")==false)
		    	styleClass.add("titledpane-error");
		} else {
		    ObservableList<String> styleClass = gridpane.getStyleClass();
		    if(styleClass.contains("titledpane-error"))
		    	styleClass.removeIf(x->x.equals("titledpane-error"));
		}
	}

	public boolean IsMissing() {
		if(data.onlyToolTip != null && data.onlyToolTip)
			return data.toolTipTranslation == null || data.toolTipTranslation.isBlank() || data.toolTipTranslation.equals("[**Missing**]");
		if(data.valueIsMainTranslation != null && data.valueIsMainTranslation)
			return data.values == null || Arrays.stream(data.values).anyMatch(s-> s == null || s.isEmpty() || s.equals("[**Missing**]"));
		return (data.translation == null||data.translation.isBlank()||data.translation.equals("[**Missing**]")); 
	}
	
}
