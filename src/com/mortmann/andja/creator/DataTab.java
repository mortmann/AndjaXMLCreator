package com.mortmann.andja.creator;

import java.util.ArrayList;
import java.util.HashMap;

import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.util.NotClosableTab;
import com.mortmann.andja.creator.util.Tabable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DataTab {
	private ArrayList<Node> allNodeList;

	public DataTab(String name, HashMap<Integer,? extends Tabable> map, TabPane tabs){
		ScrollPane sp = new ScrollPane();
		VBox v = new VBox();
		
	    FlowPane flow = new FlowPane();
	    flow.setPadding(new Insets(0, 7, 15, 5));
	    flow.setVgap(3);
	    flow.setHgap(3);
		for (Tabable t : map.values()) {
			
			Button b = new Button();
			Tabable s = t;
			b.setOnAction(x->GUI.Instance.AddTab(s,new WorkTab(s).getScrollPaneContent()));
			b.setText((String) s.toString());
			b.setMinSize(100, 100);
			b.setPrefSize(100, 100);
			b.setMaxSize(100, 100);
			b.setTooltip(new Tooltip(s.toString()));
			b.setWrapText(true);
			b.setId(s.toString().toLowerCase());
//			b.setTextAlignment(TextAlignment.CENTER);
			b.setTextOverrun(OverrunStyle.ELLIPSIS);
			flow.getChildren().add(b);
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
		allNodeList = new ArrayList<>(flow.getChildren());
		sp.setContent(flow);
		sp.setMaxHeight(Double.MAX_VALUE);
		sp.setMaxWidth(Double.MAX_VALUE);
	    flow.setPrefWrapLength(310); // preferred width allows for two columns
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
				ns.removeIf(x->x.getId().contains(search.getText().toLowerCase())==false);
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
}
