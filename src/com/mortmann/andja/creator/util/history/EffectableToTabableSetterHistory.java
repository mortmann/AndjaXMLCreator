package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.other.GameEvent;
import com.mortmann.andja.creator.other.GameEvent.Target;
import com.mortmann.andja.creator.util.Tabable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("unchecked")
public class EffectableToTabableSetterHistory extends GridPane implements Changeable {
	
	Field field;
	Tabable tabable;
	private VBox listbox;

	public EffectableToTabableSetterHistory(String name, Field field, Tabable tabable) {
		ComboBoxHistory<Target> box = new ComboBoxHistory<Target>(GameEvent.specialTargetRangeClasses);
		
		this.field = field;
		this.tabable = tabable;
		
		box.setMaxWidth(Double.MAX_VALUE);
		add(new Label(name), 0, 0);	
		add(box, 1, 0);
			
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(75);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(165);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(165);
        getColumnConstraints().addAll(col1,col2,col3);
		ComboBoxHistory<Tabable> tabableBox = new ComboBoxHistory<Tabable>();
		add(tabableBox, 2, 0);

		box.setOnAction(x-> {
			Target c = box.getValue();
			ObservableList<Tabable> tabs = FXCollections.observableArrayList();
			if(c == Target.AllStructure) {
				tabs.addAll(GUI.Instance.idToStructures.values());
			}
			if(c == Target.AllUnit) {
				tabs.addAll(GUI.Instance.idToUnit.values());
			}
			tabableBox.setItems(tabs);
				try {
					if(box.getValue()==null){
						return;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		
		listbox = new  VBox();
        add(listbox, 1, 1);
        GridPane.setColumnSpan(listbox, 2);

		try {		
			HashMap<Target, String[]> map = field.get(tabable)!=null ? (HashMap<Target, String[]>) field.get(tabable) : new HashMap<>();
			for(Target t : map.keySet()) {
				for(String id : map.get(t)) {
					listbox.getChildren().add(CreateHBoxEffectableTabable(id,"" ,t,listbox,map));
				}
			}
			
			tabableBox.setOnAction(x-> {
					try {
						Target target = box.getValue();
						Tabable select = tabableBox.getValue();
						if(tabableBox.getValue()==null){
							return;
						}
						ArrayList<String> temp = new ArrayList<String>();
						if(map.get(target) != null)
							temp.addAll(Arrays.asList(map.get(target)));

						temp.add(tabableBox.getValue().GetID());
						String[] t = new String[1];
						HashMap<Target,String[]> tempMap = new HashMap<>(map);
 						map.put(target, temp.toArray(t));
						field.set(tabable, map);
						listbox.getChildren()
							.add(CreateHBoxEffectableTabable(select.GetID(),select.GetName(),target,listbox,map));
						OnChange(tempMap,map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			);
		} catch (Exception e1) {
			e1.printStackTrace();
		} 

	}

	private Node CreateHBoxEffectableTabable(String selectID,String selectName, Target target, VBox listbox,HashMap<Target, String[]> map) {
		HBox hbox = new HBox();
		// Name
		Label tabable = new Label(selectName);
		// Remove Button
		Button b = new Button("X");
		Label targetLabel = new Label(target.toString());
		hbox.getChildren().addAll (targetLabel,tabable,b);
		// set the press button action
		b.setOnAction(s -> {
			try {
				// remove the label and button
				listbox.getChildren().remove(hbox);
				ArrayList<String> temp = new ArrayList<String>(Arrays.asList(map.get(target)));
				temp.remove(selectID);
				String[] t = new String[1];
				HashMap<Target,String[]> tempMap = new HashMap<>(map);
				map.put(target, temp.toArray(t));
				OnChange(tempMap,map);
				if(temp.isEmpty())
					map.remove(target);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		return hbox;
	}
	
	@Override
	public void Do(Object change) {
		listbox.getChildren().clear();
		HashMap<Target, String[]> map = null;
		try {
			map = field.get(tabable)!=null ? (HashMap<Target, String[]>) field.get(tabable) : new HashMap<>();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		for(Target t : map.keySet()) {
			for(String id : map.get(t)) {
				listbox.getChildren().add(CreateHBoxEffectableTabable(id,"" ,t,listbox,map));
			}
		}
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(map, change, false);
			}
		}
	}

	@Override
	public void Undo(Object change) {
		listbox.getChildren().clear();
		HashMap<Target, String[]> map = null;
		try {
			map = field.get(tabable)!=null ? (HashMap<Target, String[]>) field.get(tabable) : new HashMap<>();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		for(Target t : map.keySet()) {
			for(String id : map.get(t)) {
				listbox.getChildren().add(CreateHBoxEffectableTabable(id,"" ,t,listbox,map));
			}
		}
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(map, change, false);
			}
		}
	}

	@Override
	public void OnChange(Object change, Object old) {
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
}
