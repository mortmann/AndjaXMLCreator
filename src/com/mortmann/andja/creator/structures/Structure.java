package com.mortmann.andja.creator.structures;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.other.Need.People;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.convertes.BuildTypesConverter;
import com.mortmann.andja.creator.util.convertes.BuildingTypConverter;
import com.mortmann.andja.creator.util.convertes.DirectionConverter;
import com.mortmann.andja.creator.util.convertes.ExtraBuildUIConverter;
import com.mortmann.andja.creator.util.convertes.ExtraUIConverter;

@Root(strict=false)
public abstract class Structure implements Tabable, Comparable<Structure>  {
	public enum BuildTypes {Drag, Path, Single};
	public enum BuildingTyp {Pathfinding, Blocking,Free};
	public enum Direction {None, N, E, S, W};
	public enum ExtraUI { None, ContactRange, Efficiency };
	public enum ExtraBuildUI { None, Range, Efficiency };
	
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int ID =-1;	
	
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Description;
	@FieldInfo(required=true,subType=String.class)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> HoverOver;
	@FieldInfo(subType=String.class) @ElementMap(key = "lang",required=false) public HashMap<String,String> Short;
	
	@Element(required=false) public boolean canBeBuild = true;
	@Element(required=false) public boolean isWalkable;
	@Element(required=false) public boolean hasHitbox;
	@FieldInfo(required=true) @Element public float MaxHealth;

	@Element(required=false) public int buildingRange = 0;
	@FieldInfo(required=true,subType=People.class)@Element public int PopulationLevel = -1;
	@FieldInfo(required=true)@Element public int PopulationCount = -1;

	@Element(required=false) public int StructureLevel = 0;

	@FieldInfo(required=true) @Element public int tileWidth;
	@FieldInfo(required=true) @Element public int tileHeight;

	@Element(required=false) public boolean canRotate = true;
	@Element(required=false) public boolean canBeBuildOver = false;
	@Element(required=false) public boolean canBeUpgraded = false;
	@Element(required=false) public boolean canTakeDamage = false;

	@Element(required=false)@Convert(DirectionConverter.class) public Direction mustFrontBuildDir = Direction.None; 

	@Element(required=false) public boolean canStartBurning = false;
	@Element(required=false) public boolean mustBeBuildOnShore = false;
	@Element(required=false) public boolean mustBeBuildOnMountain = false;
 
	@Element(required=false) public int maintenancecost;
	@Element(required=false) public int buildcost;

	@Element(required=false)@Convert(BuildTypesConverter.class) public BuildTypes BuildTyp =  BuildTypes.Single;
	@Element(required=false)@Convert(BuildingTypConverter.class) public BuildingTyp myBuildingTyp = BuildingTyp.Blocking;
	@Element(required=false)@Convert(ExtraUIConverter.class) public ExtraUI ExtraUITyp = ExtraUI.None;
	@Element(required=false)@Convert(ExtraBuildUIConverter.class) public ExtraBuildUI ExtraBuildUITyp = ExtraBuildUI.None;
	@ElementArray(entry="Item",required=false) public Item[] buildingItems;

	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;
	
	public int compareTo(Structure str) {
		int val = Integer.compare(PopulationLevel, str.PopulationLevel); // first order after Level
		if(val == 0){
			return Integer.compare(PopulationCount, str.PopulationCount); // if its the same then order count
		}
		return val;
	}	
	@Override
	public String toString() {
		return ID+":"+ GetName();
	}
	@Override
	public int GetID() {
		return ID;
	}
	protected Tabable StructureDependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class){
			for (Item item : buildingItems) {
				if(item.ID==t.GetID()){
					return this;
				}
			}
		}
		return null;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return StructureDependsOnTabable(t);
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}
}
