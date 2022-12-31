package com.mortmann.andja.creator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;

import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.other.GameEvent.Target;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.structures.Structure.TileType;
import com.mortmann.andja.creator.unitthings.*;
import com.mortmann.andja.creator.util.*;
import com.mortmann.andja.creator.util.history.ChangeHistory;
import com.mortmann.andja.creator.util.history.ChangeListenerHistory;
import com.mortmann.andja.creator.util.history.Changeable;
import com.mortmann.andja.creator.util.history.CheckBoxHistory;
import com.mortmann.andja.creator.util.history.ComboBoxHistory;
import com.mortmann.andja.creator.util.history.EffectableToTabableSetterHistory;
import com.mortmann.andja.creator.util.history.EnumArraySetterHistory;
import com.mortmann.andja.creator.util.history.ItemArraySetterHistory;
import com.mortmann.andja.creator.util.history.NumberTextField;
import com.mortmann.andja.creator.util.history.TabableArraySetterHistory;
import com.mortmann.andja.creator.util.history.TabableToFloatSetterHistory;
import com.mortmann.andja.creator.util.history.TextAreaHistory;
import com.mortmann.andja.creator.util.history.TextFieldHistory;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class WorkTab extends Tab {
	ScrollPane scrollPaneContent;
	GridPane mainGrid;
	GridPane booleanGrid;
	GridPane stringGrid;
	GridPane floatGrid;
	GridPane intGrid;
	GridPane enumGrid;
	GridPane languageGrid;
	GridPane otherGrid;
	HashMap<Method, Label> updateMethodLabel;
	HashMap<String, Changeable> variableToChangeable;
	HashMap<String, Node> variableToNode;

	Tabable myTabable;
	private boolean newTabable;

	public WorkTab(Tabable tabable, boolean newTabable) {
		super();
		mainGrid = new GridPane();
		booleanGrid = new GridPane();
		floatGrid = new GridPane();
		intGrid = new GridPane();
		stringGrid = new GridPane();
		otherGrid = new GridPane();
		languageGrid = new GridPane();
		enumGrid = new GridPane();
		this.newTabable = newTabable;
		mainGrid.setGridLinesVisible(true);
		mainGrid.add(Utility.wrapPaneInTitledPane("Integer", intGrid, false), 0, 0);
		mainGrid.add(Utility.wrapPaneInTitledPane("Boolean", booleanGrid, false), 1, 0);
		mainGrid.add(Utility.wrapPaneInTitledPane("Float", floatGrid, false), 2, 0);
		mainGrid.add(Utility.wrapPaneInTitledPane("Enum", enumGrid, false), 0, 2);
		TitledPane string = Utility.wrapPaneInTitledPane("String", stringGrid, false);
		mainGrid.add(string, 0, 1);
		TitledPane other = Utility.wrapPaneInTitledPane("Other", otherGrid, false);
		GridPane.setColumnSpan(other, 2);
		mainGrid.add(other, 1, 1);
		TitledPane language = Utility.wrapPaneInTitledPane("Language", languageGrid, false);
		mainGrid.add(language, 1, 2);
		GridPane.setColumnSpan(language, 2);

		ColumnConstraints col = new ColumnConstraints();
		ColumnConstraints mid = new ColumnConstraints();

		col.setPercentWidth(40);
		mid.setPercentWidth(30);
		col.setHgrow(Priority.ALWAYS);
		mainGrid.getColumnConstraints().addAll(col, mid, col);

		scrollPaneContent = new ScrollPane();
		mainGrid.setMaxWidth(1225);
		scrollPaneContent.setContent(mainGrid);
		scrollPaneContent.setMaxHeight(Double.MAX_VALUE);
		scrollPaneContent.setMaxWidth(Double.MAX_VALUE);

		variableToChangeable = new HashMap<String, Changeable>();
		variableToNode = new HashMap<String, Node>();
		ClassAction(tabable);
		setText(tabable.GetName());
		setContent(scrollPaneContent);

		setOnCloseRequest(x -> {
			if (ChangeHistory.IsSaved(tabable)) {
				return;
			}
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning!");
			String s = "Any unsaved data will be lost!";
			alert.setContentText(s);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {

			} else {
				x.consume();
			}
		});
		setOnClosed(x -> {
			GUI.Instance.RemoveTab(this, tabable);
		});
		for (Changeable c : variableToChangeable.values()) {
			c.AddChangeListener(new ChangeListenerHistory() {
				@Override
				public void changed(Object old, Object changed, boolean newChange) {
					UpdateMethods(c);
				}
			}, false);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void ClassAction(Tabable t) {

		Class c = t.getClass();
		Field fld[] = c.getFields();
		myTabable = t;
		Arrays.sort(fld, new OrderEr());
		for (int i = 0; i < fld.length; i++) {
			FieldInfo info = fld[i].getAnnotation(FieldInfo.class);
			if (info != null && info.ignore())
				continue;
			Class compare = fld[i].getType();
			if (info != null && info.compareType().equals(void.class) == false) {
				compare = info.compareType();
			}
			if (compare == Boolean.TYPE) {
				booleanGrid.add(CreateBooleanSetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == Float.TYPE) {
				floatGrid.add(CreateFloatSetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == Integer.TYPE) {
				if (fld[i].getAnnotation(FieldInfo.class) != null) {
					if (info.subType() == PopulationLevel.class) {
						intGrid.add(CreateTabableIntSetter(fld[i].getName(), fld[i], myTabable, PopulationLevel.class),
								0, i);
						continue;
					}
				}
				intGrid.add(CreateIntSetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == String.class) {
				if (info != null && info.longtext()) {
					stringGrid.add(CreateLongStringSetter(fld[i].getName(), fld[i], myTabable), 0, i);
				} else {
					stringGrid.add(CreateStringSetter(fld[i].getName(), fld[i], myTabable), 0, i);
				}
			} else if (compare.isEnum()) {
				// This is for all enums makes it way easier in the future to create new ones
				// and removes need to add smth here
				enumGrid.add(CreateEnumSetter(fld[i].getName(), fld[i], myTabable, compare), 0, i);
			} else if (compare == ArrayList.class) {
				if (info == null) {
					System.out.println(
							"[ERROR] This type " + fld[i].getName() + " of field needs a fieldinfo-subtype declared!");
					continue;
				}
				enumGrid.add(CreateEnumArraySetter(fld[i].getName(), fld[i], myTabable, info.subType()), 0, i);
			} else if (compare == Item[].class) {
				otherGrid.add(CreateItemArraySetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == Item.class) {
				otherGrid.add(CreateItemSetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == HashMap.class) {
				if (fld[i].getAnnotation(FieldInfo.class) == null) {
					System.out.println(
							"[ERROR] This type " + fld[i].getName() + " of field needs a fieldinfo-subtype declared!");
					continue;
				}
				if (info.subType() == void.class) {
					System.out.println("[ERROR] This type " + fld[i].getName() + " of field needs a subtype declared!");
					continue;
				}
				if (info.subType() == DamageType.class) {
					otherGrid.add(
							CreateClassToFloatSetter(fld[i].getName(), fld[i], myTabable, GUI.Instance.idToDamageType),
							0, i);
					continue;
				}
				if (info.subType() == ArmorType.class) {
					otherGrid.add(
							CreateClassToFloatSetter(fld[i].getName(), fld[i], myTabable, GUI.Instance.idToArmorType),
							0, i);
					continue;
				}
				if (info.subType() == PopulationLevel.class) {
					otherGrid.add(CreateClassToFloatSetter(fld[i].getName(), fld[i], myTabable,
							GUI.Instance.idToPopulationLevel), 0, i);
					continue;
				}
				if (info.subType() == ItemXML.class) {
					otherGrid.add(CreateTabableToFloatSetter(fld[i].getName(), fld[i], myTabable,
							GUI.Instance.idToItem), 0, i);
					continue;
				}
				if (info.subType() == String.class && info.mainType() == void.class) {
					languageGrid.add(CreateLanguageSetter(fld[i].getName(), fld[i], myTabable), 0, i);
					continue;
				}
				if (info.subType() == String.class && info.mainType() == Climate.class) {
					stringGrid.add(CreateEnumStringMapSetter(fld[i].getName(), fld[i], myTabable, Climate.class), 0, i);
					continue;
				}
				if (info.mainType() == Target.class && info.subType() == Integer.class) {
					otherGrid.add(CreateEffectableToTabableSetter(fld[i].getName(), fld[i], myTabable), 0, i);
					continue;
				}
			} else if (compare == Fertility.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, Fertility.class,
						GUI.Instance.idToFertility), 0, i);
			} else if (compare == NeedGroup.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, NeedGroup.class,
						GUI.Instance.idToNeedGroup), 0, i);
			} else if (compare == PopulationLevel.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, PopulationLevel.class,
						GUI.Instance.idToPopulationLevel), 0, i);
			} else if (compare == Growable.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, Growable.class,
						GUI.Instance.idToStructures), 0, i);
			} else if (compare == Structure.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, Structure.class,
						GUI.Instance.idToStructures), 0, i);
			} else if (compare == NeedStructure.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, NeedStructure.class,
						GUI.Instance.idToStructures), 0, i);
			} else if (compare == DamageType.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, DamageType.class,
						GUI.Instance.idToDamageType), 0, i);
			} else if (compare == ArmorType.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, ArmorType.class,
						GUI.Instance.idToArmorType), 0, i);
			} else if (compare == Worker.class) {
				otherGrid.add(CreateTabableSetter(fld[i].getName(), fld[i], myTabable, Worker.class,
						GUI.Instance.idToWorker), 0, i);
			}  else if (compare == float[].class) {
				otherGrid.add(CreateFloatArraySetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == Unit[].class) {
				otherGrid.add(CreateTabableArraySetter(fld[i].getName(), fld[i], myTabable, Unit[].class,
						GUI.Instance.idToUnit), 0, i);
			} else if (compare == Effect[].class) {
				otherGrid.add(CreateTabableArraySetter(fld[i].getName(), fld[i], myTabable, Effect[].class,
						GUI.Instance.idToEffect), 0, i);
			} else if (compare == NeedStructure[].class) {
				otherGrid.add(CreateTabableArraySetter(fld[i].getName(), fld[i], myTabable, NeedStructure[].class,
						GUI.Instance.idToStructures), 0, i);
			} else if (compare == Structure[].class) {
				otherGrid.add(CreateTabableArraySetter(fld[i].getName(), fld[i], myTabable, Structure[].class,
						GUI.Instance.idToStructures), 0, i);
			} else if (compare == Tabable.class && info.RequiresEffectable()) {
				otherGrid.add(CreateEffectableSetter(fld[i].getName(), fld[i], myTabable), 0, i);
			} else if (compare == TileType[][].class) {
				otherGrid.add(
						CreateEnumTwoDimensionalArraySetter(fld[i].getName(), fld[i], myTabable, TileType.class, true),
						0, i);
			} else {
				System.out.println("Variable Name is: " + fld[i].getName() + " : " + compare);
			}
		}
		Method[] methods = c.getMethods();
		for (Method m : methods) {
			MethodInfo mi = m.getAnnotation(MethodInfo.class);
			if (mi == null)
				continue;
			if (updateMethodLabel == null)
				updateMethodLabel = new HashMap();
			GridPane grid = new GridPane();
			ColumnConstraints col1 = new ColumnConstraints();
			col1.setMinWidth(150);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setMinWidth(75);
			grid.getColumnConstraints().addAll(col1, col2);
			String name = m.getName();
			if (mi.Title() != null && mi.Title().isBlank() == false)
				name = mi.Title();
			grid.add(new Label(name), 0, 0);
			Label result = new Label("NaN");
			try {
				result.setText(m.invoke(t).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateMethodLabel.put(m, result);
			grid.add(result, 1, 0);
			if (mi.BelongingVariable() != null && mi.BelongingVariable().isBlank() == false) {
				if (variableToChangeable.containsKey(mi.BelongingVariable()) == false) {
					System.out.println(
							"ERROR -- Beloning Variable is missing in the variable map. " + mi.BelongingVariable());
					return;
				}
				col1.setMinWidth(75);
				Changeable changeable = variableToChangeable.get(mi.BelongingVariable());
				if (changeable instanceof GridPane) {
					GridPane gp = ((GridPane) changeable);
					gp.add(grid, 1, gp.getRowCount());
				}
				continue;
			}
			Class returnType = m.getReturnType();
			if (returnType == int.class) {
				intGrid.add(grid, 0, intGrid.getRowCount() + 1);
			} else if (returnType == float.class) {
				floatGrid.add(grid, 0, floatGrid.getRowCount() + 1);
			} else if (returnType == String.class) {
				stringGrid.add(grid, 0, stringGrid.getRowCount() + 1);
			} else if (returnType == Enum.class) {
				enumGrid.add(grid, 0, enumGrid.getRowCount() + 1);
			} else {
				otherGrid.add(grid, 0, otherGrid.getRowCount() + 1);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Node CreateTabableIntSetter(String name, Field field, Tabable t, Class tabClass) {
		ArrayList<Tabable> allList = GUI.Instance.getTabableList(tabClass);
		GridPane grid = new GridPane();
		ObservableList<Tabable> strs = FXCollections.observableArrayList(allList);
		ObservableMap<String, Tabable> obsMapTabable = (ObservableMap<String, Tabable>) GUI.Instance.classToClassObservableMap
				.get(tabClass);

		obsMapTabable.addListener(new MapChangeListener<String, Tabable>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Tabable> change) {
				if (change.getValueAdded() != null)
					strs.add(change.getValueAdded());
				if (change.getValueRemoved() != null)
					strs.remove(change.getValueRemoved());
			}
		});
		strs.removeIf(x -> x.getClass().equals(tabClass) == false);
		ComboBoxHistory<Tabable> box = new ComboBoxHistory<Tabable>(strs);
		CheckIfRequired(box, field, myTabable);
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		ObservableList<String> styleClass = box.getStyleClass();
		if (fi != null) {
			if (fi.required()) {
				styleClass.add("combobox-error");
				box.AddChangeListener((old, newValue, newC) -> {
					if (newValue == null || obsMapTabable.containsKey(((Tabable) newValue).GetID()) == false) {
						if (!styleClass.contains("combobox-error")) {
							styleClass.add("combobox-error");
						}
					} else {
						if (styleClass.contains("combobox-error")) {
							styleClass.remove("combobox-error");
						}
					}
				}, true);

			}
		}
		box.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(box, Priority.ALWAYS);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(75);
		grid.getColumnConstraints().addAll(col1, col2);
		try {
			if (obsMapTabable.containsKey(field.get(myTabable) + "")) {
				box.SetValueIgnoreChange(obsMapTabable.get(field.get(myTabable) + ""));
				styleClass.remove("combobox-error");
			}
		} catch (Exception e1) {
		}
		box.SetupDone();

		box.setOnAction(x -> {
			try {
				if (box.getValue() == null) {
					return;
				}
				field.set(myTabable, Integer.valueOf(box.getValue().GetID()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return grid;
	}

	private Node CreateEffectableToTabableSetter(String name, Field field, Tabable tabable) {
		return new EffectableToTabableSetterHistory(name, field, tabable);
	}

	@SuppressWarnings({ "rawtypes" })
	private Node CreateEffectableSetter(String name, Field field, Tabable tab) {
		GridPane grid = new GridPane();
		HashMap<String, Class> nameToClass = new HashMap<>();
		ObservableList<String> names = FXCollections.observableArrayList();
		for (Class c : GameEvent.targetClasses) {
			names.add(c.getSimpleName());
			nameToClass.put(c.getSimpleName(), c);
		}
		ComboBoxHistory<String> box = new ComboBoxHistory<String>(names);
		ComboBoxHistory<String> variablebox = new ComboBoxHistory<String>();
		ObservableList<String> styleClass = box.getStyleClass();
		ObservableList<String> styleClassVariable = variablebox.getStyleClass();

		if (field.getAnnotation(FieldInfo.class) != null) {
			if (field.getAnnotation(FieldInfo.class).required()) {
				styleClassVariable.add("combobox-error");
				styleClass.add("combobox-error");
				variablebox.AddChangeListener((oldValue, newValue, changed) -> {
					System.out.println(oldValue + " " + newValue);
					if (newValue == null) {
						if (!styleClass.contains("combobox-error")) {
							styleClass.add("combobox-error");
							styleClassVariable.add("combobox-error");
						}
					} else {
						if (styleClass.contains("combobox-error")) {
							styleClass.remove("combobox-error");
							styleClassVariable.remove("combobox-error");
						}
					}
				}, true);

			}
		}
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(75);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(165);

		grid.getColumnConstraints().addAll(col1, col2);

		box.setOnAction(x -> {
			ObservableList<String> varnames = FXCollections.observableArrayList();
			for (Field f : nameToClass.get(box.getValue()).getFields()) {
				FieldInfo info = f.getAnnotation(FieldInfo.class);
				if (info == null)
					continue;
				if (info.IsEffectable() == false)
					continue;
				varnames.add(f.getName());
			}
			variablebox.setItems(varnames);
		});
		variablebox.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object old, Object changed, boolean newChange) {
				try {
					if (variablebox.getValue() == null) {
						return;
					}
					field.set(tab, variablebox.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, true);

		try {
			if (field.get(tab) != null) {
				for (Class c : GameEvent.targetClasses) {
					for (Field f : c.getFields()) {
						if (f.getName().equals((String) field.get(tab))) {
							box.SetValueIgnoreChange(c.getSimpleName());
							variablebox.SetValueIgnoreChange((String) field.get(tab));
							styleClass.remove("combobox-error");
							styleClassVariable.remove("combobox-error");
						}
					}
				}
			}
		} catch (Exception e1) {
		}
		box.SetupDone();
		grid.add(variablebox, 1, 1);
		return grid;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Tabable> Node CreateTabableArraySetter(String name, Field field, Tabable tabable,
			Class tabableClass, ObservableMap<String, T> obsMapTabable) {
		return new TabableArraySetterHistory(name, field, tabable, tabableClass, obsMapTabable);
	}

	@SuppressWarnings("rawtypes")
	public <E extends Enum<E>> GridPane CreateEnumIntSetter(String name, Field field, Tabable m, Class<E> class1) {
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(class1)) {
			names.add(e);
		}
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(100);
		col2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col1, col2);
		ComboBoxHistory<Enum> box = new ComboBoxHistory<Enum>(names);
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);

		try {
			if (field.get(m) != null) {
				box.SetValueIgnoreChange((int) field.get(m));
			}
		} catch (Exception ignored) {
		}

		box.setOnAction(x -> {
			try {
				field.set(m, box.getValue().ordinal());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		box.SetupDone();
		return grid;
	}

	@SuppressWarnings("unchecked")
	private Node CreateItemSetter(String name, Field field, Tabable tab) {
		GridPane grid = new GridPane();
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
		if(field.isAnnotationPresent(FieldInfo.class)) {
			FieldInfo fi = field.getAnnotation(FieldInfo.class);
			if(fi.ComperatorMethod().isBlank() == false) {
				try {
					java.lang.reflect.Method method = tab.getClass().getMethod(fi.ComperatorMethod());
					its.sort((Comparator<? super Item>) method.invoke(tab));
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
		ComboBoxHistory<Item> box = new ComboBoxHistory<Item>(its);
		box.setCellFactory(Utility.GetItemListView());
		try {
			if (field.get(tab) != null) {
				// if error i changed from GUI.Instance.idToItem.get(field.get(tab)) to just
				// field.get(tab)
				box.SetValueIgnoreChange((Item) field.get(tab));
			}
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		box.setOnAction(x -> {
			try {
				if (field.isAnnotationPresent(FieldInfo.class)
						&& field.getAnnotation(FieldInfo.class).compareType() == Item.class) {
					field.set(tab, box.getValue().GetID());
				} else {
					field.set(tab, box.getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(75);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(175);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setMinWidth(40);
		grid.getColumnConstraints().addAll(col1, col2, col3);
		grid.add(new Label(field.getName()), 0, 0);
		grid.add(box, 1, 0);
		Button b = new Button("X");
		b.setOnAction(x -> {
			box.getSelectionModel().clearSelection();
		});
		grid.add(b, 2, 0);
		box.SetupDone();

		return grid;
	}

	/// NEEDS FIELDINFO ARRAYPOS
	private Node CreateFloatArraySetter(String name, Field field, Tabable tab) {
		GridPane grid = new GridPane();
		FieldInfo fi = field.getDeclaredAnnotation(FieldInfo.class);

		String[] temp = null;
		if (fi.arraypos().isEnum()) {
			ErrorHelper.ShowErrorAlert("NOT IMPLEMENTED Enum for CreateFloatArraySetter",
					fi.arraypos().getCanonicalName());
		} else {
			temp = new String[fi.arraypos().getMethods().length];
			for (int i = 0; i < fi.arraypos().getMethods().length; i++) {
				temp[i] = fi.arraypos().getMethods()[i].getName();
			}
		}
		float[] val = null;
		try {
			val = (float[]) field.get(tab);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		String[] names = temp;
		for (int i = 0; i < names.length; i++) {
			grid.add(new Label(names[i]), 0, i);
			NumberTextField ntf = new NumberTextField(true, fi.Minimum(), fi.Maximum());
			CheckIfRequired(ntf, field, tab);
			if (val != null) {
				ntf.setStartText(val[i] + "");
			} else {
				ntf.setText("");
			}
			ntf.unsetIgnoreFlag();
			int pos = i;
			ntf.AddChangeListener(new ChangeListenerHistory() {
				@Override
				public void changed(Object arg0, Object arg1, boolean newChange) {
					try {
						float[] val = (float[]) field.get(tab);
						if (val == null) {
							val = new float[names.length];
						}
						val[pos] = ntf.GetFloatValue();
						field.set(tab, val);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}

				}
			}, true);
			grid.add(ntf, 1, i);
		}
		return grid;
	}

	@SuppressWarnings("unchecked")
	private <T extends Tabable> Node CreateClassToFloatSetter(String name, Field field, Tabable t, ObservableMap<String, T> hash) {
		GridPane grid = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(75);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(165);
		grid.getColumnConstraints().addAll(col1, col2);
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		int row = 0;
		try {
			HashMap<String, Float> h = (HashMap<String, Float>) field.get(t);
			if (h == null) {
				h = new HashMap<>();
			}
			if(fi.PresetDefaultForHashMapTabable()) {
				for(String tID : hash.keySet()) {
					if(h.containsKey(tID)==false) {
						h.put(tID, 0f);
					}
				}
			}
			hash.addListener(new MapChangeListener<String, T>() {
				@Override
				public void onChanged(
						javafx.collections.MapChangeListener.Change<? extends String, ? extends T> change) {
					if (change.getValueAdded() == null) {
						return; // doin nothin for removed for now
					}
					int row = grid.getChildren().size() / 2; // this is gonna be fixed in java 9
					// but for now there is no good way to get row index
					Tabable tab = change.getValueAdded();
					grid.add(new Label(tab.toString()), 0, row);
					NumberTextField ntf = new NumberTextField(true, fi.Minimum(), fi.Maximum());
					CheckIfRequired(ntf, field, tab);
					ntf.AddChangeListener(new ChangeListenerHistory() {
						@Override
						public void changed(Object arg0, Object arg1, boolean newChange) {
							try {
								HashMap<String, Float> ha = (HashMap<String, Float>) field.get(t);
								if (ha == null) {
									ha = new HashMap<>();
								}
								ha.put(tab.GetID(), ntf.GetFloatValue());
								field.set(t, ha);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, true);
					ntf.unsetIgnoreFlag();
					row++;
				}

			});
			for (Tabable tab : hash.values()) {
				grid.add(new Label(tab.toString()), 0, row);
				NumberTextField ntf = new NumberTextField(true, fi.Minimum(), fi.Maximum());
				try {
					if (h.containsKey(tab.GetID()))
						ntf.setStartText((Float) h.get(tab.GetID()) + "");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				ntf.unsetIgnoreFlag();
				ntf.AddChangeListener(new ChangeListenerHistory() {
					@Override
					public void changed(Object arg0, Object arg1, boolean newChange) {
						try {
							HashMap<String, Float> ha = (HashMap<String, Float>) field.get(t);
							if (ha == null) {
								ha = new HashMap<>();
							}
							ha.put(tab.GetID(), ntf.GetFloatValue());
							field.set(t, ha);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, true);
				grid.add(ntf, 1, row);
				row++;
			}
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			e2.printStackTrace();
		}

		return grid;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <E extends Enum<E>> Node CreateEnumArraySetter(String name, Field field, Tabable tabable, Class<E> class1) {
		return new EnumArraySetterHistory(name, field, tabable, class1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Tabable> Node CreateTabableSetter(String name, Field field, Tabable m, Class str,
			ObservableMap<String, T> obsMapTabable) {
		GridPane grid = new GridPane();
		ObservableList<Tabable> strs = FXCollections.observableArrayList(obsMapTabable.values());
		strs.sort(Comparator.comparing(Tabable::GetID));
		obsMapTabable.addListener((MapChangeListener<String, T>) change -> {

			if (change.getValueAdded() != null) {
				if(change.getValueAdded().getClass().equals(str))
					strs.add(change.getValueAdded());
			}
			if (change.getValueRemoved() != null)
				strs.remove(change.getValueRemoved());
		});
		strs.removeIf(x -> str.isAssignableFrom(x.getClass()) == false);
		ComboBoxHistory<Tabable> box = new ComboBoxHistory<Tabable>(strs);
		CheckIfRequired(box, field, m);
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		ObservableList<String> styleClass = box.getStyleClass();

		if (fi != null) {
			if (fi.required()) {
				styleClass.add("combobox-error");
				box.AddChangeListener((old, newValue, newC) -> {
					if (newValue == null || obsMapTabable.containsKey(((Tabable) newValue).GetID()) == false) {
						if (!styleClass.contains("combobox-error")) {
							styleClass.add("combobox-error");
						}
					} else {
						if (styleClass.contains("combobox-error")) {
							styleClass.remove("combobox-error");
						}
					}
				}, true);

			}
		}
		box.setCellFactory(new Callback<>() {
			@Override
			public ListCell<Tabable> call(ListView<Tabable> view) {
				return new ListCell<>() {

					@Override
					public void updateSelected(boolean selected) {
						super.updateSelected(selected);
					}

					@Override
					protected void updateItem(Tabable item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							return;
						}
						setText(item.toString());
						setStyle("-fx-text-fill: black; -fx-background-color: " + getItem().GetButtonColor() + ";");
					}

				};
			}

		});

		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);
		Button b = new Button("X");
		b.setOnAction(x -> {
			if (fi != null && fi.required() && styleClass.contains("combobox-error") == false)
				styleClass.add("combobox-error");
			box.getSelectionModel().clearSelection();
		});
		grid.add(b, 2, 0);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(75);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(165);
		grid.getColumnConstraints().addAll(col1, col2);
		try {
			if (field.get(m) != null && obsMapTabable.containsKey(field.get(m))) {
				box.SetValueIgnoreChange(obsMapTabable.get(field.get(m)));
				styleClass.remove("combobox-error");
			}
		} catch (Exception e1) {
		}

		box.setOnAction(x -> {
			try {
				if (box.getValue() == null) {
					return;
				}
				field.set(m, box.getValue().GetID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return grid;
	}

	@SuppressWarnings("unchecked")
	private Node CreateLanguageSetter(String name, Field field, Tabable str) {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(50);
		grid.getColumnConstraints().addAll(col1, col2);

		ArrayList<String> langes = new ArrayList<>(Arrays.asList(GUI.Languages));
		grid.add(new Label(field.getName()), 0, 0);

		for (int i = 0; i < langes.size(); i++) {
			grid.add(new Label(langes.get(i).toString()), 0, i + 1);
			StringProperty temp = null;
			FieldInfo fi = field.getDeclaredAnnotation(FieldInfo.class);
			Node t = null;
			if (fi != null && fi.longtext()) {
				t = new TextAreaHistory();
				((TextAreaHistory) t).setPrefRowCount(5);
				((TextAreaHistory) t).setPrefColumnCount(100);
				((TextAreaHistory) t).setWrapText(true);
				((TextAreaHistory) t).setPrefWidth(400);
				((TextAreaHistory) t).setIgnoreFlag();

				temp = ((TextAreaHistory) t).textProperty();
				((TextAreaHistory) t).setIgnoreFlag();
			} else {
				t = new TextFieldHistory();
				t.prefWidth(400);
				temp = ((TextFieldHistory) t).textProperty();
				((TextFieldHistory) t).setIgnoreFlag();
			}
			StringProperty sp = temp;
			CheckIfRequired(t, field, str);
			int num = i;

			try {
				HashMap<String, String> h = (HashMap<String, String>) field.get(str);
				if (h != null) {
					sp.set(h.get(langes.get(i).toString()));
				}
			} catch (Exception e1) {

			}
			if(t instanceof TextFieldHistory) {
				((TextFieldHistory) t).unsetIgnoreFlag();
			}
			if(t instanceof TextAreaHistory) {
				((TextAreaHistory) t).unsetIgnoreFlag();
			}
			((Changeable) t).AddChangeListener(new ChangeListenerHistory() {
				@Override
				public void changed(Object arg0, Object arg1, boolean newChange) {
					try {
						HashMap<String, String> h = (HashMap<String, String>) field.get(str);
						if (h == null) {
							h = new HashMap<>();
						}
						h.put(langes.get(num).toString(), sp.get());
						field.set(str, h);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, true);
			grid.add(t, 1, i + 1);

		}
		return grid;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private<E extends Enum<E>> Node CreateEnumStringMapSetter(String name, Field field, Tabable str, Class<E> class1) {
		GridPane grid = new GridPane();
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(class1)) {
			names.add(e);
		}

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(50);
		grid.getColumnConstraints().addAll(col1, col2);

		grid.add(new Label(field.getName()), 0, 0);

		for (int i = 0; i < names.size(); i++) {
			grid.add(new Label(names.get(i).toString()), 0, i + 1);
			TextFieldHistory t = new TextFieldHistory();
			CheckIfRequired(t, field, str);
			int num = i;
			try {
				HashMap<E, String> h = (HashMap<E, String>) field.get(str);
				if (h != null) {
					t.setStartText(h.get(names.get(i)));
				}
			} catch (Exception e1) {

			}
			((Changeable) t).AddChangeListener(new ChangeListenerHistory() {
				@Override
				public void changed(Object arg0, Object arg1, boolean newChange) {
					try {
						HashMap<E, String> h = (HashMap<E, String>) field.get(str);
						if (h == null) {
							h = new HashMap<>();
						}
						h.put((E) names.get(num), t.getText());
						field.set(str, h);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, true);
			grid.add(t, 1, i + 1);

		}

		return grid;
	}
	private Node CreateItemArraySetter(String name, Field field, Tabable tabable) {
		return new ItemArraySetterHistory(name, field, tabable, this);
	}
	
	private Node CreateTabableToFloatSetter(String name, Field field, Tabable tabable, ObservableMap<String, ? extends Tabable> hash) {
		return new TabableToFloatSetterHistory(name, field, tabable, this, hash);
	}
	
	public GridPane CreateBooleanSetter(String name, Field field, Tabable m) {
		GridPane grid = new GridPane();
		CheckBoxHistory box = new CheckBoxHistory(name);
		try {
			box.setSelectedOverride((boolean) field.get(m));
		} catch (Exception e1) {
		}
		box.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object arg0, Object arg1, boolean newChange) {
				try {
					field.setBoolean(m, box.isSelected());
				} catch (Exception e) {
					e.printStackTrace();
				}
				UpdateMethods(box);
			}
		}, true);
		grid.add(box, 0, 0);

		return grid;
	}

	public GridPane CreateFloatSetter(String name, Field field, Tabable tabable) {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(75);
		grid.getColumnConstraints().addAll(col1, col2);
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		float max = Integer.MAX_VALUE;
		float min = Integer.MIN_VALUE;
		if (fi != null) {
			max = fi.Maximum();
			min = fi.Minimum();
		}
		NumberTextField box = new NumberTextField(true, min, max);
		CheckIfRequired(box, field, tabable);
		variableToChangeable.put(field.getName(), box);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);
		try {
			if (field.get(tabable) != null) {
				box.setIgnoreFlag();
				box.setStartText(field.get(tabable).toString());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		box.unsetIgnoreFlag();
		box.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object arg0, Object arg1, boolean newChange) {
				try {
					field.setFloat(tabable, box.GetFloatValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				UpdateMethods(box);
			}
		}, true);
		return grid;
	}

	@SuppressWarnings("unchecked")
	public GridPane CreateIntSetter(String name, Field field, Tabable str) {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(75);
		grid.getColumnConstraints().addAll(col1, col2);
		FieldInfo info = field.getAnnotation(FieldInfo.class);
		float max = Integer.MAX_VALUE;
		float min = Integer.MIN_VALUE;
		if (info != null) {
			max = info.Maximum();
			min = info.Minimum();
		}
		NumberTextField ntf = new NumberTextField(min, max);
		variableToChangeable.put(field.getName(), ntf);

		CheckIfRequired(ntf, field, str);
		// NEEDED FOR POPULATIONSLEVEL!
		if (info != null && info.id() && newTabable) {
			try {
				Optional<Tabable> opt = (Optional<Tabable>) GUI.Instance.GetObservableList(str.getClass()).values()
						.stream().max(new Comparator<Tabable>() {
							@Override
							public int compare(Tabable o1, Tabable o2) {
								return o1.GetID().compareTo(o2.GetID());
							}
						});
				if (opt.isEmpty())
					field.setInt(str, 0);
				else
					field.setInt(str, Integer.valueOf(opt.get().GetID()) + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			if (field.get(str) != null) {
				ntf.setStartText((Integer) field.get(str) + "");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		grid.add(new Label(name), 0, 0);
		grid.add(ntf, 1, 0);
		ntf.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object arg0, Object arg1, boolean newChange) {
				try {
					field.setInt(str, ntf.GetIntValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				UpdateMethods(ntf);
			}
		}, true);
		ntf.unsetIgnoreFlag();
		return grid;
	}

	public GridPane CreateLongStringSetter(String name, Field field, Tabable str) {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(75);
		grid.getColumnConstraints().addAll(col1, col2);
		TextAreaHistory box = new TextAreaHistory();
		variableToChangeable.put(field.getName(), box);

		box.setPrefRowCount(10);
		box.setPrefColumnCount(1000);
		box.setWrapText(true);
		box.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		CheckIfRequired(box, field, str);

		try {
			if (field.get(str) != null) {
				box.setStartText((String) field.get(str));
			}
		} catch (Exception e1) {
		}
		box.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object arg0, Object arg1, boolean newChange) {
				try {
					field.set(str, box.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
				UpdateMethods(box);
			}
		}, true);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);

		return grid;
	}

	public GridPane CreateStringSetter(String name, Field field, Tabable str) {
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(75);
		grid.getColumnConstraints().addAll(col1, col2);

		TextFieldHistory box = new TextFieldHistory();
		CheckIfRequired(box, field, str);
		variableToChangeable.put(field.getName(), box);
		try {
			if (field.get(str) != null) {
				box.setStartText((String) field.get(str));
			}
		} catch (Exception e1) {
		}
		box.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object arg0, Object arg1, boolean newChange) {
				try {
					field.set(str, box.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
				UpdateMethods(box);
			}
		}, true);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);
		box.unsetIgnoreFlag();
		return grid;
	}

	@SuppressWarnings("rawtypes")
	public <E extends Enum<E>> GridPane CreateEnumSetter(String name, Field field, Tabable m, Class<E> class1) {
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(class1)) {
			names.add(e);
		}
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(100);
		col2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col1, col2);
		ComboBoxHistory<Enum> box = new ComboBoxHistory<Enum>(names);
		variableToChangeable.put(field.getName(), box);
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);

		try {
			if (field.get(m) != null) {
				box.SetValueIgnoreChange(field.get(m));
			}
		} catch (Exception e1) {
		}

		box.setOnAction(x -> {
			try {
				field.set(m, box.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		box.SetupDone();

		return grid;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <E extends Enum<E>> GridPane CreateEnumTwoDimensionalArraySetter(String name, Field field, Tabable m,
			Class<E> enumClass, boolean newSetup) {
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		GridPane grid = new GridPane();
		variableToNode.put(name, grid);
		Field firstField;
		Field secondField;
		int first = 0;
		int second = 0;
		try {
			firstField = m.getClass().getField(fi.First2DName());
			secondField = m.getClass().getField(fi.Second2DName());
			first = firstField.getInt(m);
			second = secondField.getInt(m);
			if (newSetup) {
				ChangeListenerHistory changeListener = new ChangeListenerHistory() {
					@Override
					public void changed(Object old, Object newV, boolean newChange) {
						try {
							Node node = variableToNode.get(name);
							Object oldArray = field.get(m);
							otherGrid.getChildren().remove(node);
							otherGrid.add(CreateEnumTwoDimensionalArraySetter(name, field, myTabable, enumClass, false),
									0, GridPane.getRowIndex(node));
							if (newChange) {
								ChangeHistory.AddToLastChange(field, m, field.get(m), oldArray);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				};
				variableToChangeable.get(fi.First2DName()).AddChangeListener(changeListener, false);
				variableToChangeable.get(fi.Second2DName()).AddChangeListener(changeListener, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (field.get(m) != null) {
				E[][] d2a = (E[][]) field.get(m);
				int currSecond = 0;
				for (int i = 0; i < d2a.length; i++) {
					if(d2a[i] != null)
						currSecond = d2a[i].length;
				}
				
				if (first != d2a.length || second != currSecond) {
					E[][] temp = (E[][]) Array.newInstance(enumClass, first, second);
					for (int x = 0; x < Math.min(d2a.length, first); x++) {
						for (int y = 0; y < Math.min(d2a[x].length, second); y++) {
							temp[x][y] = d2a[x][y];
						}
					}
					field.set(m, temp);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			e2.printStackTrace();
		}
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(enumClass)) {
			names.add(e);
		}
		grid.add(new Label(name), 0, 0);
		for (int x = 0; x < first; x++) {
			for (int y = 0; y < second; y++) {
				int dx = x;
				int dy = y;
				ComboBoxHistory<Enum> box = new ComboBoxHistory<Enum>(names);
				box.setMaxWidth(Double.MAX_VALUE);
				grid.add(box, 1 + x, 1 + y);
				try {
					if (field.get(m) != null) {
						box.SetValueIgnoreChange(((E[][]) field.get(m))[dx][dy]);
					}
				} catch (Exception e1) {
				}
				int tf = first;
				int ts = second;
				box.setOnAction(de -> {
					try {
						try {
							if (field.get(m) == null) {
								field.set(m, (E[][]) Array.newInstance(enumClass, tf, ts));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						E[][] d2a = (E[][]) field.get(m);
						d2a[dx][dy] = (E) box.getValue();
						field.set(m, d2a);

						for (int i = 0; i < d2a.length; i++) {
							for (int j = 0; j < d2a[i].length; j++) {
								if (d2a[i][j] == EnumSet.allOf(enumClass).toArray()[0])
									field.set(m, null);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				box.SetupDone();
			}
		}
		return grid;
	}


	@SuppressWarnings("unchecked")
	public static <T> T[] removeElementFromArray(T[] a, int del) {
		if (a.length - 1 == 0) {
			return (T[]) Array.newInstance(a.getClass().getComponentType(), 0);
		}
		T[] newA = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length - 1);
		int newI = 0;// new array pos
		for (int i = 0; i < a.length; i++) { // increase old array pos
			if (i == del) { // its the one to copy -> skip
				continue;
			}
			newA[newI] = a[i]; // just copy
			newI++; // increase new array pos
		}
		return newA;
	}

	public void UpdateMethods(Changeable changeable) {
		UpdateFields();
		if (updateMethodLabel == null)
			return;
		for (Method m : updateMethodLabel.keySet()) {
			MethodInfo mi = m.getAnnotation(MethodInfo.class);
			if (mi == null)
				continue;
			try {
				updateMethodLabel.get(m).setText(m.invoke(myTabable).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void CheckIfRequired(Node text, Field field, Tabable t) {
		FieldInfo info = field.getAnnotation(FieldInfo.class);
		if (info == null || info.required() == false) {
			return;
		}
		ObservableList<String> styleClass = text.getStyleClass();

		StringProperty temp = null;
		if (text instanceof TextFieldHistory) {
			temp = ((TextFieldHistory) text).textProperty();
		} else if (text instanceof TextAreaHistory) {
			temp = ((TextAreaHistory) text).textProperty();
		}
		if (temp == null)
			return;
		styleClass.add("text-field-error");
		StringProperty sp = temp;
		sp.addListener((arg0, oldValue, newValue) -> {
			if (info.id()) {
				if (text instanceof NumberTextField) {
					boolean exist = GUI.Instance.doesIDexistForTabable(((NumberTextField) text).GetIntValue(), t);
					if (exist) {
						if (!styleClass.contains("text-field-warning")) {
							styleClass.add("text-field-warning");
						}
					} else {
						if (styleClass.contains("text-field-warning")) {
							styleClass.remove("text-field-warning");
						}
					}
				} else if (text instanceof TextFieldHistory) {
					String id = ((TextFieldHistory) text).textProperty().getValueSafe();
					// remove punctuations
					if (id.matches("(?s).*[\\p{Punct}\\s&&[^_]]+.*")) {
						System.out.println("Char not allowed.");
						Alert a = new Alert(AlertType.INFORMATION,
								"IDs should be simple and contain no duplicates for all types.", ButtonType.OK);
						a.setTitle("Charakter not allowed.");
						a.setHeaderText("ID cannot contain Punctuation, Whitespaces or Special Charakters.");
						a.show();
					}
					id = id.replaceAll("[\\p{Punct}\\s&&[^_]]+", "");
					((TextFieldHistory) text).textProperty().set(id);
					if (GUI.Instance.doesIDexistForTabable(id, t)) {
						if (!styleClass.contains("text-field-warning")) {
							styleClass.add("text-field-warning");
						}
					} else {
						if (styleClass.contains("text-field-warning")) {
							styleClass.remove("text-field-warning");
						}
					}
				} else {
					System.out.println("Non integer&string ids are not supported atm!");
				}
			}
			if (sp.getValue().isEmpty()) {
				if (!styleClass.contains("text-field-error")) {
					styleClass.add("text-field-error");
				}
			} else if (text instanceof NumberTextField) {
				if (((NumberTextField) text).IsValidEntry()) {
					if (styleClass.contains("text-field-error")) {
						styleClass.remove("text-field-error");
					}
				}
			} else {
				if (styleClass.contains("text-field-error")) {
					styleClass.remove("text-field-error");
				}
			}

		});

	}

	public ScrollPane getScrollPaneContent() {
		return scrollPaneContent;
	}

	public void setScrollPaneContent(ScrollPane scrollPaneContent) {
		this.scrollPaneContent = scrollPaneContent;
	}

	public Tabable getTabable() {
		return myTabable;
	}

	public void UpdateFields() {

	}
}
