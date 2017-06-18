package com.mortmann.andja.creator.structures;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Item;

@Root
public abstract class Structure {
	public enum BuildTypes {Drag, Path, Single};
	public enum BuildingTyp {Pathfinding, Blocking,Free};
	public enum Direction {None, N, E, S, W};
	
	@Attribute
	int ID;
	
	@ElementList public ArrayList<String> Name;
	@ElementList public ArrayList<String> Description;
	@ElementList public ArrayList<String> HoverOver;
	@ElementList public ArrayList<String> Short;
	
	
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

	@Element(required=false) public Direction mustFrontBuildDir= Direction.None; 

	@Element(required=false) public boolean canStartBurning;
	@Element(required=false) public boolean mustBeBuildOnShore = false;
	@Element(required=false) public boolean mustBeBuildOnMountain = false;

	@Element(required=false) public int maintenancecost;
	@Element(required=false) public int buildcost;

	@Element(required=false) public BuildTypes BuildTyp;
	@Element(required=false) public BuildingTyp myBuildingTyp = BuildingTyp.Blocking;
	@ElementArray public Item[] buildingItems;

	@Element(required=false) public String spriteBaseName;
	
}
