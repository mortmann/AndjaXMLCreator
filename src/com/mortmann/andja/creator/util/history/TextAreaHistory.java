package com.mortmann.andja.creator.util.history;

import java.util.ArrayList;

import com.mortmann.andja.creator.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TextAreaHistory extends TextArea implements Changeable {
	boolean ignoreChange = false;

	public TextAreaHistory(String content) {
		super(content);
		ignoreChange = true;
		Setup();
	}
	
	public TextAreaHistory() {
		Setup();
	}

	private void Setup() {
		ignoreChange = true;
		textProperty().addListener(new ChangeListener<String>() {
	        public void changed(ObservableValue<? extends String> ov,
	        		String old_val, String new_val) {
	        	OnChange(old_val, new_val);
	        }
        });
		addEventFilter(KeyEvent.ANY, e -> {
            if ((e.getCode() == KeyCode.Z || e.getCode() == KeyCode.Y) && e.isShortcutDown()) {
        		ignoreChange = true;
            }
        });	
		
	}

	@Override
	public void Do(Object change) {
		ignoreChange = true;
//		setText((String)change);
		if(isFocused()==false)
			redo();
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}

	@Override
	public void Undo(Object change) {
		ignoreChange = true;
//		setText((String)change);
		if(isFocused()==false)
			undo();
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}
	public void setStartText(String value) {
		ignoreChange = true;
		setText(value);
		
	}

	@Override
	public void OnChange(Object change, Object old) {
		if(ignoreChange) {
			ignoreChange = false;
			return;
		}
		ChangeHistory.AddChange(this, change, old); 
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(old, change, true);
			}
		}
		GUI.Instance.UpdateCurrentTab();
	}
	ArrayList<ChangeListenerHistory> changeListeners;
	@Override
	public void AddChangeListener(ChangeListenerHistory changeListener, boolean first) {
		if(changeListeners==null)
			changeListeners = new ArrayList<ChangeListenerHistory>();
		if(first)
			changeListeners.add(0,changeListener);
		else
			changeListeners.add(changeListener);
	}

	public void setIgnoreFlag() {
		ignoreChange = true;
	}

}
