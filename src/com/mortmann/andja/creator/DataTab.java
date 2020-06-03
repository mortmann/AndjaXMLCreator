package com.mortmann.andja.creator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.mortmann.andja.creator.util.NotClosableTab;
import com.mortmann.andja.creator.util.Tabable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DataTab<T extends Tabable> {
	private ArrayList<Node> allNodeList;
	private FlowPane flow;
	private HashMap<T,Button> tabToButton;
	public DataTab(String name, ObservableMap<String, T> map, TabPane tabs){
		ScrollPane sp = new ScrollPane();
		VBox v = new VBox();
		tabToButton = new HashMap<>();
	    flow = new FlowPane();
	    flow.setPadding(new Insets(2, 2, 2, 2));
	    flow.setVgap(3);
	    flow.setHgap(3);
	    SetUPButtons(map);
		map.addListener(new MapChangeListener<String, T>() {
			@Override
			public void onChanged(javafx.collections.MapChangeListener.Change<? extends String, ? extends T> change) {
				if(change.getValueAdded()==null){
					RemoveButton(change.getValueRemoved());
					return; // doin nothin for removed for now
				}
				AddButton(change.getValueAdded());
				SetUPButtons(map);
			}

		});
		allNodeList = new ArrayList<>(flow.getChildren());
		sp.setContent(flow);
		sp.setMaxHeight(Double.MAX_VALUE);
		sp.setMaxWidth(Double.MAX_VALUE);
		sp.hbarPolicyProperty().set(ScrollBarPolicy.NEVER);
	    flow.setPrefWrapLength(310); // preferred width allows for two columns
	    flow.prefWrapLengthProperty().bind(v.widthProperty());
	    TextField search = new TextField("");
	    search.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(search.getText().isEmpty()||search.getText().trim().isEmpty()){
					flow.getChildren().clear();
					flow.getChildren().addAll(allNodeList);
					return;
				}
				ObservableList<Node> ns = FXCollections.observableArrayList(allNodeList);
				ns.removeIf(x->x.getId().toLowerCase().contains(search.getText().toLowerCase())==false);
				flow.getChildren().clear();
				flow.getChildren().addAll(ns);
			}
		});
	    v.getChildren().add(search);
	    v.getChildren().add(sp);
		NotClosableTab nct = new NotClosableTab(name);
		nct.setContent(v);
        tabs.getTabs().add(nct);
	}
	private void RemoveButton(T valueRemoved) {
		flow.getChildren().remove(tabToButton.get(valueRemoved));
	}
	protected void AddButton(T valueAdded) {
		Button b = new Button();
		Tabable s = valueAdded;
		
		b.setOnAction(x->{GUI.Instance.AddWorkTab(s, false);});
		b.setText((String) s.toString());
		b.setMinSize(100, 100);
		b.setPrefSize(100, 100);
		b.setMaxSize(100, 100);
		b.setTooltip(new Tooltip(s.toString()));
		b.setWrapText(true);
		String color = valueAdded.GetButtonColor();
		if(color!=null) {
			Color c = Color.decode(color);
		    double darkness = 1-(0.299* c.getRed() + 0.587*c.getGreen() + 0.114*c.getBlue())/255;
			if(darkness>0.5)
				b.setStyle("-fx-background-color: " + color +" !important" + "-fx-text-fill: white;");
			else
				b.setStyle("-fx-background-color: " + color +" !important");
		}
		
		b.setId(s.toString().toLowerCase());
//		b.setTextAlignment(TextAlignment.CENTER);
		b.setTextOverrun(OverrunStyle.ELLIPSIS);
		flow.getChildren().add(b);
		tabToButton.put(valueAdded, b);
	}
	private void SetUPButtons(ObservableMap<String, T> map) {
		flow.getChildren().clear();
		ArrayList<T> l = new ArrayList<T>(map.values());
//		FXCollections.sort(l);
		Collections.sort(l); 
//		l.sorted();
		for (T t : l) {
			AddButton(t);
		}
		
//		for (int i = 0; i < 200; i++) {
//			String s = i+"-sadasdsdasda";
//			Button b = new Button(s);
//			b.setMinSize(100, 100);
//			b.setMaxSize(100, 100);
//			b.setTooltip(new Tooltip(s));
//			b.setWrapText(true);
//			b.setId(i+"test");
//			b.setTextAlignment(TextAlignment.CENTER);
//			b.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);
//			flow.getChildren().add( b );
//		}		
	}
}
