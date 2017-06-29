package com.mortmann.andja.creator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.Item.ItemType;
import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.structures.Growable;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.structures.Structure.BuildTypes;
import com.mortmann.andja.creator.structures.Structure.BuildingTyp;
import com.mortmann.andja.creator.structures.Structure.Direction;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.NumberTextField;
import com.mortmann.andja.creator.util.OrderEr;
import com.mortmann.andja.creator.util.Tabable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class WorkTab {
	ScrollPane scrollPaneContent;
	GridPane mainGrid;
	GridPane booleanGrid;
	GridPane stringGrid;
	GridPane floatGrid;
	GridPane intGrid;
	GridPane enumGrid;
	GridPane languageGrid;
	GridPane otherGrid;

	Tabable obj;
	
	public WorkTab(Tabable t){
        mainGrid = new GridPane();
        booleanGrid = new GridPane();
        floatGrid = new GridPane();
        intGrid = new GridPane();
        stringGrid = new GridPane();
        otherGrid = new GridPane();
        languageGrid = new GridPane();
        enumGrid = new GridPane();

        mainGrid.setGridLinesVisible(true);
        mainGrid.add(wrapPaneInTitledPane("Integer",intGrid), 0, 0);
        mainGrid.add(wrapPaneInTitledPane("Boolean",booleanGrid), 1, 0);
        mainGrid.add(wrapPaneInTitledPane("Float",floatGrid), 2, 0);
        mainGrid.add(wrapPaneInTitledPane("Enum",enumGrid), 0, 1);
        mainGrid.add(wrapPaneInTitledPane("String",stringGrid), 1, 1);
        mainGrid.add(wrapPaneInTitledPane("Other",otherGrid), 2, 1);
        mainGrid.add(wrapPaneInTitledPane("Language",languageGrid), 0, 2);
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(33);
        col.setHgrow(Priority.ALWAYS);
        mainGrid.getColumnConstraints().addAll(col,col,col);
//        booleanGrid.setPrefHeight(floatGrid.getHeight());
//        floatGrid.setPrefHeight(intGrid.getHeight());
//        intGrid.setPrefHeight(booleanGrid.getHeight());
//        stringGrid.setPrefHeight(stringGrid.getHeight());
//        otherGrid.setPrefHeight(otherGrid.getHeight());
//        languageGrid.setPrefHeight(enumGrid.getHeight());
//        enumGrid.setPrefHeight(Double.MAX_VALUE);

        
        scrollPaneContent = new ScrollPane();
        scrollPaneContent.setContent(mainGrid);
        scrollPaneContent.setMaxHeight(Double.MAX_VALUE);
        scrollPaneContent.setMaxWidth(Double.MAX_VALUE);
        
        ClassAction(t);
	}
	
	private TitledPane wrapPaneInTitledPane(String Name,Pane pane){
        TitledPane btp = new TitledPane(Name,pane);
        btp.setExpanded(true);
        btp.setCollapsible(false);
        btp.setMaxHeight(Double.MAX_VALUE);
        return btp;
	}
	@SuppressWarnings({ "rawtypes" })
	private void ClassAction(Tabable t){
        Class c = t.getClass();
		Field fld[] = c.getFields();
		obj = t;
		Arrays.sort(fld,new OrderEr());
        for (int i = 0; i < fld.length; i++) {
        	
            if(fld[i].getType() == Boolean.TYPE){
            	booleanGrid.add(CreateBooleanSetter(fld[i].getName(),fld[i],obj), 0, i);
            }
            else if(fld[i].getType() == Float.TYPE) {
            	floatGrid.add(CreateFloatSetter(fld[i].getName(),fld[i],obj), 0, i);
            }
            else if(fld[i].getType() == Integer.TYPE) {
            	intGrid.add(CreateIntSetter(fld[i].getName(),fld[i],obj), 0, i);
            }
            else if(fld[i].getType() ==  String.class) {
            	stringGrid.add(CreateStringSetter(fld[i].getName(),fld[i],obj), 0, i);
            }
            else if(fld[i].getType() ==  BuildTypes.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],obj,BuildTypes.class), 0, i);
            }
            else if(fld[i].getType() == BuildingTyp.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],obj,BuildingTyp.class), 0, i);
            }
            else if(fld[i].getType() == Direction.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],obj,Direction.class), 0, i);
            }
            else if(fld[i].getType() == Climate.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],obj,Climate.class), 0, i);
            }
            else if(fld[i].getType() == ItemType.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],obj,ItemType.class), 0, i);
            }
            else if(fld[i].getType() == ArrayList.class) { // we´re gonna take it as list of climate if not check here what type
            	enumGrid.add(CreateEnumArraySetter(fld[i].getName(),fld[i],obj,Climate.class), 0, i);
            }
            else if(fld[i].getType() == Item[].class) {
            	otherGrid.add(CreateItemArraySetter(fld[i].getName(),fld[i],obj), 0, i);
            }
            else if(fld[i].getType() == HashMap.class) { // we´re gonna take every HashMap as list of strings
            	languageGrid.add(CreateLanguageSetter(fld[i].getName(),fld[i],obj), 0, i);
            }
            else if(fld[i].getType() == Growable.class) { 
            	otherGrid.add(CreateStructureSetter(fld[i].getName(),fld[i],obj,Growable.class), 0, i);
            }
            else {
                System.out.println("Variable Name is : " + fld[i].getName() +" : " + fld[i].getType() );

            }
        }         
       
        
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private<E extends Enum<E>> Node CreateEnumArraySetter(String name, Field field, Tabable tab, Class<E> class1) {
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (E e : EnumSet.allOf(class1)) {
			  names.add(e);
		}
		GridPane grid = new GridPane();
		GridPane listpane = new  GridPane();
        ColumnConstraints gridcol1 = new ColumnConstraints();
        gridcol1.setMinWidth(75);
        ColumnConstraints gridcol2 = new ColumnConstraints();
        gridcol2.setMinWidth(25);
        grid.getColumnConstraints().addAll(gridcol1,gridcol2);
        
        ColumnConstraints listcol1 = new ColumnConstraints();
        listcol1.setMinWidth(50);
        ColumnConstraints listcol2 = new ColumnConstraints();
        listcol2.setMinWidth(25);
        ColumnConstraints listcol3 = new ColumnConstraints();
        listcol3.setMinWidth(15);
        listpane.getColumnConstraints().addAll(listcol1,listcol2,listcol3);
        ArrayList<E> oldArray = null;
		
		try {
			oldArray =(ArrayList<E>) field.get(tab);
			System.out.println(oldArray.size());
		} catch (Exception e1) {
		}
		if(oldArray ==null){
			oldArray = new ArrayList<E>();
		} else {
			for(int i = 0; i<oldArray.size();i++){
				OnEnumSelect(listpane,field,tab,oldArray.get(i),true);
			}
		}
		
		ComboBox<Enum> box = new ComboBox<Enum>(names);
		grid.add(new Label(name), 0, oldArray.size()+1);	
		grid.add(box, 1, oldArray.size()+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnEnumSelect(listpane,field,tab,box.getSelectionModel().getSelectedItem(),false);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		grid.add(sp, 3, oldArray.size()+1);
		
		return grid;

	}

	@SuppressWarnings({ "unchecked" })
	private<E extends Enum<E>> void OnEnumSelect(GridPane listpane, Field field, Tabable tab, E e, boolean setup) {
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
			if(setup==false){
				old.add(e);
			}
			// set the press button action
			b.setOnAction(s -> {
				try {
			        ArrayList<E> list = null;
					try {
						list = (ArrayList<E>) field.get(tab); 
					} catch (Exception e1) {
					}
					list.remove(e);
					//remove the label and button
					listpane.getChildren().removeAll(l, b);
					ObservableList<Node> children = FXCollections.observableArrayList(listpane.getChildren());
					listpane.getChildren().clear();
					for (int i = 0; i < children.size(); i+=2) {
						listpane.add(children.get(i), 0, i);
						listpane.add(children.get(i+1), 1, i);
					}
					System.out.println("set");
					field.set(tab, list);
				} catch (Exception e1) {
					System.out.println(e1);
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		listpane.add(l, 0, old.size());
		listpane.add(b, 2, old.size());
	}

	@SuppressWarnings("rawtypes")
	private Node CreateStructureSetter(String name, Field field, Tabable m, Class str) {
		GridPane grid = new  GridPane();
		ObservableList<Structure> strs = FXCollections.observableArrayList(GUI.Instance.getStructureList());
		strs.removeIf(x->x.getClass().equals(str.getClass()));
		ComboBox<Structure> box = new ComboBox<Structure>(strs);
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(75);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(165);
        
        grid.getColumnConstraints().addAll(col1,col2);
		try {
			if(field.get(m)!=null){
				box.getSelectionModel().select((Structure) field.get(m));
			}
		} catch (Exception e1) {
		} 
		
		box.setOnAction(x-> {
				try {
					field.set(m, box.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);

		
		return grid;
	}

	@SuppressWarnings("unchecked")
	private Node CreateLanguageSetter(String name, Field field, Tabable str) {
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(50);
        grid.getColumnConstraints().addAll(col1,col2);
		
		ArrayList<Language> langes = new ArrayList<>();
		for (Language e : EnumSet.allOf(Language.class)) {
			langes.add(e);
		}
		grid.add(new Label(field.getName()), 0, 0);
		
		for (int i = 0; i < langes.size(); i++) {
			grid.add(new Label(langes.get(i).toString()), 0, i+1);
			TextField t = new TextField();
			CheckIfRequired(t, field);
			int num = i;
			
			try {
				
				HashMap<String, String> h = (HashMap<String, String>) field.get(str);
				if(h != null){
					t.setText(h.get(langes.get(i).toString()));
				}
			} catch (Exception e1) {

			} 
			
			t.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					try {
						HashMap<String,String> h = (HashMap<String, String>) field.get(str);
						if(h == null){
							h = new HashMap<>();
						}
						h.put(langes.get(num).toString(), t.getText());
						field.set(str,h);	
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			grid.add(t, 1, i+1);
			
		}

		
		return grid;
	}

	private Node CreateItemArraySetter(String name, Field field, Tabable m) {
		ObservableList<Item> its = FXCollections.observableArrayList();
		its.addAll(GUI.Instance.getItems());
		GridPane grid = new GridPane();
		GridPane listpane = new  GridPane();
        ColumnConstraints gridcol1 = new ColumnConstraints();
        gridcol1.setMinWidth(75);
        ColumnConstraints gridcol2 = new ColumnConstraints();
        gridcol2.setMinWidth(25);
        grid.getColumnConstraints().addAll(gridcol1,gridcol2);
        
        ColumnConstraints listcol1 = new ColumnConstraints();
        listcol1.setMinWidth(50);
        ColumnConstraints listcol2 = new ColumnConstraints();
        listcol2.setMinWidth(25);
        ColumnConstraints listcol3 = new ColumnConstraints();
        listcol3.setMinWidth(15);
        listpane.getColumnConstraints().addAll(listcol1,listcol2,listcol3);
		Item[] oldArray = null;
		
		try {
			oldArray = (Item[]) field.get(m);
		} catch (Exception e1) {
		}
		if(oldArray ==null){
			oldArray = new Item[1];
		} else {
			for (Item item : oldArray) {
				OnItemSelect(listpane,field,m,item,true);
			}
		}
		
		ComboBox<Item> box = new ComboBox<Item>(its);
		grid.add(new Label(name), 0, oldArray.length+1);	
		grid.add(box, 1, oldArray.length+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnItemSelect(listpane,field,m,box.getSelectionModel().getSelectedItem(),false);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		grid.add(sp, 3, oldArray.length+1);
		
		return grid;
	}
	private void OnItemSelect(GridPane listpane, Field field, Tabable m, Item select,boolean setup){
		//get existing field if null or not
		Item[] old = null;
		try {
			old = (Item[]) field.get(m); 
		} catch (Exception e1) {
//			System.out.println(e1);
		}
		if(old != null && setup== false){
			for (Item item : old) {
				if(item.ID == select.ID){
					return;
				}
			}
		}
		//if null we start at pos 1 else insert at length+1
		int pos = old == null? 1 : old.length+1;
		// Name of Item
		Label l = new Label(select.toString());
		//Amount field
		NumberTextField count = new NumberTextField(3);
		CheckIfRequired(count, field);

		count.setMaxWidth(35);
		count.setText(select.count +"");
		count.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					select.count = count.GetIntValue();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// Remove Button
		Button b = new Button("X");
		
		try {
			// Create newArray in Case old was null
			Item[] newArray = new Item[1];
			if(old != null){
				//else create a array one bigger than old
				newArray = new Item[old.length + 1];
				//copy over variables
				System.arraycopy(old,0,newArray,0,old.length);
			}
			//set the new place in array to selected variable 
			newArray[pos-1] = select;
			field.set(m, newArray);
			// set the press button action
			b.setOnAction(s -> {
				try {
					//get array
					Item[] array = (Item[]) field.get(m);
					//remove the label and button
					listpane.getChildren().removeAll(l, b, count);
					ObservableList<Node> children = FXCollections.observableArrayList(listpane.getChildren());
					listpane.getChildren().clear();
					for (int i = 0; i < children.size(); i+=3) {
						listpane.add(children.get(i), 0, i);
						listpane.add(children.get(i+1), 1, i);
						listpane.add(children.get(i+2), 2, i);
					}
					//remove this value and set the array in class
					field.set(m, removeItemElement(array, pos-1));
				} catch (Exception e1) {
					System.out.println(e1);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		listpane.add(l, 0, pos);
		listpane.add(count, 1, pos);
		listpane.add(b, 2, pos);
	}

	public GridPane CreateBooleanSetter(String name, Field field, Tabable m){
		GridPane grid = new  GridPane();
		CheckBox box = new CheckBox(name);
		try {
			box.setSelected((boolean) field.get(m));
		} catch (Exception e1) {
		}
		box.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	                Boolean old_val, Boolean new_val) {
	        	try {
					field.setBoolean(m, box.isSelected());
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        });
		grid.add(box, 0, 0);	
		
		return grid;
	}
	public GridPane CreateFloatSetter(String name, Field field, Tabable str){
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);
		
		NumberTextField box = new NumberTextField(true);
		CheckIfRequired(box, field);

		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		try {
			if(field.get(str)!=null){
				box.setText((Float) field.get(str) +"");
			}
		} catch (Exception e1) {
		} 
		
		
		box.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					field.setFloat(str, box.GetFloatValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return grid;
	}
	public GridPane CreateIntSetter(String name, Field field, Tabable str){
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);
		
		NumberTextField box = new NumberTextField();
		CheckIfRequired(box, field);

		try {
			if(field.get(str)!=null){
				box.setText((Integer) field.get(str)+"");
			}
		} catch (Exception e1) {
		} 
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		box.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					field.setInt(str, box.GetIntValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return grid;
	}

	public GridPane CreateStringSetter(String name, Field field, Tabable str){
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);
		
		TextField box = new TextField();
		CheckIfRequired(box, field);

		try {
			if(field.get(str)!=null){
				box.setText((String) field.get(str));
			}
		} catch (Exception e1) {
		} 
		box.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					field.set(str, box.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		
		return grid;
	}
	
	@SuppressWarnings("rawtypes")
	public<E extends Enum<E>> GridPane CreateEnumSetter(String name, Field field, Tabable m, Class<E>  class1){
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(class1)) {
			  names.add(e);
		}
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(100);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1,col2);
		ComboBox<Enum> box = new ComboBox<Enum>(names);
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		
		try {
			if(field.get(m)!=null){
				box.getSelectionModel().select((Enum) field.get(m));
			}
		} catch (Exception e1) {
		} 
		
		box.setOnAction(x-> {
				try {
					field.set(m, box.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		
		return grid;
	}
	
	public Item[] removeItemElement(Item[] a, int del) {
		if(a.length-1==0){
			return new Item[0];
		}
		Item[] newA = new Item[a.length-1];
//	    System.arraycopy(newA,0,a,del,a.length-1-del);
		int newI = 0;// new array pos
	    for (int i = 0; i < a.length; i++) { // increase old array pos
	    	if(i==del){ // its the one to copy -> skip
	    		continue;
	    	}
			newA[newI] = a[i]; // just copy 
	    	newI++; // increase new array pos
		}
	    return newA;
	}
	/*
	 * if(a.length-1==0){
			return new Item[0];
		}
		Item[] newA = new Item[a.length-1];
		int newI = 0;
	    for (int i = 0; i < a.length; i++) {
	    	System.out.println(a[i]);
	    	if(i==del){
	    		i++;
	    		continue;
	    	}
			newA[newI] = a[i];
	    	newI++;
		}
	    return newA;
	 */
	
	private void CheckIfRequired(TextField text,Field field){
        FieldInfo info = field.getAnnotation(FieldInfo.class);
        if(info == null || info.required() == false){
        	return;
        }
	    ObservableList<String> styleClass = text.getStyleClass();
	    
	    styleClass.add("text-field-error");
		text.textProperty().addListener((arg0, oldValue, newValue) -> {		
			System.out.println(text.getText());
			
			if(text.getText().isEmpty()){
	    	    if(!styleClass.contains("text-field-error")) {
	    	        styleClass.add("text-field-error");
	    	    }
	        } else
			if(text instanceof NumberTextField){
				if(((NumberTextField) text).GetIntValue()>0){
					if(styleClass.contains("text-field-error")) {
		    	        styleClass.remove("text-field-error");
		    	    }
				}
			} else {
	        	if(styleClass.contains("text-field-error")) {
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

	public Object getObject() {
		return obj;
	}

}
