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
	public HashMap<String,Float> UsageAmounts;

	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;

	@FieldInfo(required=false)
	@Element(required=false)
	public Item item;
	@FieldInfo(required=false,compareType=NeedStructure[].class)
	@Element(required=false)
	public String[] structures;
	
	@FieldInfo(required=true)
	@Element(required=false)
	public int startLevel;
	@FieldInfo(required=true,subType=People.class)
	@Element(required=false)
	public int popCount;
	
	@FieldInfo(required=true,compareType=NeedGroup.class)
	@Element(required=false)
	public String group;
	
	@Override
	public String GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(t instanceof Item){
			if(item!=null && t.GetID().equals(item.GetID())){
				return this;
			}
		}
		if(t instanceof NeedStructure){
			if(structures!=null) {
				for (String id : structures) {
					if(t.GetID().equals(id)){
						return this;
					}
				}
			}
		}
		if(t instanceof NeedGroup){
			if(t.GetID().equals(group)){
				return this;
			}
		}
		if(t instanceof PopulationLevel) {
			for(String id : UsageAmounts.keySet()){
				if(t.GetID().equals(id)){
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

	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(t instanceof Item && item!=null){
			if(ID.equals(item.GetID())){
				item.ID = t.GetID();
			}
		}
		if(t instanceof NeedStructure && structures!=null){
			for(int i = 0; i<structures.length;i++ ) {
				if(ID.equals(structures[i])){
					 structures[i] = t.GetID();
				}
			}
		}
		if(t instanceof NeedGroup){
			if(ID.equals(group)){
				group = t.GetID();
			}
		}
		if(t instanceof PopulationLevel) {
			UsageAmounts.put(t.GetID(), UsageAmounts.remove(ID));
		}
	}

	@Override
	public String GetButtonColor() {
		return null;
	}

	@Override
	public int compareTo(Tabable o) {
		return ID.compareTo(o.GetID());
	}
}
