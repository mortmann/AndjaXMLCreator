package com.mortmann.andja.creator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.mortmann.andja.creator.other.Need;
import com.mortmann.andja.creator.other.PopulationLevel;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.unitthings.Unit;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.Utility;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class PopulationLevelThingsTab extends Tab {

	private FlowPane mainGrid;
	HashMap<String, FlowPane> idToPane = new HashMap<String, FlowPane>();
	
	public PopulationLevelThingsTab() {
		super();
		setText("Population Level Things Tab");
		ScrollPane scroll = new ScrollPane();
	    scroll.setStyle("-fx-background-color: transparent;");
	    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);    // Horizontal scroll bar
	    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);    // Vertical scroll bar
	    scroll.setFitToHeight(true);
	    scroll.setFitToWidth(true);
		mainGrid = new FlowPane();
		mainGrid.prefWidthProperty().bind(scroll.widthProperty().subtract(10)); 
		scroll.setContent(mainGrid);
		setContent(scroll);

		for (String id : GUI.Instance.idToPopulationLevel.keySet()) {
			PopulationLevel level = GUI.Instance.idToPopulationLevel.get(id);
			mainGrid.getChildren().add(Utility.wrapPaneInTitledPane(level.GetName(), CreatePane(level), true));
		}	
		List<Structure> sortedStructures = new ArrayList<>(GUI.Instance.idToStructures.values());
		Collections.sort(sortedStructures);
		for (Structure s : sortedStructures) {
			AddButton(s, GUI.Instance.idToStructures, idToPane.get(s.populationLevel +"structures"));
		}
		GUI.Instance.idToStructures.addListener(new MapChangeListener<String, Structure>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Structure> change) {
				Structure s = change.getValueAdded();
				if(change.wasAdded())
					AddButton(s, GUI.Instance.idToStructures, idToPane.get(s.populationLevel +"structures"));
			}
		});
		
		for (Need n : GUI.Instance.idToNeed.values()) {
			AddButton(n, GUI.Instance.idToNeed, idToPane.get(n.startLevel +"needs"));
		}
		GUI.Instance.idToNeed.addListener(new MapChangeListener<String, Need>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Need> change) {
				Need n = change.getValueAdded();
				if(change.wasAdded())
					AddButton(n, GUI.Instance.idToNeed, idToPane.get(n.startLevel +"needs"));
			}
		});
		
		for (Unit u : GUI.Instance.idToUnit.values()) {
			AddButton(u, GUI.Instance.idToUnit, idToPane.get(u.populationLevel +"units"));
		}
		GUI.Instance.idToUnit.addListener(new MapChangeListener<String, Unit>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Unit> change) {
				Unit u = change.getValueAdded();
				if(change.wasAdded())
					AddButton(u, GUI.Instance.idToUnit, idToPane.get(u.populationLevel +"units"));
			}
		});
		setOnClosed(x -> {
			GUI.Instance.RemoveTab(this, null);
		});
	}
	
	private Pane CreatePane(PopulationLevel level) {
		GridPane pane = new GridPane();
		FlowPane structures = new FlowPane();
		pane.add(Utility.wrapPaneInTitledPane("Structures", structures, true), 0, 0);
		idToPane.put(level.GetID()+"structures", structures);
		FlowPane needs = new FlowPane();
		pane.add(Utility.wrapPaneInTitledPane("Needs", needs, true), 0, 1);
		idToPane.put(level.GetID()+"needs", needs);
		FlowPane units = new FlowPane();
		pane.add(Utility.wrapPaneInTitledPane("Units", units, true), 0, 2);
		idToPane.put(level.GetID()+"units", units);
		return pane;
	}
	private void RemoveButton(Node valueRemoved, FlowPane flow) {
		flow.getChildren().remove(valueRemoved);
	}
	protected void AddButton(Tabable valueAdded, ObservableMap<String, ? extends Tabable> map, FlowPane flowPane) {
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
		b.setTextOverrun(OverrunStyle.ELLIPSIS);
		map.addListener(new MapChangeListener<String, Tabable>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Tabable> change) {
				if(change.wasRemoved() && change.getValueRemoved().equals(s))
					RemoveButton(b, flowPane);
			}

		});
		flowPane.getChildren().add(b);
	}

	
}
