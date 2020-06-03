package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.WorkTab;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class ItemArraySetterHistory extends GridPane implements Changeable {
	private GridPane listpane;
	Field field;
	Tabable tabable;
	WorkTab tab;

	public ItemArraySetterHistory(String name, Field field, Tabable tabable, WorkTab tab) {
		ObservableList<Item> its = FXCollections.observableArrayList();
		its.addAll(GUI.Instance.getItems());
		GUI.Instance.idToItem.addListener(new MapChangeListener<String, ItemXML>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends ItemXML> change) {
				if (change.getValueAdded() == null) {
					return;
				}
				Item i = new Item(change.getValueAdded());
				its.add(i);
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
		Item[] oldArray = null;

		try {
			oldArray = (Item[]) field.get(tabable);
		} catch (Exception e1) {
		}
		if (oldArray == null) {
			oldArray = new Item[1];
		} else {
			for (Item item : oldArray) {
				OnItemSelect(listpane, field, tabable, item, true);
			}
		}

		ComboBoxHistory<Item> box = new ComboBoxHistory<Item>(its);
		if (field.getAnnotation(FieldInfo.class) != null) {
			if (field.getAnnotation(FieldInfo.class).required()) {
				ObservableList<String> styleClass = box.getStyleClass();

				styleClass.add("combobox-error");
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {
					Item[] i = null;
					try {
						i = (Item[]) field.get(tabable);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (i == null || i.length == 0) {
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
		add(new Label(name), 0, oldArray.length + 1);
		add(box, 1, oldArray.length + 1);
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnItemSelect(listpane, field, tabable, box.getSelectionModel().getSelectedItem(), false);
		});
		sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		add(sp, 3, oldArray.length + 1);

	}

	private void OnItemSelect(GridPane listpane, Field field, Tabable m, Item select, boolean setup) {
		// get existing field if null or not
		Item[] old = null;
		Integer rows = listpane.getRowCount();
		try {
			old = (Item[]) field.get(m);
		} catch (Exception e1) {
			System.out.println(e1);
		}
		// if null we start at pos 0 else insert at length
		int pos = 0;
		if (old != null) {
			if (setup) {
				for (int i = 0; i < old.length; i++) {
					if (old[i].GetID().equals(select.GetID())) {
						pos = i;
					}
				}
			} else {
				pos = old.length;
			}
		}

		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		// Name of Item
		Label l = new Label(select.toString());
		float max = Integer.MAX_VALUE;
		float min = Integer.MIN_VALUE;
		if (fi != null) {
			max = fi.Maximum();
			min = fi.Minimum();
		}
		// Amount field
		NumberTextField count = new NumberTextField(3, min, max);
		WorkTab.CheckIfRequired(count, field, m);

		count.setMaxWidth(35);
		count.setStartText(select.count + "");
		count.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					select.count = count.GetIntValue();
				} catch (Exception e) {
					e.printStackTrace();
				}
				tab.UpdateMethods(count);
			}
		});
		// Remove Button
		Button b = new Button("X");
		// set the press button action
		int remove = pos;
		b.setOnAction(s -> {
			try {
				// get array
				Item[] array = (Item[]) field.get(m);
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
				// Create newArray in Case old was null
				Item[] newArray = new Item[1];
				if (old != null) {
					// else create a array one bigger than old
					newArray = new Item[old.length + 1];
					// copy over variables
					System.arraycopy(old, 0, newArray, 0, old.length);
				}
				// set the new place in array to selected variable
				newArray[pos] = select;
				field.set(m, newArray);
				OnChange(newArray, field.get(tabable));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listpane.add(l, 0, rows);
		listpane.add(count, 1, rows);
		listpane.add(b, 2, rows);
	}

	@Override
	public void Do(Object change) {
		try {
			field.set(tabable, change);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		listpane.getChildren().clear();
		for (Item item : (Item[]) change) {
			OnItemSelect(listpane, field, tabable, item, true);
		}
	}

	@Override
	public void Undo(Object change) {
		try {
			field.set(tabable, change);
			listpane.getChildren().clear();
			for (Item item : (Item[]) change) {
				OnItemSelect(listpane, field, tabable, item, true);
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
