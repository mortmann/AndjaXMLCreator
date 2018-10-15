package com.mortmann.andja.creator.unitthings;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Unit implements Tabable, Comparable<Unit> {
	
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int ID =-1;	
	
	@FieldInfo(required=true) @Element public float MaxHealth;
	@FieldInfo(required=true) public float buildTime = 1f;

	@Element public int PopulationLevel = 0;
	@Element public int PopulationCount = 0;
	@FieldInfo(required=true) @Element public int inventoryPlaces;
	@FieldInfo(required=true) @Element public int inventorySize;
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;

	@Element(required=false) public int maintenancecost;
	@Element(required=false) public int buildcost;

	@ElementArray(entry="Item",required=false) public Item[] buildingItems;
	@FieldInfo(required=true) @Element float aggroTimer=1f;
	@FieldInfo(required=true) @Element public float attackRange=1f;
	@FieldInfo(required=true) @Element public float damage=10;
	@FieldInfo(order = 0,type=DamageType.class) @Element public int myDamageType;
	@FieldInfo(order = 0,type=ArmorType.class) @Element public int myArmorType;
	@FieldInfo(required=true) @Element public float attackCooldown=1;
	@FieldInfo(required=true) @Element public float attackRate=1;
	@FieldInfo(required=true) @Element public float speed;   // Tiles per second
	@FieldInfo(required=true) @Element public float turnSpeed;   // Tiles per second
	
	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;

	public Unit(){
		
	}
	
	@Override
	public int GetID() {
		return ID;
	}
	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(myArmorType==t.GetID()&&t.getClass()==ArmorType.class){
			return this;
		}
		if(myDamageType==t.GetID()&&t.getClass()==DamageType.class){
			return this;
		}
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
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			System.out.println("Name is empty!");
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}
	@Override
	public int compareTo(Unit u) {
		return Integer.compare(ID, u.ID);
	}
	
}
