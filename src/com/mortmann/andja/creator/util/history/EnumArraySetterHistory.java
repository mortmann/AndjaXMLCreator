package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

@SuppressWarnings({"rawtypes","unchecked"})
public class EnumArraySetterHistory<E extends Enum<E>> extends GridPane implements Changeable {

	private GridPane listpane;
	private ComboBoxHistory<Enum> box;
	Field field;
	Tabable tabable;
	boolean ignoreChange = false;

	public EnumArraySetterHistory(String name, Field field, Tabable tabable, Class<E> class1) {
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (E e : EnumSet.allOf(class1)) {
			  names.add(e);
		}
		this.field = field;
		this.tabable = tabable;
		listpane = new  GridPane();
        ColumnConstraints gridcol1 = new ColumnConstraints();
        gridcol1.setMinWidth(75);
        ColumnConstraints gridcol2 = new ColumnConstraints();
        gridcol2.setMinWidth(25);

        getColumnConstraints().addAll(gridcol1,gridcol2);
        
        ColumnConstraints listcol1 = new ColumnConstraints();
        listcol1.setMinWidth(50);
        ColumnConstraints listcol2 = new ColumnConstraints();
        listcol2.setMinWidth(25);
        ColumnConstraints listcol3 = new ColumnConstraints();
        listcol3.setMinWidth(15);
        listpane.getColumnConstraints().addAll(listcol1,listcol2,listcol3);
        ArrayList<E> oldArray = null;
		box = new ComboBoxHistory<Enum>(names);

		try {
			oldArray = (ArrayList<E>) field.get(tabable);
		} catch (Exception e1) {
		}
		if(oldArray ==null){
			oldArray = new ArrayList<E>();
		} else {
			for(int i = 0; i<oldArray.size();i++){
				ignoreChange = true;
				OnEnumSelect(box,listpane,field,tabable,oldArray.get(i),true);
			}
		}
		if(field.getAnnotation(FieldInfo.class)!=null){
			if(field.getAnnotation(FieldInfo.class).required()){
			    ObservableList<String> styleClass = box.getStyleClass();
			    
			    styleClass.add("combobox-error");
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {		
		        	if(styleClass.contains("combobox-error")) {
		        		styleClass.remove("combobox-error");
	    	    	}
				});

			}
		}
		add(new Label(name), 0, oldArray.size()+1);	
		add(box, 1, oldArray.size()+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnEnumSelect(box,listpane,field,tabable,(E)box.getSelectionModel().getSelectedItem(),false);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		add(sp, 3, oldArray.size()+1);
	}

	private void OnEnumSelect(ComboBoxHistory box,GridPane listpane, Field field, Tabable tab, E e, boolean setup) {
        ArrayList<E> old = null;
		try {
			old = (ArrayList<E>) field.get(tab); 
		} catch (Exception e1) {
			old = new ArrayList<>();
//			System.out.println(e1);
		}
		if(old != null && old.contains(e) && setup== false){
			return;
		}
		if(old == null){
			old = new ArrayList<>();
			try {
				field.set(tab, old);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		}

		// Name of Item
		Label l = new Label(e.toString());
		//Amount field
		// Remove Button
		Button b = new Button("X");
		
		try {
			ArrayList<E> tempList = new ArrayList<E>(old);
			if(setup==false){
				old.add(e);
			}
			OnChange(new ArrayList<E>(old), tempList);
			// set the press button action
			b.setOnAction(s -> {
				try {
			        ArrayList<E> list = null;
					try {
						list = (ArrayList<E>) field.get(tab); 
					} catch (Exception e1) {
					}
					ArrayList<E> tempList2 = new ArrayList<E>(list);
					list.remove(e);
					//remove the label and button
					listpane.getChildren().removeAll(l, b);
					ObservableList<Node> children = FXCollections.observableArrayList(listpane.getChildren());
					listpane.getChildren().clear();
					for (int i = 0; i < children.size(); i+=2) {
						listpane.add(children.get(i), 0, i);
						listpane.add(children.get(i+1), 1, i);
					}
					if(list.size()==0){
						FieldInfo fi = field.getAnnotation(FieldInfo.class);
						if(fi!=null&&fi.required()&&box.getStyle().contains("combobox-error")==false){
							box.getStyleClass().add("combobox-error");
						}
					}
					field.set(tab, list);
					OnChange(new ArrayList<E>(list), tempList2);
				} catch (Exception e1) {
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		listpane.add(l, 0, old.size());
		listpane.add(b, 2, old.size());
	}
	
	
	@Override
	public void Do(Object change) {
		for(E e : (E[]) change){
			ignoreChange = true;
			OnEnumSelect(box, listpane, field,tabable, e,true);
		}
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}

	@Override
	public void Undo(Object change) {
		for(E e : (E[]) change){
			ignoreChange = true;
			OnEnumSelect(box, listpane, field,tabable, e,true);
		}		
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
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

}
