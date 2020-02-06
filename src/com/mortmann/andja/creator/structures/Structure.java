package com.mortmann.andja.creator.structures;

import java.util.Comparator;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.other.Need.People;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public abstract class Structure implements Tabable, Comparable<Tabable>  {
	public enum BuildTypes {Drag, Path, Single};
	public enum StructureTyp {Pathfinding, Blocking,Free};
	public enum Direction {None, N, E, S, W};
	public enum ExtraUI { None, Range, Upgrade, Efficiency };
	public enum ExtraBuildUI { None, Range, Efficiency };
	public enum BuildRestriktions { Land, Shore, Mountain };
	public enum TileType { NoRestriction, BuildLand, /*Ocean,*/ Shore, Cliff, Water, Dirt, Grass, Stone, Desert, Steppe, Jungle, Mountain };

	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;	

	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Description;
	@FieldInfo(required=false,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> HoverOver;
	@FieldInfo(subType=String.class) @ElementMap(key = "lang",required=false) public HashMap<String,String> Short;
	
	@Element(required=false) public boolean canBeBuild = true;
	@Element(required=false) public boolean isWalkable;
	@Element(required=false) public boolean hasHitbox;
	
	@FieldInfo(required=true, IsEffectable=true) @Element public float maxHealth;
	@FieldInfo(required=true, ignore=true, IsEffectable=true) public float CurrentHealth; //only for effects here!

	@FieldInfo(IsEffectable=true) @Element(required=false) public int structureRange = 0;
	
	@FieldInfo(required=true,subType=People.class)@Element public int populationLevel = -1;
	@FieldInfo(required=true)@Element public int populationCount = -1;
	@Element(required=false) public int structureLevel = 0;

	@FieldInfo(required=true) @Element public int tileWidth;
	@FieldInfo(required=true) @Element public int tileHeight;
	@FieldInfo(required=false, First2DName="tileWidth",Second2DName="tileHeight") @ElementArray(required=false) public TileType[][] buildTileTypes;
	
	@Element(required=false) public boolean canRotate = true;
	@Element(required=false) public boolean canBeBuildOver = false;
	@Element(required=false) public boolean canBeUpgraded = false;
	@Element(required=false) public boolean canTakeDamage = false;

	@Element(required=false) public Direction mustFrontBuildDir = Direction.None; 

	@Element(required=false) public boolean canStartBurning = false;
 
	@FieldInfo(IsEffectable=true) @Element(required=false) public int maintenanceCost;
	@Element(required=false) public int buildCost;

	@Element(required=false) public BuildTypes buildTyp =  BuildTypes.Single;
	@Element(required=false) public StructureTyp structureTyp = StructureTyp.Blocking;
	@Element(required=false) public ExtraUI extraUITyp = ExtraUI.None;
	@Element(required=false) public ExtraBuildUI extraBuildUITyp = ExtraBuildUI.None;
	
	@ElementArray(entry="Item",required=false) public Item[] buildingItems;

	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;
	
	public int compareTo(Tabable str) {
		if(Structure.class.isAssignableFrom(str.getClass())) {
			return Comparator.comparing(Structure::getClassName).thenComparing(Structure::getPopulationLevel).thenComparing(Structure::getPopulationCount).compare(this,(Structure) str);
		}
		return GetID().compareTo(str.GetID());
	}	
	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public String GetID() {
		return ID;
	}
	protected Tabable StructureDependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class){
			if(buildingItems!=null) {
				for (Item item : buildingItems) {
					if(item.GetID().equals(t.GetID())){
						return this;
					}
				}
			}
		}
		return StructureDependsOnTabable(t);
	}
	public int getPopulationLevel() {
		return populationLevel;
	}
	public int getPopulationCount() {
		return populationCount;
	}
	public String getClassName() {
		return getClass().getName();
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(t.getClass()==ItemXML.class && buildingItems!=null){
			for(int i = 0; i < buildingItems.length;i++ ) {
				if(ID.equals(buildingItems[i].GetID())){
					buildingItems[i].ID = t.GetID();
				}
			}
		}
		StructureUpdateDependables(t,ID);
	}
	public void StructureUpdateDependables(Tabable t, String ID) {

	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}
}
