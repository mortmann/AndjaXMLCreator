package com.mortmann.andja.creator.util.history;

import java.util.ArrayList;

import com.mortmann.andja.creator.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class ComboBoxHistory<T> extends ComboBox<T> implements Changeable {
    private ArrayList<UndoListener> listeners = new ArrayList<UndoListener>();
	boolean ignoreChange = false;

	public ComboBoxHistory() {
		super();
		Setup();
	}
	
	public ComboBoxHistory(ObservableList<T> specialtargetrangeclasses) {
		super(specialtargetrangeclasses);
		Setup();
	}

	private void Setup() {
		ignoreChange = true;
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
	        public void changed(ObservableValue<? extends T> ov,
	        		T old_val, T new_val) {
	        	OnChange(old_val, new_val);
	        }
        });
	}

	@Override
	public void Do(Object change) {
		ignoreChange = true;
		SetValue(change);
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void SetValue(Object change) {
		try {
			getSelectionModel().select((T)change);
		} catch(Exception e) {
			System.out.println("Wrong type given to ComboBox");
		}	
	}
	@SuppressWarnings("unchecked")
	public void SetValueIgnoreChange(Object change) {
		try {
			ignoreChange = true;
			getSelectionModel().select((T)change);
		} catch(Exception e) {
			System.out.println("Wrong type given to ComboBox");
		}	
	}
	@Override
	public void Undo(Object change) {
		ignoreChange = true;
		SetValue(change);
		for (UndoListener ul : listeners)
			ul.OnUndo(change);
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}
	public void addListener(UndoListener toAdd) {
        listeners.add(toAdd);
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
		if(changeListeners == null)
			changeListeners = new ArrayList<ChangeListenerHistory>();
		if(first)
			changeListeners.add(0,changeListener);
		else
			changeListeners.add(changeListener);
	}
	public interface UndoListener {
		public void OnUndo(Object Change);
	}
	
}
