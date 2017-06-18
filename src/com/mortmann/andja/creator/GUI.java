package com.mortmann.andja.creator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.EnumSet;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.*;
import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.structures.*;
import com.mortmann.andja.creator.structures.Structure.*;

public class GUI {
	private Stage mainWindow;
	private BorderPane mainLayout;
	private Scene scene;
	GridPane mainGrid;
	public void start(Stage primaryStage) {
		mainWindow = primaryStage;
		mainLayout = new BorderPane();
        mainGrid = new GridPane();

        Serializer serializer = new Persister(new AnnotationStrategy());
        
        File source = new File("items.xml");
        try {
			Items example = serializer.read(Items.class, source);
			if(example != null){
	        	for (ItemXML itemXML : example.items) {
					System.out.println(itemXML.EN_Name);
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        Mine m = new Mine();
        Field fld[] = m.getClass().getFields();
        
        for (int i = 0; i < fld.length; i++) {
            if(fld[i].getType() == Boolean.TYPE){
                mainGrid.add(CreateBooleanSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() == Float.TYPE) {
                mainGrid.add(CreateFloatSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() == Integer.TYPE) {
                mainGrid.add(CreateIntSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() == int[].class) {
            	System.out.println("int[].class");
            }
            else if(fld[i].getType() ==  String.class) {
                mainGrid.add(CreateStringSetter(fld[i].getName(),fld[i],m), 0, i);
            }
            else if(fld[i].getType() ==  BuildTypes.class) {
            	System.out.println("BuildTypes");
                mainGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,BuildTypes.class), 0, i);
            }
            else if(fld[i].getType() == BuildingTyp.class) {
            	System.out.println("BuildTypes");
                mainGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,BuildingTyp.class), 0, i);
            }
            else if(fld[i].getType() == Direction.class) {
            	System.out.println("BuildTypes");
                mainGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,Direction.class), 0, i);
            }
            else if(fld[i].getType() == Climate.class) {
            	System.out.println("BuildTypes");
                mainGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,Climate.class), 0, i);
            }
            else if(fld[i].getType() == Item[].class) {
            	System.out.println("Item[].class");
//                mainGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,BuildTypes.class), 0, i);
            }
            else if(fld[i].getType() == ArrayList.class) { // we´re gonna take every arraylist as list of strings
            	System.out.println("ArrayList");
//                mainGrid.add(CreateEnumSetter(fld[i].getName(),fld[i],m,BuildTypes.class), 0, i);
            } else {
                System.out.println("Variable Name is : " + fld[i].getName() +" : " + fld[i].getType() );

            }
        }                     
        mainLayout.setCenter(mainGrid);
        scene = new Scene(mainLayout,1024,720);
		mainWindow.setScene(scene);
		mainWindow.show();
	}

	public GridPane CreateBooleanSetter(String name, Field field, Structure str){
		GridPane grid = new  GridPane();
		CheckBox box = new CheckBox(name);
		box.setOnAction(x-> {
				try {
					field.setBoolean(str, box.isSelected());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		
		grid.add(box, 0, 0);	
		
		return grid;
	}
	public GridPane CreateFloatSetter(String name, Field field, Structure str){
		GridPane grid = new  GridPane();
		TextField box = new TextField();
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		try {
			field.setFloat(str, 0.3f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}
	public GridPane CreateIntSetter(String name, Field field, Structure str){
		GridPane grid = new  GridPane();
		TextField box = new TextField();
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		try {
			field.setInt(str, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}

	public GridPane CreateStringSetter(String name, Field field, Structure str){
		GridPane grid = new  GridPane();
		TextField box = new TextField();
		box.setOnAction(x-> {
			try {
				field.set(str, box.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		try {
			field.set(str, "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}
	
	@SuppressWarnings("rawtypes")
	public<E extends Enum<E>> GridPane CreateEnumSetter(String name, Field field, Structure str, Class<E>  en){
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(en)) {
			  names.add(e);
		}
		GridPane grid = new  GridPane();
		ComboBox<Enum> box = new ComboBox<Enum>(names);
		grid.add(new Label(name), 0, 0);	
		grid.add(box, 1, 0);	
		box.setOnAction(x-> {
				try {
					field.set(str, box.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);
		return grid;
	}
}
