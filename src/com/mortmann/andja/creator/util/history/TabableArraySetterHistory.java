package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.WorkTab;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class TabableArraySetterHistory<T extends Tabable> extends GridPane implements Changeable {

	private GridPane listpane;
	Field field;
	ObservableMap<String, T> obsMapTabable;
	Tabable tabable;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TabableArraySetterHistory(String name, Field field, Tabable tabable, Class tabableClass, ObservableMap<String, T> obsMapTabable) {
		ObservableList<Tabable> tabables = FXCollections.observableArrayList();
		tabables.addAll(obsMapTabable.values());
		this.field = field;
		this.obsMapTabable = obsMapTabable;
		this.tabable = tabable;
		Class singleItemClass = tabableClass.getComponentType();
		if(singleItemClass != null && Structure.class.isAssignableFrom(singleItemClass) ) {
			//if its a structure we need only THAT type of structure in the list so we need to filter the rest out.
			tabables.removeIf(p->{
				return singleItemClass.isAssignableFrom(p.getClass())==false;
			});
		} 
		
		if(field.isAnnotationPresent(FieldInfo.class)) {
			FieldInfo fi = field.getAnnotation(FieldInfo.class);
			if(fi.ComperatorMethod().isBlank() == false) {
				try {
					java.lang.reflect.Method method = tabable.getClass().getMethod(fi.ComperatorMethod());
					tabables.sort((Comparator<? super Tabable>) method.invoke(tabable));
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
		
		obsMapTabable.addListener(new MapChangeListener<String,Tabable>(){
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends Tabable> change) {
				if(change.getValueAdded()==null){
					return;
				}
				if(Structure.class.isAssignableFrom(singleItemClass)) {
					if(change.getValueAdded().getClass() != singleItemClass) {
						return;
					}
				}
				tabables.add(change.getValueAdded());
			}
		});
		
		listpane = new GridPane();
        ColumnConstraints gridcol1 = new ColumnConstraints();
        gridcol1.setMinWidth(75);
        ColumnConstraints gridcol2 = new ColumnConstraints();
        gridcol2.setMinWidth(25);
        
        getColumnConstraints().addAll(gridcol1,gridcol2);
        
        ColumnConstraints listcol1 = new ColumnConstraints();
        listcol1.setMinWidth(50);
        ColumnConstraints listcol2 = new ColumnConstraints();
        listcol2.setMinWidth(50);
        
        listpane.getColumnConstraints().addAll(listcol1,listcol2);
        String[] oldArray = null;
		
		try {
			oldArray = (String[]) field.get(tabable);
		} catch (Exception e1) {
		}
		if(oldArray ==null){
			oldArray = new String[1];
			oldArray[0] = "";
		} else {
			for (String id : oldArray) {
				OnArrayClassSelect(listpane, field, tabable, obsMapTabable.get(id) ,true);
			}
		}
		
		ComboBoxHistory<Tabable> box = new ComboBoxHistory<Tabable>(tabables);
		if(oldArray[0] != ""){
			String filter =oldArray[0];
			FilteredList<Tabable> filterd = tabables.filtered(x-> x.GetID().equals(filter));
			if(filterd.size()>0)
				box.getSelectionModel().select(tabables.indexOf(filterd.get(0)));
		}
		if(field.getAnnotation(FieldInfo.class)!=null){
			if(field.getAnnotation(FieldInfo.class).required()){
			    ObservableList<String> styleClass = box.getStyleClass();
				if(oldArray[0].isEmpty()){
					styleClass.add("combobox-error");
				}
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {		
					String[] i = null;
					try {
						i = (String[]) field.get(tabable);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(newValue == null && i==null){
			    	    if(!styleClass.contains("combobox-error")) {
			    	        styleClass.add("combobox-error");
			    	    }
			        } else {
			        	if(styleClass.contains("combobox-error")) {
			    	        styleClass.remove("combobox-error");
			    	    }
			        }
				});

			}
		}
		add(new Label(name), 0, oldArray.length+1);	
		add(box, 1, oldArray.length+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnArrayClassSelect(listpane,field, tabable,(T)box.getSelectionModel().getSelectedItem(),false);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		add(sp, 3, oldArray.length+1);
	}
	private void OnArrayClassSelect(GridPane listpane, Field field, Tabable m, T select,boolean setup){
		//get existing field if null or not
		String[] old = null;
		int rows = listpane.getRowCount();
		try {
			old = (String[]) field.get(m); 
		} catch (Exception e1) {
			System.out.println(e1);
		}
		//if null we start at pos 1 else insert at length+1
		int pos = 1;
		if(old != null){
			pos = old.length+1;
			for (int i = 0; i < old.length; i++) {
				if(old[i].equals(select.GetID()) ){
					if(setup== false){
						return;
					}
					pos = i;
				}
			}
		}
		// Name
		Label l = new Label(select.toString());
		// Remove Button
		Button b = new Button("X");
		// set the press button action
//		int remove = pos;
		b.setOnAction(s -> {
			try {
				// get array
				String[] array = (String[]) field.get(m);
				// remove the label and button
				int remove = GridPane.getRowIndex(l);
				listpane.getChildren().removeAll(l, b);
				ObservableList<Node> children = FXCollections.observableArrayList(listpane.getChildren());
				listpane.getChildren().clear();
				for (int i = 0; i < children.size(); i += 2) {
					listpane.add(children.get(i), 0, i);
					listpane.add(children.get(i + 1), 1, i);
				}
				// remove this value and set the array in class
				field.set(m, WorkTab.removeElementFromArray(array, remove));
				OnChange(array, field.get(tabable));

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		try {
			if(setup==false){
				// Create newArray in Case old was null
				String[] newArray = new String[1];
				if(old != null){
					//else create a array one bigger than old
					newArray = new String[old.length + 1];
					//copy over variables
					System.arraycopy(old,0,newArray,0,old.length);
				}
				//set the new place in array to selected variable 
				newArray[pos-1] = select.GetID();
				field.set(m, newArray);
				OnChange(newArray, old);
			}
 		} catch (Exception e) {
			e.printStackTrace();
		}
		listpane.add(l, 0, rows);
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
		for (String id : (String[]) change) {
			OnArrayClassSelect(listpane, field, tabable, obsMapTabable.get(id) ,true);
		}
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}

	@Override
	public void Undo(Object change) {
		try {
			field.set(tabable, change);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		listpane.getChildren().clear();
		for (String id : (String[]) change) {
			OnArrayClassSelect(listpane,field, tabable, obsMapTabable.get(id) ,true);
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
		if(changeListeners == null)
			changeListeners = new ArrayList<ChangeListenerHistory>();
		if(first)
			changeListeners.add(0,changeListener);
		else
			changeListeners.add(changeListener);
	}


}
