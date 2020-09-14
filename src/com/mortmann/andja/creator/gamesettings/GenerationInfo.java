package com.mortmann.andja.creator.gamesettings;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.saveclasses.BaseSave;
import com.mortmann.andja.creator.structures.Mine;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.structures.Structure.TileType;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.OrderEr;
import com.mortmann.andja.creator.util.Range;
import com.mortmann.andja.creator.util.Size;
import com.mortmann.andja.creator.util.history.ChangeHistory;
import com.mortmann.andja.creator.util.history.ChangeListenerHistory;
import com.mortmann.andja.creator.util.history.ComboBoxHistory;
import com.mortmann.andja.creator.util.history.EnumArraySetterHistory;
import com.mortmann.andja.creator.util.history.NumberTextField;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

@Root(strict=false,name="generationInfos")
@SuppressWarnings({ "rawtypes" })
public class GenerationInfo extends Tab implements GameSettings {
	@FieldInfo(required = false, subType = IslandSize.class)
	@ElementList(required = false, entry = "islandSize")
	ArrayList<IslandSize> islandSizes;
	@FieldInfo(required = false, subType = Resource.class)
	@ElementList(required = false, entry = "resource")
	ArrayList<Resource> resources;
	
	private ArrayList<Mine> mines;
	private ScrollPane scroll;
	private FlowPane IslandSizePane;
	private FlowPane ResourcesPane;

	public GenerationInfo() {
		
	}
	public GenerationInfo(ArrayList<Resource> resources, ArrayList<IslandSize> islandSizes) {
		Setup(resources, islandSizes);
	}
	private void Setup(ArrayList<Resource> loadResources, ArrayList<IslandSize> loadIslandSizes) {
		GUI.Instance.idToStructures.addListener(new MapChangeListener<String, Structure>(){
			@Override
			public void onChanged(Change<? extends String, ? extends Structure> change) {
				if(change.getValueAdded() instanceof Mine) {
					AddMineResource(((Mine)change.getValueAdded()));
				}
				if(change.getValueRemoved() instanceof Mine) {
					RemoveMineResource(((Mine)change.getValueAdded()));
				}
			}
		});
		if(loadResources == null) {
			resources = new ArrayList<Resource>();
		} else {
			resources = loadResources;
		}
		if(loadIslandSizes == null) {
			islandSizes = new ArrayList<IslandSize>();
			for(Size s : Size.values()) {
				islandSizes.add(new IslandSize(s));
			}
		} else {
			islandSizes = loadIslandSizes;
		}
		mines = new ArrayList<Mine>();
		//dumbest thing ever -- why java why -- is there no easy way to do this?
		ArrayList<Mine> temps = new ArrayList<Mine>();
		temps.addAll(Arrays.asList(GUI.Instance.getStructureList(Mine.class).toArray(new Mine[1])));
		ResourcesPane = new FlowPane();
		TitledPane ResourcesPaneTP = new TitledPane("Island Sizes", ResourcesPane);
		ResourcesPaneTP.setExpanded(true);
		ResourcesPaneTP.setMaxHeight(Double.MAX_VALUE);

		ResourcesPane.setPrefWidth(1000);
		for(Mine mine : temps) {
			AddMineResource(mine);
		}
		for(Resource resource : resources) {
			ResourcesPane.getChildren().add(AddResource(resource));
		}
		
		GridPane grid = new GridPane();
		grid.setGridLinesVisible(true);
		scroll = new ScrollPane();
		scroll.setContent(grid);
		setText("GenerationInfo");
		setContent(scroll);

		IslandSizePane = new FlowPane();
		TitledPane IslandSizePaneTP = new TitledPane("Island Sizes", IslandSizePane);
        IslandSizePaneTP.setExpanded(true);
        IslandSizePaneTP.setMaxHeight(Double.MAX_VALUE);

		for(IslandSize size : islandSizes) {
			IslandSizePane.getChildren().add(AddIslandSize(size));
		}
		grid.add(IslandSizePaneTP, 0, 0);
		grid.add(ResourcesPaneTP, 0, 1);
		
		setOnCloseRequest(ac->{
			if(ChangeHistory.IsSaved(this)){
				return;
			}
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning!");
			String s = "Any unsaved data will be lost!";
			alert.setContentText(s);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				
			} else {
				ac.consume();
			}
		});
		setOnClosed(ac->{
			GUI.Instance.RemoveTab(this,this);
		});

	}
	
	private Node AddIslandSize(IslandSize size) {
		GridPane pane = new GridPane();
        TitledPane btp = new TitledPane(size.size.toString(), pane);
        btp.setExpanded(true);
        btp.setCollapsible(false);
        btp.setMaxHeight(Double.MAX_VALUE);

        Class c = IslandSize.class;
		Field fld[] = c.getFields();
		Arrays.sort(fld,new OrderEr());
		for (int i = 0; i < fld.length; i++) {
        	Field field = fld[i];
            FieldInfo info = field.getAnnotation(FieldInfo.class);
            if(info!=null && info.ignore())
            	continue;
        	Class compare = field.getType();
            if(info!=null&&info.compareType().equals(void.class)==false){
        		compare = info.compareType();
        	}
            if(compare == Range.class) {
            	pane.add(CreateRangeSetter(field.getName(), field, size, false), 0, i);
            }
        }
        return btp;
	}
	@SuppressWarnings("unchecked")
	private Node CreateHashMapEnumRangeSetter(String name, Field field, Object object, Class compare) {
		GridPane pane = new GridPane();
        TitledPane btp = new TitledPane(name, pane);
        btp.setExpanded(true);
        btp.setCollapsible(false);
        btp.setMaxHeight(Double.MAX_VALUE);
        HashMap<Size,Range> value = null;
		try {
			value = (HashMap<Size, Range>) field.get(object);
		} catch (Exception e) {
		} 
		Size[] sizes = Size.values();
		for (int i = 0; i < sizes.length; i++) {	
			if(value.containsKey(sizes[i]) == false)
				value.put(sizes[i], new Range());
			pane.add(CreateRangeSetter(sizes[i].name(), field, value.get(sizes[i]), true), 0, i);
		}

		return btp;
	}

	@SuppressWarnings("unchecked")
	private Node AddResource(Resource resource) {
		GridPane pane = new GridPane();
        TitledPane btp = new TitledPane(resource.ID, pane);
        btp.setExpanded(true);
        btp.setMaxHeight(Double.MAX_VALUE);

        Class c = Resource.class;
		Field fld[] = c.getFields();
		Arrays.sort(fld,new OrderEr());
        for (int i = 0; i < fld.length; i++) {
        	Field field = fld[i];
            FieldInfo info = field.getAnnotation(FieldInfo.class);
            if(info!=null && info.ignore())
            	continue;
        	Class compare = field.getType();
            if(info!=null&&info.compareType().equals(void.class)==false){
        		compare = info.compareType();
        	}
            if(compare == Range.class) {
            	CreateRangeSetter(field.getName(), field, resource, false);
            }
            if (compare == ArrayList.class) {
				if (info == null) {
					System.out.println(
							"[ERROR] This type " + field.getName() + " of field needs a fieldinfo-subtype declared!");
					continue;
				}
				if (info.subType() == void.class) {
					System.out.println("[ERROR] This type " + field.getName() + " of field needs a subtype declared!");
					continue;
				}
//				if (info.subType() == Climate.class) {
//					pane.add(CreateEnumArraySetter(field.getName(), field, resource, Climate.class), 0, i);
//				}
				pane.add(CreateEnumArraySetter(field.getName(), field, resource, info.subType()), 0, i);
            } 
            else if (compare.isEnum()) {
				// This is for all enums makes it way easier in the future to create new ones
				// and removes need to add smth here
            	pane.add(CreateEnumSetter(field.getName(), field, resource, compare), 0, i);
			} 
            else if(compare == HashMap.class) {
            	if(info.subType() == Range.class && info.fixed()) {
            		pane.add(CreateHashMapEnumRangeSetter(field.getName(), field, resource, compare), 0, i);
            	}
            }
        }

        return btp;
	}
	private void RemoveMineResource(Mine mine) {
		mines.remove(mine);
		for(Item item : mine.output) {
			//JUST for checking if any other mine is creating the item as output
			if(mines.stream().anyMatch(x->Arrays.asList(x.output).stream().anyMatch(y->y.ID.contentEquals(item.ID)))){
				//IF it is -- just leave it in
				continue;
			}
			//if not -- remove it!
			resources.removeIf(x->x.ID == item.ID);
		}
	}
	private void AddMineResource(Mine mine) {
		if(mines.contains(mine))
			return;
		for(Item item : mine.output) {
			if(resources.stream().anyMatch(x->x.ID.contentEquals(item.ID)))
				continue;
			//if not -- add it!
			resources.add(new Resource(item.ID));
		}	
		mines.add(mine);
	}
	@SuppressWarnings({ "unchecked" })
	private<E extends Enum<E>> Node CreateEnumArraySetter(String name, Field field,Object object, Class<E> class1) {
		return new EnumArraySetterHistory(name, field, object, class1);
	}
	private Node CreateRangeSetter(String name, Field field, Object object, boolean objectIsRange) {
		Range range = null;
		if(objectIsRange == false) {
			try {
				range = (Range) field.get(object);
				if(range == null) {
					range = new Range();
				}
				field.set(object,range);
			} catch (Exception e) {
			} 
		} else {
			range = (Range) object;
		}
		
		Range finalRange = range;
		ColumnConstraints col = new ColumnConstraints();
		col.setMinWidth(50);
		col.setHgrow(Priority.ALWAYS);
		GridPane grid = new GridPane();
		grid.getColumnConstraints().addAll(col, col, col);

		grid.add(new Label(name), 0, 0);
		grid.add(new Label("Lower:"), 0, 1);
		NumberTextField lower = new NumberTextField(range.lower+"");
		lower.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object old, Object changed, boolean newChange) {
				finalRange.lower = lower.GetIntValue();
			}
		}, true);
		grid.add(lower, 1, 1);
		grid.add(new Label("Upper:"), 2, 1);
		NumberTextField upper = new NumberTextField(range.upper+"");
		upper.AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object old, Object changed, boolean newChange) {
				finalRange.upper = upper.GetIntValue();
			}
		}, true);
		grid.add(upper, 3, 1);
		return grid;
	}

	public <E extends Enum<E>> GridPane CreateEnumSetter(String name, Field field, Object m, Class<E> class1) {
		ObservableList<Enum> names = FXCollections.observableArrayList();
		for (Enum e : EnumSet.allOf(class1)) {
			names.add(e);
		}
		GridPane grid = new GridPane();

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setMinWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setMinWidth(100);
		col2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col1, col2);
		ComboBoxHistory<Enum> box = new ComboBoxHistory<Enum>(names);
		
		box.setMaxWidth(Double.MAX_VALUE);
		grid.add(new Label(name), 0, 0);
		grid.add(box, 1, 0);

		try {
			if (field.get(m) != null) {
				box.getSelectionModel().select((Enum) field.get(m));
			}
		} catch (Exception e1) {
		}

		box.setOnAction(x -> {
			try {
				field.set(m, box.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return grid;
	}
	public static GenerationInfo Load() {
		Serializer serializer = new Persister(new AnnotationStrategy());
		String filename ="mapgeneration.xml";
		GenerationInfo tab = new GenerationInfo();
		try {
			GenerationInfo temp = serializer.read(tab, Paths.get(BaseSave.saveFilePath, filename).toFile());
			tab = new GenerationInfo(temp.resources, temp.islandSizes);
		} catch (Exception e1) {
			e1.printStackTrace();
			tab = new GenerationInfo(null,null);
		}    
		return tab;
	}
	public boolean Save(){
		return BaseSave.Save("mapgeneration.xml", this);
	}
}
