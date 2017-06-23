package com.mortmann.andja.creator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.structures.Structure.BuildTypes;
import com.mortmann.andja.creator.structures.Structure.BuildingTyp;
import com.mortmann.andja.creator.structures.Structure.Direction;
import com.mortmann.andja.creator.util.NumberTextField;
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

public class MyTab {
	ScrollPane scrollPaneContent;
	GridPane mainGrid;
	GridPane booleanGrid;
	GridPane stringGrid;
	GridPane floatGrid;
	GridPane intGrid;
	GridPane enumGrid;
	GridPane languageGrid;
	GridPane otherGrid;

	Object obj;
	
	public MyTab(@SuppressWarnings("rawtypes") Class c){
        mainGrid = new GridPane();
        booleanGrid = new GridPane();
        floatGrid = new GridPane();
        intGrid = new GridPane();
        stringGrid = new GridPane();
        otherGrid = new GridPane();
        languageGrid = new GridPane();
        enumGrid = new GridPane();

        mainGrid.setGridLinesVisible(true);
        mainGrid.add(wrapPaneInTitledPane("Boolean",booleanGrid), 0, 0);
        mainGrid.add(wrapPaneInTitledPane("Integer",intGrid), 1, 0);
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
        
        ClassAction(c);
	}
	
	private TitledPane wrapPaneInTitledPane(String Name,Pane pane){
        TitledPane btp = new TitledPane(Name,pane);
        btp.setExpanded(true);
        btp.setCollapsible(false);
        btp.setMaxHeight(Double.MAX_VALUE);
        return btp;
	}
	@SuppressWarnings("unchecked")
	private void ClassAction(@SuppressWarnings("rawtypes") Class c){
        
		Field fld[] = c.getFields();
		Tabable m = null;
		try {
			m = (Tabable) c.getConstructor().newInstance();
			obj = m;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if(m == null){
			return; //somehow it got passed a non structure
		}
        for (int i = 0; i < fld.length; i++) {
        	
            if(fld[i].getType() == Boolean.TYPE){
            	booleanGrid.add(CreateBooleanSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() == Float.TYPE) {
            	floatGrid.add(CreateFloatSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() == Integer.TYPE) {
            	intGrid.add(CreateIntSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() ==  String.class) {
            	stringGrid.add(CreateStringSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() ==  BuildTypes.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,BuildTypes.class), 0, i);
            }
            else if(fld[i].getType() == BuildingTyp.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,BuildingTyp.class), 0, i);
            }
            else if(fld[i].getType() == Direction.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,Direction.class), 0, i);
            }
            else if(fld[i].getType() == Climate.class) {
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,Climate.class), 0, i);
            }
            else if(fld[i].getType() == Item[].class) {
            	otherGrid.add(CreateItemArraySetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() == HashMap.class) { // we´re gonna take every HashMap as list of strings
            	languageGrid.add(CreateLanguageSetter(fld[i].getName(),fld[i],m), 0, i);
            } else {
                System.out.println("Variable Name is : " + fld[i].getName() +" : " + fld[i].getType() );

            }
        }         
       
        
	}
	private Node CreateLanguageSetter(String name, Field field, Tabable m) {
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
			int num = i;
			t.setOnAction(x-> {
				try {
					@SuppressWarnings("unchecked")
					HashMap<String,String> h = (HashMap<String, String>) field.get(m);
//					field.set(str, );
					h.put(langes.get(num).toString(), t.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }
		    );
			grid.add(t, 1, i+1);

		}

		
		return grid;
	}

	private Node CreateItemArraySetter(String name, Field field, Tabable m) {
		ObservableList<ItemXML> its = FXCollections.observableArrayList();
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
		}
		
		ComboBox<ItemXML> box = new ComboBox<ItemXML>(its);
		grid.add(new Label(name), 0, oldArray.length+1);	
		grid.add(box, 1, oldArray.length+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			//get existing field if null or not
			Item[] old = null;
			try {
				old = (Item[]) field.get(m); 
			} catch (Exception e1) {
//				System.out.println(e1);
			}
			if(old != null){
				for (Item item : old) {
					if(item.ID == box.getValue().ID){
						return;
					}
				}
			}
			Item select = box.getValue();
			//if null we start at pos 1 else insert at length+1
			int pos = old == null? 1 : old.length+1;
			// Name of Item
			Label l = new Label(box.getValue().toString());
			//Amount field
			NumberTextField count = new NumberTextField(3);
			count.setMaxWidth(35);
			count.setOnAction(p->{
				select.count = count.GetIntValue();
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
				newArray[pos-1] = box.getValue();
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

		);
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		grid.add(sp, 3, oldArray.length+1);
		
		return grid;
	}

	public GridPane CreateBooleanSetter(String name, Field field, Tabable m){
		GridPane grid = new  GridPane();
		CheckBox box = new CheckBox(name);
		
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
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		box.setOnAction(x-> {
				try {
					field.setFloat(str, box.GetFloatValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
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
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		box.setOnAction(x-> {
			System.out.println(box.GetIntValue());
			try {
				field.setInt(str, box.GetIntValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	);
		return grid;
	}

	public GridPane CreateStringSetter(String name, Field field, Tabable m){
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);
		
		TextField box = new TextField();
		box.setOnInputMethodTextChanged(x-> {
				try {
					System.out.println(box.getText());
					field.set(m, box.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}	
		});
		box.setOnAction(x-> {
			try {
				System.out.println(box.getText());
				field.set(m, box.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		try {
			field.set(m, "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		box.setOnAction(x-> {
				try {
					field.set(m, box.getValue());
					System.out.println(field.get(m));
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
