package com.mortmann.andja.creator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.other.GameEvent.Target;
import com.mortmann.andja.creator.other.Need.People;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.structures.Structure.TileType;
import com.mortmann.andja.creator.unitthings.*;
import com.mortmann.andja.creator.util.*;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
	
	private GridPane UpdateTileTypeArrayPane;

	Tabable myTabable;
	private boolean newTabable;
	
	public WorkTab(Tabable t, boolean newTabable){
        mainGrid = new GridPane();
        booleanGrid = new GridPane();
        floatGrid = new GridPane();
        intGrid = new GridPane();
        stringGrid = new GridPane();
        otherGrid = new GridPane();
        languageGrid = new GridPane();
        enumGrid = new GridPane();
        this.newTabable=newTabable;
        mainGrid.setGridLinesVisible(true);
        mainGrid.add(wrapPaneInTitledPane("Integer",intGrid), 0, 0);
        mainGrid.add(wrapPaneInTitledPane("Boolean",booleanGrid), 1, 0);
        mainGrid.add(wrapPaneInTitledPane("Float",floatGrid), 2, 0);
        mainGrid.add(wrapPaneInTitledPane("Enum",enumGrid), 0, 2);
        TitledPane string = wrapPaneInTitledPane("String",stringGrid);
        mainGrid.add(string,  0, 1);
        TitledPane other = wrapPaneInTitledPane("Other",otherGrid);
        GridPane.setColumnSpan(other, 2);
        mainGrid.add(other, 1, 1);
        TitledPane language = wrapPaneInTitledPane("Language",languageGrid);
        mainGrid.add(language, 1, 2);
        GridPane.setColumnSpan(language, 2);

        ColumnConstraints col = new ColumnConstraints();
        ColumnConstraints mid = new ColumnConstraints();

        col.setPercentWidth(40);
        mid.setPercentWidth(30);
        col.setHgrow(Priority.ALWAYS);
        mainGrid.getColumnConstraints().addAll(col,mid,col);
        
        scrollPaneContent = new ScrollPane();
        mainGrid.setMaxWidth(1225);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void ClassAction(Tabable t){               

        Class c = t.getClass();
		Field fld[] = c.getFields();
		myTabable = t;
		Arrays.sort(fld,new OrderEr());
        for (int i = 0; i < fld.length; i++) {
            FieldInfo info = fld[i].getAnnotation(FieldInfo.class);
            if(info!=null && info.ignore())
            	continue;
        	Class compare = fld[i].getType();
            if(info!=null&&info.compareType().equals(void.class)==false){
        		compare = info.compareType();
        	}
            if(compare == Boolean.TYPE){
            	booleanGrid.add(CreateBooleanSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare == Float.TYPE) {
            	floatGrid.add(CreateFloatSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare == Integer.TYPE) {
            	if(fld[i].getAnnotation(FieldInfo.class)!=null){
            		FieldInfo fi = fld[i].getAnnotation(FieldInfo.class);
                	if(fi.subType() == People.class){
                		intGrid.add(CreateEnumIntSetter(fld[i].getName(),fld[i],myTabable,People.class), 0, i);
                		continue;
                	}
            	}
            	intGrid.add(CreateIntSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare ==  String.class) {
            	if(info!=null&&info.longtext()){
                	stringGrid.add(CreateLongStringSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            	} else {
                	stringGrid.add(CreateStringSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            	}
            }
            else if(compare.isEnum()) {
            	//This is for all enums makes it way easier in the future to create new ones and removes need to add smth here
            	enumGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],myTabable,compare), 0, i);
            }
            else if(compare == ArrayList.class) {
            	if(fld[i].getAnnotation(FieldInfo.class)==null){
            		System.out.println("[ERROR] This type "+ fld[i].getName() +" of field needs a fieldinfo-subtype declared!");
            		continue;
            	}
            	FieldInfo fi = fld[i].getAnnotation(FieldInfo.class);
            	if(fi.subType()==void.class){
            		System.out.println("[ERROR] This type "+ fld[i].getName() +" of field needs a subtype declared!");
            		continue;
            	}
            	if(fi.subType() == Climate.class){
            		enumGrid.add(CreateEnumArraySetter(fld[i].getName(),fld[i],myTabable,Climate.class), 0, i);
            	}
            	if(fi.subType() == Target.class){
            		enumGrid.add(CreateEnumArraySetter(fld[i].getName(),fld[i],myTabable,Target.class), 0, i);
            	}
        	}
            else if(compare == Item[].class) {
            	otherGrid.add(CreateItemArraySetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare == Item.class) {
            	otherGrid.add(CreateItemSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare == HashMap.class) { 
            	if(fld[i].getAnnotation(FieldInfo.class)==null){
            		System.out.println("[ERROR] This type "+ fld[i].getName() +" of field needs a fieldinfo-subtype declared!");
            		continue;
            	}
            	FieldInfo fi = fld[i].getAnnotation(FieldInfo.class);
            	if(fi.subType()==void.class){
            		System.out.println("[ERROR] This type "+ fld[i].getName() +" of field needs a subtype declared!");
            		continue;
            	}
            	if(fi.subType()==DamageType.class){
            		otherGrid.add(CreateClassToFloatSetter(fld[i].getName(),fld[i],myTabable,GUI.Instance.idToDamageType), 0, i);
            		continue;
            	} 
            	if(fi.subType()==ArmorType.class){
            		otherGrid.add(CreateClassToFloatSetter(fld[i].getName(),fld[i],myTabable,GUI.Instance.idToArmorType), 0, i);
            		continue;
            	} 
            	if(fi.subType()==PopulationLevel.class){
            		otherGrid.add(CreateClassToFloatSetter(fld[i].getName(),fld[i],myTabable,GUI.Instance.idToPopulationLevel), 0, i);
            		continue;
            	}
            	if(fi.subType()==String.class){
                	languageGrid.add(CreateLanguageSetter(fld[i].getName(),fld[i],myTabable), 0, i);
                	continue;
            	} 
            	if(fi.mainType() == Target.class && fi.subType()==Integer.class){		
            		otherGrid.add(CreateEffectableToTabableSetter(fld[i].getName(), fld[i], myTabable), 0, i);
                	continue;
            	} 
            }
            else if(compare == Fertility.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,Fertility.class,GUI.Instance.idToFertility), 0, i);
            }
            else if(compare == NeedGroup.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,NeedGroup.class,GUI.Instance.idToNeedGroup), 0, i);
            }
            else if(compare == PopulationLevel.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,PopulationLevel.class,GUI.Instance.idToPopulationLevel), 0, i);
            }
            else if(compare == Growable.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,Growable.class,GUI.Instance.idToStructures), 0, i);
            }
            else if(compare == NeedStructure.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,NeedStructure.class,GUI.Instance.idToStructures), 0, i);
            }
            else if(compare == DamageType.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,DamageType.class,GUI.Instance.idToDamageType), 0, i);
            }
            else if(compare == ArmorType.class) { 
            	otherGrid.add(CreateTabableSetter(fld[i].getName(),fld[i],myTabable,ArmorType.class,GUI.Instance.idToArmorType), 0, i);
            }
            else if(compare == float[].class){
            	otherGrid.add(CreateFloatArraySetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare == Unit[].class){                
            	otherGrid.add(CreateTabableArraySetter(fld[i].getName(),fld[i],myTabable, Unit[].class, GUI.Instance.idToUnit), 0, i);
            }
            else if(compare == Effect[].class){                
            	otherGrid.add(CreateTabableArraySetter(fld[i].getName(),fld[i],myTabable, Effect[].class, GUI.Instance.idToEffect), 0, i);
            }
            else if(compare == NeedStructure[].class){                
            	otherGrid.add(CreateTabableArraySetter(fld[i].getName(),fld[i],myTabable, NeedStructure[].class, GUI.Instance.idToStructures), 0, i);
            }
            else if(compare == Structure[].class){                
            	otherGrid.add(CreateTabableArraySetter(fld[i].getName(),fld[i],myTabable, Structure[].class, GUI.Instance.idToStructures), 0, i);
            }
            else if(compare == Tabable.class && info.RequiresEffectable()) {
            	otherGrid.add(CreateEffectableSetter(fld[i].getName(),fld[i],myTabable), 0, i);
            }
            else if(compare == TileType[][].class) {
            	otherGrid.add(CreateEnumTwoDimensionalArraySetter(fld[i].getName(), fld[i], myTabable, TileType.class), 0, i);
            }
            else {
                System.out.println("Variable Name is: " + fld[i].getName() +" : " + compare );
            }
        }         
       
        
	}
	@SuppressWarnings({ "unchecked" })
	private Node CreateEffectableToTabableSetter(String name, Field field, Tabable tab) {
		GridPane grid = new  GridPane();
		ComboBox<Target> box = new ComboBox<Target>(GameEvent.specialTargetRangeClasses);
		
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);
			
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(75);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(165);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(165);
        grid.getColumnConstraints().addAll(col1,col2,col3);
		ComboBox<Tabable> tabableBox = new ComboBox<Tabable>();
		grid.add(tabableBox, 2, 0);

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
		
		VBox listbox = new  VBox();
        grid.add(listbox, 1, 1);
        GridPane.setColumnSpan(listbox, 2);

		try {		
			HashMap<Target, String[]> map = field.get(tab)!=null ? (HashMap<Target, String[]>) field.get(tab) : new HashMap<>();
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
//						if(map.containsKey(box.getValue()) == false) {
//							map.put(target, new Integer[1]);
//						}
						temp.add(tabableBox.getValue().GetID());
						String[] t = new String[1];
 						map.put(target, temp.toArray(t));
						field.set(tab, map);
						listbox.getChildren()
							.add(CreateHBoxEffectableTabable(select.GetID(),select.GetName(),target,listbox,map));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			);
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		return grid;
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
				map.put(target, temp.toArray(t));
				if(temp.isEmpty())
					map.remove(target);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		return hbox;
	}

	@SuppressWarnings({ "rawtypes"})
	private Node CreateEffectableSetter(String name, Field field, Tabable tab) {
		GridPane grid = new  GridPane();
		HashMap<String, Class> nameToClass = new HashMap<>();
		ObservableList<String> names = FXCollections.observableArrayList();
		for(Class c : GameEvent.targetClasses) {
			names.add(c.getSimpleName());
			nameToClass.put(c.getSimpleName(), c);
		}
		ComboBox<String> box = new ComboBox<String>(names);
		
		if(field.getAnnotation(FieldInfo.class)!=null){
			if(field.getAnnotation(FieldInfo.class).required()){
			    ObservableList<String> styleClass = box.getStyleClass();
			    styleClass.add("combobox-error");
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {		
					Object o = null;
					try {
						o =  (field.get(tab));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(o == null){
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
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);
			
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(75);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(165);
        
        grid.getColumnConstraints().addAll(col1,col2);
		
		ComboBox<String> variablebox = new ComboBox<String>();
		box.setOnAction(x-> {
			ObservableList<String> varnames = FXCollections.observableArrayList();
			for(Field f : nameToClass.get(box.getValue()).getFields()) {
	            FieldInfo info = f.getAnnotation(FieldInfo.class);
	            if(info == null)
	            	continue;
	            if(info.IsEffectable() == false)
	            	continue;
	            varnames.add(f.getName());
			}
			variablebox.setItems(varnames);
				try {
					if(box.getValue()==null){
						return;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		variablebox.setOnAction(x-> {
				try {
					if(variablebox.getValue()==null){
						return;
					}
					field.set(tab, variablebox.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		
		try {
			if(field.get(tab)!=null){
				for(Class c : GameEvent.targetClasses) {
					for(Field f : c.getFields()) {
						if(f.getName().equals((String)field.get(tab))) {
							box.getSelectionModel().select(c.getSimpleName());
							variablebox.getSelectionModel().select((String)field.get(tab));
						}
					}
				}
			}
		} catch (Exception e1) {
		} 
		
		
		grid.add(variablebox, 1, 1);
		return grid;
	}

	@SuppressWarnings("rawtypes") 
	private<T extends Tabable> Node CreateTabableArraySetter(String name, Field field, Tabable m, Class tabableClass, ObservableMap<String, T> obsMapTabable) {
		ObservableList<Tabable> tabables = FXCollections.observableArrayList();
		tabables.addAll(obsMapTabable.values());

		Class singleItemClass = tabableClass.getComponentType();

		if(singleItemClass != null && Structure.class.isAssignableFrom(singleItemClass)) {
			//if its a structure we need only THAT type of structure in the list so we need to filter the rest out.
			tabables.removeIf(p-> p.getClass() != singleItemClass);
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
        listcol2.setMinWidth(50);
        
        listpane.getColumnConstraints().addAll(listcol1,listcol2);
        String[] oldArray = null;
		
		try {
			oldArray = (String[]) field.get(m);
		} catch (Exception e1) {
		}
		if(oldArray ==null){
			oldArray = new String[1];
			oldArray[0] = "";
		} else {
			for (String id : oldArray) {
				OnArrayClassSelect(listpane,field,m, obsMapTabable.get(id) ,true);
			}
		}
		
		ComboBox<Tabable> box = new ComboBox<Tabable>(tabables);
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
					int[] i = null;
					try {
						i = (int[]) field.get(m);
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
		grid.add(new Label(name), 0, oldArray.length+1);	
		grid.add(box, 1, oldArray.length+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnArrayClassSelect(listpane,field,m,box.getSelectionModel().getSelectedItem(),false);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		grid.add(sp, 3, oldArray.length+1);
		return grid;
	}

	@SuppressWarnings("rawtypes")
	public<E extends Enum<E>> GridPane CreateEnumIntSetter(String name, Field field, Tabable m, Class<E>  class1){
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
				box.getSelectionModel().select((int) field.get(m));
			}
		} catch (Exception e1) {
		} 
		
		box.setOnAction(x-> {
				try {
					field.set(m, box.getValue().ordinal() );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		
		return grid;
	}

	private Node CreateItemSetter(String name, Field field, Tabable tab) {
		GridPane grid = new GridPane();
		ObservableList<Item> its = FXCollections.observableArrayList();
		its.addAll(GUI.Instance.getItems());
		GUI.Instance.idToItem.addListener(new MapChangeListener<String,ItemXML>(){
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends ItemXML> change) {
				if(change.getValueAdded()==null){
					return;
				}
				Item i = new Item(change.getValueAdded());
				its.add(i);
			}
		});
		ComboBox<Item> box = new ComboBox<Item>(its);
		try {
			if(field.get(tab) != null){
				//if error i changed from GUI.Instance.idToItem.get(field.get(tab)) to just field.get(tab)
				box.getSelectionModel().select((Item) field.get(tab));
			}
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		box.setOnAction(x->{
			try {
				if(field.isAnnotationPresent(FieldInfo.class)&&field.getAnnotation(FieldInfo.class).compareType()==Item.class){
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
        col2.setMinWidth(165);
        
        grid.getColumnConstraints().addAll(col1,col2);
		grid.add(new Label(field.getName()), 0, 0);
		grid.add(box, 1, 0);
		Button b = new Button("X");
		b.setOnAction(x->{
			box.getSelectionModel().clearSelection();
		});
		grid.add(b, 2, 0);	
		return grid;
	}

	///NEEDS FIELDINFO ARRAYPOS
	private Node CreateFloatArraySetter(String name, Field field, Tabable tab) {
		System.out.println("CreateFloatArraySetter!");

		GridPane grid = new GridPane();
		FieldInfo fi = field.getDeclaredAnnotation(FieldInfo.class);
		if(fi==null||fi.arraypos().isEnum()==false){
			System.out.println("ERROR-Float Array Setter needs a enum to declare what is what!");
			return grid;
		}
		String[] temp = null;
		if(fi.arraypos().isEnum()){
			if(fi.arraypos()==People.class){
				temp = new String[People.values().length];
				for(int i = 0; i < People.values().length; i++){
					System.out.println(People.values()[i].name());
					temp[i] = People.values()[i].name();
				}
			}
		} else {
			temp = new String[fi.arraypos().getMethods().length];
			for(int i = 0; i < fi.arraypos().getMethods().length; i++){
				temp[i] = fi.arraypos().getMethods()[i].getName();
			}
		}
		float[] val=null;
		try {
			val = (float[]) field.get(tab);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		String[] names = temp;
		for (int i = 0; i < names.length; i++) {
			System.out.println(names[i] + " " + i);
			grid.add(new Label(names[i]), 0, i);
			NumberTextField ntf = new NumberTextField(true);
			CheckIfRequired(ntf, field, tab);
			if(val!=null){
				ntf.setText(val[i]+"");
			}
			int pos = i;
			ntf.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					try {
						float[] val = (float[]) field.get(tab);
						if(val==null){
							val = new float[names.length];
						}
						val[pos] = ntf.GetFloatValue();
						field.set(tab, val);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					
				}
			});
			grid.add(ntf,1,i);
		}
		return grid;
	}

	@SuppressWarnings("unchecked")
	private<T extends Tabable> Node CreateClassToFloatSetter(String name, Field field, Tabable t, ObservableMap<String,T> hash) {
		GridPane grid = new GridPane();
		int row=0;
		try {
			HashMap<String, Float> h = (HashMap<String, Float>) field.get(t);
			if(h == null){
				h = new HashMap<>();
			}
			hash.addListener(new MapChangeListener<String, T>(){
				@Override
				public void onChanged(
						javafx.collections.MapChangeListener.Change<? extends String, ? extends T> change) {
					if(change.getValueAdded()==null){
						return; // doin nothin for removed for now
					}
					int row = grid.getChildren().size()/2; // this is gonna be fixed in java 9
					//but for now there is no good way to get row index
					Tabable tab = change.getValueAdded();
					grid.add(new Label(tab.toString()), 0, row);
					NumberTextField ntf = new NumberTextField(true);
					CheckIfRequired(ntf, field, tab);
					ntf.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
							try {
								HashMap<String, Float> ha = (HashMap<String, Float>) field.get(t);
								if(ha == null){
									ha = new HashMap<>();
								}
								ha.put(tab.GetID(), ntf.GetFloatValue());
								field.set(t,ha);	
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					row++;
				}
				
			});
			for (Tabable tab : hash.values()) {
				grid.add(new Label(tab.toString()), 0, row);
				NumberTextField ntf = new NumberTextField(true);
				try {
					if(h.containsKey(tab.GetID()))
						ntf.setText((Float) h.get(tab.GetID())+"");
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				ntf.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						try {
							HashMap<String, Float> ha = (HashMap<String, Float>) field.get(t);
							if(ha == null){
								ha = new HashMap<>();
							}
							ha.put(tab.GetID(), ntf.GetFloatValue());
							field.set(t,ha);	
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				grid.add(ntf, 1, row);
				row++;
			}
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			e2.printStackTrace();
		}
		
		return grid;
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
		ComboBox<Enum> box = new ComboBox<Enum>(names);

		try {
			oldArray =(ArrayList<E>) field.get(tab);
		} catch (Exception e1) {
		}
		if(oldArray ==null){
			oldArray = new ArrayList<E>();
		} else {
			for(int i = 0; i<oldArray.size();i++){
				OnEnumSelect(box,listpane,field,tab,oldArray.get(i),true);
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
		grid.add(new Label(name), 0, oldArray.size()+1);	
		grid.add(box, 1, oldArray.size()+1);	
		ScrollPane sp = new ScrollPane();
		// Action on selection
		box.setOnAction(x -> {
			OnEnumSelect(box,listpane,field,tab,box.getSelectionModel().getSelectedItem(),false);
		});
	    sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(listpane);
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		grid.add(sp, 3, oldArray.size()+1);
		
		return grid;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private<E extends Enum<E>> void OnEnumSelect(ComboBox box,GridPane listpane, Field field, Tabable tab, E e, boolean setup) {
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
					if(list.size()==0){
						if(box.getStyle().contains("combobox-error")==false){
							box.getStyleClass().add("combobox-error");
						}
					}
					field.set(tab, list);
				} catch (Exception e1) {
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		listpane.add(l, 0, old.size());
		listpane.add(b, 2, old.size());
	}

	@SuppressWarnings({ "rawtypes" })
	private<T extends Tabable> Node CreateTabableSetter(String name, Field field, Tabable m, Class str, ObservableMap<String, T> obsMapTabable) {
		GridPane grid = new  GridPane();
		ObservableList<Tabable> strs = FXCollections.observableArrayList(obsMapTabable.values());
		obsMapTabable.addListener(new MapChangeListener<String, T>(){
			@Override
			public void onChanged(javafx.collections.MapChangeListener.Change<? extends String, ? extends T> change) {
				if(change.getValueAdded()!=null)
					strs.add(change.getValueAdded());
				if(change.getValueRemoved()!=null)
					strs.remove(change.getValueRemoved());
			}
			
		});
		strs.removeIf(x->x.getClass().equals(str)==false);
		ComboBox<Tabable> box = new ComboBox<Tabable>(strs);
		
		if(field.getAnnotation(FieldInfo.class)!=null){
			if(field.getAnnotation(FieldInfo.class).required()){
			    ObservableList<String> styleClass = box.getStyleClass();
			    
			    styleClass.add("combobox-error");
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {		
					String i = "";
					try {
						i =  ((String) field.get(m));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(obsMapTabable.containsKey(i) == false){
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
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);
		Button b =new Button("X");
		b.setOnAction(x->{
			box.getSelectionModel().clearSelection();
		});
		grid.add(b, 2, 0);	
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(75);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(165);
        
        grid.getColumnConstraints().addAll(col1,col2);
		try {
			if(field.get(m)!=null){
				box.getSelectionModel().select(obsMapTabable.get(field.get(m)));
			}
		} catch (Exception e1) {
		} 
		
		box.setOnAction(x-> {
				try {
					if(box.getValue()==null){
						return;
					}
					field.set(m, box.getValue().GetID());
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
			StringProperty temp = null;
			FieldInfo fi = field.getDeclaredAnnotation(FieldInfo.class);
			Node t = null;
			if(fi!=null&&fi.longtext()){
				t = new TextArea();
				((TextArea) t).setPrefRowCount(5);
				((TextArea) t).setPrefColumnCount(100);
				((TextArea) t).setWrapText(true);
				((TextArea) t).setPrefWidth(400);
				
				
				temp = ((TextArea) t).textProperty();
			} else {
				t = new TextField();				
				t.prefWidth(400);
				temp = ((TextField) t).textProperty();
			}
			StringProperty sp = temp;

			CheckIfRequired(t, field, str);
			int num = i;
			
			try {
				HashMap<String, String> h = (HashMap<String, String>) field.get(str);
				if(h != null){
					sp.set(h.get(langes.get(i).toString()));
				}
			} catch (Exception e1) {

			} 
			
			sp.addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					try {
						HashMap<String,String> h = (HashMap<String, String>) field.get(str);
						if(h == null){
							h = new HashMap<>();
						}
						h.put(langes.get(num).toString(), sp.get());
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
		GUI.Instance.idToItem.addListener(new MapChangeListener<String,ItemXML>(){
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends ItemXML> change) {
				if(change.getValueAdded()==null){
					return;
				}
				Item i = new Item(change.getValueAdded());
				its.add(i);
			}
		});
		
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
		if(field.getAnnotation(FieldInfo.class)!=null){
			if(field.getAnnotation(FieldInfo.class).required()){
			    ObservableList<String> styleClass = box.getStyleClass();
			    
			    styleClass.add("combobox-error");
				box.valueProperty().addListener((arg0, oldValue, newValue) -> {		
					Item[] i = null;
					try {
						i = (Item[]) field.get(m);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(i==null||i.length==0){
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
		Integer rows = 0;
		try {
			old = (Item[]) field.get(m); 
			Method method = listpane.getClass().getDeclaredMethod("getNumberOfRows");
			method.setAccessible(true);
			rows = (Integer) method.invoke(listpane);
		} catch (Exception e1) {
//			System.out.println(e1);
		}
		//if null we start at pos 1 else insert at length+1
		int pos = 1;
		if(old != null){
			pos = old.length+1;
			for (int i = 0; i < old.length; i++) {
				if(old[i].GetID().equals(select.GetID()) ){
					if(setup== false){
						return;
					}
					pos = i;
				}
			}
		}
		// Name of Item
		Label l = new Label(select.toString());
		//Amount field
		NumberTextField count = new NumberTextField(3);
		CheckIfRequired(count, field, m);

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
				field.set(m, removeElementFromArray(array, remove - 1));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		try {
			if(setup==false){
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
			}
 		} catch (Exception e) {
			e.printStackTrace();
		}
		listpane.add(l, 0, rows);
		listpane.add(count, 1, rows);
		listpane.add(b, 2, rows);
	}
	private<T extends Tabable> void OnArrayClassSelect(GridPane listpane, Field field, Tabable m, T select,boolean setup){
		//get existing field if null or not
		String[] old = null;
		Integer rows = 0;
		try {
			old = (String[]) field.get(m); 
			Method method = listpane.getClass().getDeclaredMethod("getNumberOfRows");
			method.setAccessible(true);
			rows = (Integer) method.invoke(listpane);
		} catch (Exception e1) {
			System.out.println(e1);
		}
		//if null we start at pos 1 else insert at length+1
		int pos = 1;
		if(old != null){
			pos = old.length+1;
			for (int i = 0; i < old.length; i++) {
				if( old[i].equals(select.GetID()) ){
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
		int remove = pos;
		b.setOnAction(s -> {
			try {
				// get array
				int[] array = (int[]) field.get(m);
				// remove the label and button
				listpane.getChildren().removeAll(l, b);
				ObservableList<Node> children = FXCollections.observableArrayList(listpane.getChildren());
				listpane.getChildren().clear();
				for (int i = 0; i < children.size(); i += 2) {
					listpane.add(children.get(i), 0, i);
					listpane.add(children.get(i + 1), 1, i);
				}
				// remove this value and set the array in class
				field.set(m, removeElementFromArray(array, remove - 1));
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
			}
 		} catch (Exception e) {
			e.printStackTrace();
		}
		listpane.add(l, 0, rows);
		listpane.add(b, 2, rows);
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
		CheckIfRequired(box, field, str);

		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		try {
			if(field.get(str)!=null){
				box.setText(field.get(str).toString());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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
		CheckIfRequired(box, field, str);
		// NEEDED FOR POPULATIONSLEVEL!
        FieldInfo info = field.getAnnotation(FieldInfo.class);
        if(info != null && info.id() && newTabable){
			try {
				System.out.println("CURRENTLY NOT SUPPORTED");
//				field.setInt(str, GUI.Instance.getOneHigherThanMaxID(str));
			} catch (Exception e) {
				e.printStackTrace();
			} 
        }

		try {
			if(field.get(str)!=null){
				box.setText((Integer) field.get(str)+"");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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
	public GridPane CreateLongStringSetter(String name, Field field, Tabable str){
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);
		TextArea box = new TextArea();
		
		
		box.setPrefRowCount(10);
		box.setPrefColumnCount(1000);
		box.setWrapText(true);
		box.setPrefSize( Double.MAX_VALUE, Double.MAX_VALUE );		
		CheckIfRequired(box, field, str);

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
	public GridPane CreateStringSetter(String name, Field field, Tabable str){
		GridPane grid = new  GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(75);
        grid.getColumnConstraints().addAll(col1,col2);
		
		TextField box = new TextField();
		CheckIfRequired(box, field, str);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public<E extends Enum<E>> GridPane CreateEnumTwoDimensionalArraySetter(String name, Field field, Tabable m, Class<E>  enumClass) {
		FieldInfo fi = field.getAnnotation(FieldInfo.class);
		Field firstField;
		Field secondField;
		int first = 0;
		int second = 0;
		
		try {
			firstField = m.getClass().getField(fi.First2DName());
			secondField = m.getClass().getField(fi.Second2DName());
			first = firstField.getInt(m);
			second = secondField.getInt(m);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(enumClass)) {
			  names.add(e);
		}
		GridPane grid = new GridPane();
		if(enumClass == TileType.class) {
			UpdateTileTypeArrayPane = grid;
		}
		
		grid.add(new Label(name), 0, 0);	
		for (int x = 0; x < first; x++) {
			for (int y = 0; y < second; y++) {
				int dx = x;
				int dy = y;
				ComboBox<Enum> box = new ComboBox<Enum>(names);
				box.setMaxWidth(Double.MAX_VALUE);
				grid.add(box, 1+x, 1+y);
				try {
					if(field.get(m)!=null){
						
						box.getSelectionModel().select(((E[][])field.get(m))[dx][dy]);
						
					}
				} catch (Exception e1) {
				} 
				int tf = first;
				int ts = second;
				box.setOnAction(de-> {
					try {
						try {
							if(field.get(m)==null) {
								field.set(m,(E[][])Array.newInstance(enumClass, tf, ts));
							}
						} catch (Exception e) {
							e.printStackTrace();
						} 
						E[][] d2a = (E[][]) field.get(m);
						d2a[dx][dy] = (E) box.getValue();
						field.set(m, d2a);
						
						for(int i=0; i<d2a.length; i++) {
					        for(int j=0; j<d2a[i].length; j++) {
					            if(d2a[i][j]==EnumSet.allOf(enumClass).toArray()[0])
									field.set(m, null);
					        }
					    }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			);
			}
		}
//		ColumnConstraints col1 = new ColumnConstraints();
//        col1.setMinWidth(150);
//        ColumnConstraints col2 = new ColumnConstraints();
//        col2.setMinWidth(100);
//        col2.setHgrow(Priority.ALWAYS);
//        grid.getColumnConstraints().addAll(col1,col2);
			
		
		return grid;
	}
	
	@SuppressWarnings("unchecked")
	public<T> T[] removeElementFromArray(T[] a, int del) {
		if(a.length-1==0){
			return (T[])Array.newInstance(a.getClass().getComponentType(), 0);
		}
		T[] newA = (T[])Array.newInstance(a.getClass().getComponentType(), a.length-1);
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
	public int[] removeElementFromArray(int[] a, int del) {
		if(a.length-1==0){
			return (int[])Array.newInstance(a.getClass().getComponentType(), 0);
		}
		int[] newA = (int[])Array.newInstance(a.getClass().getComponentType(), a.length-1);
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
	private void CheckIfRequired(Node text,Field field,Tabable t){
        FieldInfo info = field.getAnnotation(FieldInfo.class);
        if(info == null || info.required() == false){
        	return;
        }
	    ObservableList<String> styleClass = text.getStyleClass();
	    
	    styleClass.add("text-field-error");
		StringProperty temp = null;
	    if(text instanceof TextField){
	    	temp = ((TextField) text).textProperty();
	    }
	    else if(text instanceof TextArea){
	    	temp = ((TextArea) text).textProperty();
	    }
	    StringProperty sp = temp;
	    sp.addListener((arg0, oldValue, newValue) -> {		
			if(info.id()){
				if(text instanceof NumberTextField){
					Tabable exist = GUI.Instance.doesIDexistForTabable(((NumberTextField) text).GetIntValue(), t);
					if(exist!=null && exist!=t){
						if(!styleClass.contains("text-field-warning")) {
			    	        styleClass.add("text-field-warning");
			    	    }
					} else {
						if(styleClass.contains("text-field-warning")) {
			    	        styleClass.remove("text-field-warning");
			    	    }
					}
				} else if(text instanceof TextField){
					String id = ((TextField) text).textProperty().getValueSafe();
					//remove punctuations
					if(id.matches("(?s).*[\\p{Punct}\\s&&[^_]]+.*")){
						System.out.println("Char not allowed.");
						Alert a = new Alert(AlertType.INFORMATION, "IDs should be simple and contain no duplicates for all types.", ButtonType.OK);
						a.setTitle("Charakter not allowed.");
						a.setHeaderText("ID cannot contain Punctuation, Whitespaces or Special Charakters.");
						a.show();
					}
					id = id.replaceAll("[\\p{Punct}\\s&&[^_]]+", "");
					((TextField) text).textProperty().set(id);
					Tabable exist = GUI.Instance.doesIDexistForTabable(id, t);
					if(exist!=null && exist!=t){
						if(!styleClass.contains("text-field-warning")) {
			    	        styleClass.add("text-field-warning");
			    	    }
					} else {
						if(styleClass.contains("text-field-warning")) {
			    	        styleClass.remove("text-field-warning");
			    	    }
					}
				} else {
					System.out.println("Non integer&string ids are not supported atm!");
				}
			}
			if( sp.getValue().isEmpty()){
	    	    if(!styleClass.contains("text-field-error")) {
	    	        styleClass.add("text-field-error");
	    	    }
	        } else
			if(text instanceof NumberTextField){
				if(((NumberTextField) text).isFloat()==false){
					if(((NumberTextField) text).GetIntValue()>=0){
						if(styleClass.contains("text-field-error")) {
			    	        styleClass.remove("text-field-error");
			    	    }
					}
				} else {
					if(((NumberTextField) text).GetFloatValue()>=0){
						if(styleClass.contains("text-field-error")) {
			    	        styleClass.remove("text-field-error");
			    	    }
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

	public Tabable getTabable() {
		return myTabable;
	}
	public void UpdateFields() {
		if(UpdateTileTypeArrayPane!= null) {
			try {
				int num = otherGrid.getChildren().indexOf(UpdateTileTypeArrayPane);
				otherGrid.getChildren().remove(UpdateTileTypeArrayPane);
				Field f = myTabable.getClass().getField("buildTileTypes");
		    	otherGrid.add(CreateEnumTwoDimensionalArraySetter(f.getName(), f, myTabable, TileType.class), 0, num);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
