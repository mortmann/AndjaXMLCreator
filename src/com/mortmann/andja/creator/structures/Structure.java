package com.mortmann.andja.creator.structures;

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
public abstract class Structure implements Tabable, Comparable<Structure>  {
	public enum BuildTypes {Drag, Path, Single};
	public enum StructureTyp {Pathfinding, Blocking,Free};
	public enum Direction {None, N, E, S, W};
	public enum ExtraUI { None, Range, Upgrade, Efficiency };
	public enum ExtraBuildUI { None, Range, Efficiency };
	public enum BuildRestriktions { Land, Shore, Mountain };
	
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
	@FieldInfo(required=true) @Element public float maxHealth;

	@Element(required=false) public int structureRange = 0;
	
	@FieldInfo(required=true,subType=People.class)@Element public int populationLevel = -1;
	@FieldInfo(required=true)@Element public int populationCount = -1;
	@Element(required=false) public int structureLevel = 0;

	@FieldInfo(required=true) @Element public int tileWidth;
	@FieldInfo(required=true) @Element public int tileHeight;

	@Element(required=false) public boolean canRotate = true;
	@Element(required=false) public boolean canBeBuildOver = false;
	@Element(required=false) public boolean canBeUpgraded = false;
	@Element(required=false) public boolean canTakeDamage = false;

	@Element(required=false) public Direction mustFrontBuildDir = Direction.None; 

	@Element(required=false) public boolean canStartBurning = false;
 
	@Element(required=false) public int maintenanceCost;
	@Element(required=false) public int buildCost;

	@Element(required=false) public BuildTypes buildTyp =  BuildTypes.Single;
	@Element(required=false) public StructureTyp myStructureTyp = StructureTyp.Blocking;
	@Element(required=false) public ExtraUI extraUITyp = ExtraUI.None;
	@Element(required=false) public ExtraBuildUI extraBuildUITyp = ExtraBuildUI.None;
	@Element(required=false) public BuildRestriktions hasToBuildOnRestriktion;
	
	@ElementArray(entry="Item",required=false) public Item[] buildingItems;

	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;
	
	public int compareTo(Structure str) {
		int val = Integer.compare(populationLevel, str.populationLevel); // first order after Level
		if(val == 0){
			return Integer.compare(populationCount, str.populationCount); // if its the same then order count
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
