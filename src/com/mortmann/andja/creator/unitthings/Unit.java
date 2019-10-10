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
	public String ID;	

	@FieldInfo(required=true) public float buildTime = 1f;

	@Element public int populationLevel = 0;
	@Element public int populationCount = 0;
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(required = false, key = "lang",attribute=true) public HashMap<String,String> Description;

	@ElementArray(entry="Item",required=false) public Item[] buildingItems;
	@Element(required=false) public int buildcost;
	
	@FieldInfo(required = true, IsEffectable=true) @Element public int inventoryPlaces;
	@FieldInfo(required = true, IsEffectable=true) @Element public int inventorySize;
	@FieldInfo(required=true, IsEffectable=true) @Element(required=false) public int maintenancecost;
	@FieldInfo(required=true, IsEffectable=true) @Element public float maximumHealth;
	@FieldInfo(required=true, IsEffectable=true) @Element float aggroTimer=1f;
	@FieldInfo(required=true, IsEffectable=true) @Element public float attackRange=1f;
	@FieldInfo(required=true, IsEffectable=true) @Element public float damage=10;
	@FieldInfo(order = 0,compareType=DamageType.class) @Element public String myDamageType;
	@FieldInfo(order = 0,compareType=ArmorType.class) @Element public String myArmorType;
	@FieldInfo(required=true, IsEffectable=true) @Element public float attackRate=1;
	@FieldInfo(required=true, IsEffectable=true) @Element public float speed;   // Tiles per second
	@FieldInfo(required=true, IsEffectable=true) @Element public float turnSpeed;   // Tiles per second
	@FieldInfo(required=true, IsEffectable=true) @Element(required=false) public float aggroTime;   

	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;

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
		if(myArmorType.equals(t.GetID())&&t.getClass()==ArmorType.class){
			return this;
		}
		if(myDamageType.equals(t.GetID())&&t.getClass()==DamageType.class){
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
		return Name.get(Language.English.toString());
	}
	@Override
	public int compareTo(Unit u) {
		return ID.compareTo(u.ID);
	}

	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(myArmorType.equals(ID)&&t.getClass()==ArmorType.class){
			myArmorType = t.GetID();
		}
		if(myDamageType.equals(ID)&&t.getClass()==DamageType.class){
			myDamageType = t.GetID();
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
}
