package com.mortmann.andja.creator.structures;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.util.BuildingTypConverter;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.enumconvertes.BuildTypesConverter;
import com.mortmann.andja.creator.util.enumconvertes.DirectionConverter;

@Root
public abstract class Structure implements Comparable<Structure>, Tabable {
	public enum BuildTypes {Drag, Path, Single};
	public enum BuildingTyp {Pathfinding, Blocking,Free};
	public enum Direction {None, N, E, S, W};
	
	@Attribute
	@FieldInfo(order=0,required=true)
	public int ID =-1;
	
	@ElementMap(attribute=true) public HashMap<String,String> Name;
	@ElementMap(attribute=true) public HashMap<String,String> Description;
	@ElementMap(attribute=true) public HashMap<String,String> HoverOver;
	@ElementList(required=false) public HashMap<String,String> Short;
	
	
	@Element(required=false) public boolean isWalkable;
	@Element(required=false) public boolean hasHitbox;
	@Element(required=false) public boolean isActive;
	@Element public float MaxHealth;

	@Element(required=false) public int buildingRange = 0;
	@Element public int PopulationLevel = 0;
	@Element public int PopulationCount = 0;

	@Element(required=false) public int StructureLevel = 0;

	@Element(required=false) public int tileWidth;
	@Element(required=false) public int tileHeight;

	@Element(required=false) public boolean canRotate = true;
	@Element(required=false) public boolean canBeBuildOver = false;
	@Element(required=false) public boolean canBeUpgraded = false;
	@Element(required=false) public boolean canTakeDamage = false;
	@Element(required=false) public boolean showExtraUI = false;

	@Element(required=false)@Convert(DirectionConverter.class) public Direction mustFrontBuildDir = Direction.None; 

	@Element(required=false) public boolean canStartBurning = false;
	@Element(required=false) public boolean mustBeBuildOnShore = false;
	@Element(required=false) public boolean mustBeBuildOnMountain = false;
 
	@Element(required=false) public int maintenancecost;
	@Element(required=false) public int buildcost;

	@Element(required=false)@Convert(BuildTypesConverter.class) public BuildTypes BuildTyp =  BuildTypes.Single ;
	@Element(required=false)@Convert(BuildingTypConverter.class) public BuildingTyp myBuildingTyp = BuildingTyp.Blocking;
	
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
		return ID+":"+(String) Name.values().toArray()[0];
	}
}
