package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.WorkTab;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class TabableToFloatSetterHistory extends GridPane implements Changeable {
	private GridPane listpane;
	Field field;
	Tabable tabable;
	WorkTab tab;
	ObservableMap<String, ? extends Tabable> hash;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TabableToFloatSetterHistory(String name, Field field, Tabable tabable, WorkTab tab, ObservableMap<String, ? extends Tabable> hash) {
		ObservableList<Tabable> its = FXCollections.observableArrayList();
		its.addAll(hash.values());
		this.hash = hash;
		hash.addListener(new MapChangeListener<String, Tabable>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends Tabable> change) {
				if (change.getValueAdded() != null) {
					its.add(change.getValueAdded());
				}
				if (change.getValueRemoved() != null) {
					its.remove(change.getValueRemoved());
				}
			}
		});
		this.tabable = tabable;
		this.field = field;
		this.tab = tab;
		listpane = new GridPane();
		ColumnConstraints gridcol1 = new ColumnConstraints();
		gridcol1.setMinWidth(75);
		ColumnConstraints gridcol2 = new ColumnConstraints();
		gridcol2.setMinWidth(25);
		getColumnConstraints().addAll(gridcol1, gridcol2);

		ColumnConstraints listcol1 = new ColumnConstraints();
		listcol1.setMinWidth(50);
		ColumnConstraints listcol2 = new ColumnConstraints();
		listcol2.setMinWidth(25);
		ColumnConstraints listcol3 = new ColumnConstraints();
		listcol3.setMinWidth(15);
		listpane.getColumnConstraints().addAll(listcol1, listcol2, listcol3);

		HashMap<String, Float> h = null;
		try {
			h = (HashMap<String, Float>) field.get(tabable);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if(h != null) {
			for (String item : h.keySet()) {
				OnTabableSelect(listpane, field, tabable, hash.get(item), true);
			}
		} else {
			h = new HashMap<String, Float>();
			try {
				field.set(tabable, h);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		ComboBoxHistory<Tabable> box = new ComboBoxHistory<Tabable>(its);
		if(field.isAnnotationPresent(FieldInfo.class)) {
			FieldInfo fi = field.getAnnotation(FieldInfo.class);
			if(fi.ComperatorMethod().isBlank() == false) {
				try {
					java.lang.reflect.Method method = tabable.getClass().getMethod(fi.ComperatorMethod());
					its.sort((Comparator<? super Tabable>) method.invoke(tabable));
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		if (field.getAnnotation(FieldInfo.class) != null) {
			if (field.getAnnotation(FieldInfo.class).required()) {
				ObservableList<String> styleClass = box.getStyleClass();

				styleClass.add("combobox-error");
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {
					HashMap i = null;
					try {
						i = (HashMap) field.get(tabable);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (i == null || i.size() == 0) {
						if (!styleClass.contains("combobox-error")) {
							styleClass.add("combobox-error");
						}
					} else {
						if (styleClass.contains("combobox-error")) {
							styleClass.remove("combobox-error");
						}
					}
				});

			}
		}
		add(new Label(name), 0, h.size() + 1);
		add(box, 1, h.size() + 1);
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnTabableSelect(listpane, field, tabable, box.getSelectionModel().getSelectedItem(), false);
		});
		sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		add(sp, 3, h.size() + 1);

	}

	@SuppressWarnings("unchecked")
	private void OnTabableSelect(GridPane listpane, Field field, Tabable m, Tabable selected, boolean setup) {
		// get existing field if null or not
		Integer rows = listpane.getRowCount();
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		// Name of Item
		Label l = new Label(selected.toString());
		float max = Float.MAX_VALUE;
		float min = 0;
		if (fi != null) {
			max = fi.Maximum();
			min = fi.Minimum();
		}
		// Amount field
		NumberTextField count = new NumberTextField(true, min, max);
		WorkTab.CheckIfRequired(count, field, m);

		count.setMaxWidth(90);
		count.setStartText("0");
		count.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					HashMap<String, Float> h = (HashMap<String, Float>) field.get(tabable);
					h.put(selected.GetID(), count.GetFloatValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				tab.UpdateMethods(count);
			}
		});
		// Remove Button
		Button b = new Button("X");
		// set the press button action
//		int remove = pos;
		b.setOnAction(s -> {
			try {
				// get array
				Item[] array = (Item[]) field.get(m);
				int remove = GridPane.getRowIndex(l);
				// remove the label and button
				listpane.getChildren().removeAll(l, b, count);
				ObservableList<Node> children = FXCollections.observableArrayList(listpane.getChildren());
				listpane.getChildren().clear();
				for (int i = 0; i < children.size(); i += 3) {
					listpane.add(children.get(i), 0, i);
					listpane.add(children.get(i + 1), 1, i);
					listpane.add(children.get(i + 2), 2, i);
				}
				// remove this value and set the array in class
				field.set(m, WorkTab.removeElementFromArray(array, remove));
				OnChange(array, field.get(tabable));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		try {
			if (setup == false) {
				HashMap<String, Float> h = (HashMap<String, Float>) field.get(tabable);
				if (h == null) {
					h = new HashMap<>();
				}
				h.put(selected.GetID(), (float) 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listpane.add(l, 0, rows);
		listpane.add(count, 1, rows);
		listpane.add(b, 2, rows);
		count.unsetIgnoreFlag();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void Do(Object change) {
		try {
			field.set(tabable, change);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		listpane.getChildren().clear();
		HashMap<String, Float> h = null;
		try {
			h = (HashMap<String, Float>) field.get(tabable);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if(h == null) {
			return;
		}
		for (String item : h.keySet()) {
			OnTabableSelect(listpane, field, tabable, hash.get(item), true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void Undo(Object change) {
		try {
			field.set(tabable, change);
			listpane.getChildren().clear();
			HashMap<String, Float> h = null;
			try {
				h = (HashMap<String, Float>) field.get(tabable);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if(h == null) {
				return;
			}
			for (String item : h.keySet()) {
				OnTabableSelect(listpane, field, tabable, hash.get(item), true);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
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
		if (changeListeners == null)
			changeListeners = new ArrayList<ChangeListenerHistory>();
		if (first)
			changeListeners.add(0, changeListener);
		else
			changeListeners.add(changeListener);
	}

}
