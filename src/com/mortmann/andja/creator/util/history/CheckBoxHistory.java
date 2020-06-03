package com.mortmann.andja.creator.util.history;

import java.util.ArrayList;

import com.mortmann.andja.creator.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

public class CheckBoxHistory extends CheckBox implements Changeable {
	boolean ignoreChange = false;

	public CheckBoxHistory(String name) {
		super(name);
		ignoreChange = true;
		selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				OnChange(old_val, new_val);
			}
		});
	}

	@Override
	public void Do(Object change) {
		ignoreChange = true;
		setSelected((boolean) change);
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}

	@Override
	public void Undo(Object change) {
		ignoreChange = true;
		setSelected((boolean) change);
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}

	@Override
	public void OnChange(Object change, Object old) {
		if (ignoreChange) {
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
		if (changeListeners == null)
			changeListeners = new ArrayList<ChangeListenerHistory>();
		if (first)
			changeListeners.add(0, changeListener);
		else
			changeListeners.add(changeListener);
	}

	public void setSelectedOverride(boolean b) {
		setSelected(b);
		ignoreChange = false;
	}

}
