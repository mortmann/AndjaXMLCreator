package com.mortmann.andja.creator;

import java.util.ArrayList;
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

	public DataTab(String name, ObservableMap<Integer, T> map, TabPane tabs){
		ScrollPane sp = new ScrollPane();
		VBox v = new VBox();
		
	    flow = new FlowPane();
	    flow.setPadding(new Insets(0, 7, 15, 5));
	    flow.setVgap(3);
	    flow.setHgap(3);
	    SetUPButtons(map);
		map.addListener(new MapChangeListener<Integer, T>() {
			@Override
			public void onChanged(javafx.collections.MapChangeListener.Change<? extends Integer, ? extends T> change) {
				if(change.getValueAdded()==null){
					return; // doin nothin for removed for now
				}
				AddButton(change.getValueAdded());
				
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
	protected void AddButton(T valueAdded) {
		Button b = new Button();
		Tabable s = valueAdded;
		b.setOnAction(x->GUI.Instance.AddTab(s,new WorkTab(s).getScrollPaneContent()));
		b.setText((String) s.toString());
		b.setMinSize(100, 100);
		b.setPrefSize(100, 100);
		b.setMaxSize(100, 100);
		b.setTooltip(new Tooltip(s.toString()));
		b.setWrapText(true);
		b.setId(s.toString().toLowerCase());
//		b.setTextAlignment(TextAlignment.CENTER);
		b.setTextOverrun(OverrunStyle.ELLIPSIS);
		flow.getChildren().add(b);
	}
	private void SetUPButtons(ObservableMap<Integer,T> map) {
		for (T t : map.values()) {
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
