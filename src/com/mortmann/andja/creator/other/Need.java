package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.structures.NeedStructure;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="Need")
public class Need implements Tabable {
	public enum People {Peasent,Citizen,Patrician,Nobleman}
	
	@ElementMap(key = "Level",attribute=true,required=false) 
	@FieldInfo(order = 0, required=true, subType=PopulationLevel.class)
	public HashMap<Integer,Float> UsageAmounts;
	
	@ElementMap(key = "Level",attribute=true,required=false) 
	@FieldInfo(order = 0, required=true, subType=PopulationLevel.class)
	public HashMap<String,Float> tUsageAmounts;

	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int ID;
	@Attribute(required=false) 
	public String tempID = "";	

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;

	@FieldInfo(required=false)
	@Element(required=false)
	public Item item;
	@FieldInfo(required=false,compareType=NeedStructure[].class)
	@Element(required=false)
	public int[] structures;
	
	@FieldInfo(required=false,compareType=NeedStructure[].class)
	@Element(required=false)
	public String[] tstructures;

	 
	@FieldInfo(required=true)
	@Element(required=false)
	public int startLevel;
	@FieldInfo(required=true,subType=People.class)
	@Element(required=false)
	public int popCount;
	
	@FieldInfo(required=true,compareType=NeedGroup.class)
	@Element(required=false)
	public int group;
	@FieldInfo(required=true,compareType=NeedGroup.class)
	@Element(required=false)
	public String tgroup;
	
	@Override
	public String GetID() {
		return tempID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(t instanceof Item){
			if(t.GetID() == item.GetID()){
				return this;
			}
		}
		if(t instanceof NeedStructure){
			for (String id : tstructures) {
				if(t.GetID() == id){
					return this;
				}
			}
		}
		if(t instanceof NeedGroup){
			if(t.GetID() == tgroup){
				return this;
			}
		}
		if(t instanceof PopulationLevel) {
			for(String id : tUsageAmounts.keySet()){
				if(t.GetID() == id){
					return this;
				}
			}
		}
		return null;
	}
	@Override
	public String toString() {
		return ID +":"+ Name.get(Language.English.toString());
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}
}
