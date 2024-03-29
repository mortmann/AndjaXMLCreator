package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.FieldInfo;
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
	Object tabable;
	boolean ignoreChange = false;

	public EnumArraySetterHistory(String name, Field field, Object tabable, Class<E> class1) {
		ObservableList<Enum> names = FXCollections.observableArrayList();
		names.addAll(EnumSet.allOf(class1));
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
		} catch (Exception ignored) {
		}
		if(oldArray ==null){
			oldArray = new ArrayList<E>();
		} else {
			for(int i = 0; i<oldArray.size();i++){
				ignoreChange = true;
				OnEnumSelect(box,listpane,field,tabable,oldArray.get(i),i);
			}
		}
		if(field.getAnnotation(FieldInfo.class)!=null){
			if(field.getAnnotation(FieldInfo.class).required()){
			    ObservableList<String> styleClass = box.getStyleClass();
			    if(oldArray.isEmpty()) {
					styleClass.add("combobox-error");
				}
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {
					styleClass.remove("combobox-error");
				});

			}
		}
		add(new Label(name), 0, oldArray.size()+1);	
		add(box, 1, oldArray.size()+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnEnumSelect(box,listpane,field,tabable,(E)box.getSelectionModel().getSelectedItem(),-1);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		add(sp, 3, oldArray.size()+1);
	}
	/**
	 * 
	 * @param box
	 * @param listpane
	 * @param field
	 * @param tab
	 * @param e
	 * @param setupPos if not setup of this set it to smaller than 0 else give it the index in the array
	 */
	private void OnEnumSelect(ComboBoxHistory box,GridPane listpane, Field field, Object tab, E e, int setupPos) {
        ArrayList<E> old = null;
		try {
			old = (ArrayList<E>) field.get(tab); 
		} catch (Exception e1) {
			old = new ArrayList<>();
//			System.out.println(e1);
		}
		if(old != null && old.contains(e) && setupPos < 0){
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

		Label nameOfItem = new Label(e.toString());
		Button removeButton = new Button("X");
		
		try {
			ArrayList<E> tempList = new ArrayList<E>(old);
			if(setupPos<0){
				old.add(e);
			}
			OnChange(new ArrayList<E>(old), tempList);
			// set the press button action
			removeButton.setOnAction(s -> {
				try {
			        ArrayList<E> list = null;
					try {
						list = (ArrayList<E>) field.get(tab); 
					} catch (Exception e1) {
					}
					ArrayList<E> tempList2 = new ArrayList<E>(list);
					list.remove(e);
					//remove the label and button
					listpane.getChildren().removeAll(nameOfItem, removeButton);
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
		if(setupPos < 0) {
			listpane.add(nameOfItem, 0, old.size());
			listpane.add(removeButton, 2, old.size());
		} else {
			listpane.add(nameOfItem, 0, setupPos);
			listpane.add(removeButton, 2, setupPos);
		}
	}
	
	
	@Override
	public void Do(Object change) {
		for(E e : (E[]) change){
			ignoreChange = true;
			OnEnumSelect(box, listpane, field,tabable, e,-1);
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
			OnEnumSelect(box, listpane, field,tabable, e,-1);
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
