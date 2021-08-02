package com.mortmann.andja.creator.util;

import java.lang.reflect.Field;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.history.ChangeListenerHistory;
import com.mortmann.andja.creator.util.history.NumberTextField;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

@Root
public class Range {
	@Element public int lower;
	@Element public int upper;
	
	public static Node CreateSetter(String name, Field field, Object object, boolean objectIsRange) {
		Range range = null;
		if(objectIsRange == false) {
			try {
				range = (Range) field.get(object);
				if(range == null) {
					range = new Range();
				}
				field.set(object,range);
			} catch (Exception e) {
			} 
		} else {
			range = (Range) object;
		}
		
		Range finalRange = range;
		ColumnConstraints col = new ColumnConstraints();
		col.setMinWidth(50);
		col.setHgrow(Priority.ALWAYS);
		GridPane grid = new GridPane();
		grid.getColumnConstraints().addAll(col, col, col);

		grid.add(new Label(name), 0, 0);
		grid.add(new Label("Lower:"), 0, 1);
		NumberTextField lower = new NumberTextField(range.lower+"");
		lower.setStartText(range.lower+"");
		lower.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object old, Object changed, boolean newChange) {
				finalRange.lower = lower.GetIntValue();
			}
		}, true);
		grid.add(lower, 1, 1);
		grid.add(new Label("Upper:"), 2, 1);
		NumberTextField upper = new NumberTextField(range.upper+"");
		upper.setStartText(range.upper+"");
		upper.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object old, Object changed, boolean newChange) {
				finalRange.upper = upper.GetIntValue();
			}
		}, true);
		lower.unsetIgnoreFlag();
		upper.unsetIgnoreFlag();
		grid.add(upper, 3, 1);
		return grid;
	}

	
}
