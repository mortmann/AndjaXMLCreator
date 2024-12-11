package com.mortmann.andja.creator.unitthings;

import java.util.Comparator;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.other.PopulationLevel;
import com.mortmann.andja.creator.other.Item.ItemType;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Unit implements Tabable, Comparable<Tabable> {
	
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;	

	@FieldInfo(required=true) public float buildTime = 1f;

	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(required = false, key = "lang",attribute=true) public HashMap<String,String> Description;
	
	@Element @FieldInfo(subType=PopulationLevel.class) public int populationLevel = 0;
	@Element public int populationCount = 0;
	@ElementArray(entry="Item",required=false)@FieldInfo(ComperatorMethod = "SortBuildItem") public Item[] buildingItems;
	@Element(required=false) public int buildCost;
	@FieldInfo(required=true, IsEffectable=true) @Element(required=false) public int upkeepCost;
	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;

	@FieldInfo(required = true, IsEffectable=true) @Element public int inventoryPlaces;
	@FieldInfo(required = true, IsEffectable=true) @Element public int inventorySize;
	@FieldInfo(required=true, IsEffectable=true) @Element public float maxHealth;
	@FieldInfo(required=true, IsEffectable=true) @Element float aggroTimer=1f;
	@FieldInfo(required=true, IsEffectable=true) @Element public float attackRange=1f;
	@FieldInfo(required=true, IsEffectable=true) @Element public float damage=10;
	@FieldInfo(order = 0,compareType=DamageType.class) @Element public String damageType;
	@FieldInfo(order = 0,compareType=ArmorType.class) @Element public String armorType;
	@FieldInfo(required=true, IsEffectable=true) @Element public float attackRate=1;
	@FieldInfo(required=true, IsEffectable=true) @Element(required=false) public float buildRange=1;
	@FieldInfo(required=true, IsEffectable=true) @Element public float speed;   // Tiles per second
	@FieldInfo(required=true, IsEffectable=true) @Element public float turnSpeed;   // Tiles per second
	@FieldInfo(required=true, IsEffectable=true) @Element(required=false) public float aggroTime;   
	@FieldInfo(required=true, IsEffectable=true) @Element(required=false) public float projectileSpeed = 0; // only needed for things with projectiles
	@Element(required=false) public boolean canBeBuild = true;


	public Unit(){
		
	}
	
	@Override
	public String GetID() {
		return ID;
	}
	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(armorType.equals(t.GetID())&&t.getClass()==ArmorType.class){
			return this;
		}
		if(damageType.equals(t.GetID())&&t.getClass()==DamageType.class){
			return this;
		}
		if(t.getClass()==ItemXML.class&&buildingItems!=null){
			for (Item item : buildingItems) {
				if(item.GetID().equals(t.GetID())){
					return this;
				}
			}
		}
		
		return UnitDependsOnTabable(t);
	}
	public Tabable UnitDependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			System.out.println("Name is empty!");
			return getClass().getSimpleName();
		}
		return Name.get(Settings.CurrentLanguage.toString());
	}
	@Override
	public int compareTo(Tabable o) {
		if(Unit.class.isAssignableFrom(o.getClass()))
			return Comparator.comparing(Unit::getClassName).thenComparing(Unit::getPopulationLevel).thenComparing(Unit::getPopulationCount).compare(this,(Unit) o);
		return GetID().compareTo(o.GetID());
	}

	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(armorType.equals(ID)&&t.getClass()==ArmorType.class){
			armorType = t.GetID();
		}
		if(damageType.equals(ID)&&t.getClass()==DamageType.class){
			damageType = t.GetID();
		}
		if(t.getClass()==ItemXML.class&&buildingItems!=null){
			for(int i = 0; i < buildingItems.length;i++ ) {
				if(ID.equals(buildingItems[i].GetID())){
					buildingItems[i].ID = t.GetID();
				}
			}
		}	
		UnitUpdateDependables(t, ID);
	}
	public void UnitUpdateDependables(Tabable t, String ID) {
		
	}

	@Override
	public String GetButtonColor() {
		return "#6ab04c";
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
	
	public Comparator<Item> SortBuildItem() {
		return new Comparator<Item>(){
			@Override
			public int compare(Item o1, Item o2) {
				if(o1.getType() == ItemType.Build && o2.getType() == ItemType.Build)
					return -o1.ID.compareTo(o2.ID);
				if(o1.getType() == ItemType.Build)
					return -1;
				if(o2.getType() == ItemType.Build)
					return 1;
				return 0;
			}
		};
	}
}
