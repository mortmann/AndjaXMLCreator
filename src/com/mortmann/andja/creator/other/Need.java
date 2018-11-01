package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.structures.NeedsBuilding;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="Need")
public class Need implements Tabable {
	public enum People {Peasent,Citizen,Patrician,Nobleman}
	
	@FieldInfo(order=1,required=true)
	@Element(required=true)
	public float Peasent=0;
	@FieldInfo(order=2,required=true)
	@Element(required=true)
	public float Citizen=0;
	@FieldInfo(order=3,required=true)
	@Element(required=true)
	public float Patrician=0;
	@FieldInfo(order=4,required=true)
	@Element(required=true)
	public float Nobleman=0;
	
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int ID;
	
	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;

	@FieldInfo(required=false)
	@Element(required=false)
	public Item item;
	@FieldInfo(required=false,type=NeedsBuilding.class)
	@Element(required=false)
	public int structure;
	
	 
	@FieldInfo(required=true)
	@Element(required=false)
	public int startLevel;
	@FieldInfo(required=true,subType=People.class)
	@Element(required=false)
	public int popCount;
	
	@FieldInfo(required=true,type=NeedGroup.class)
	@Element(required=false)
	public int group;
	
	@Override
	public int GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(t instanceof Item){
			if(t.GetID() == item.ID){
				return this;
			}
		}
		if(t instanceof NeedsBuilding){
			if(t.GetID() == structure){
				return this;
			}
		}
		if(t instanceof NeedGroup){
			if(t.GetID() == group){
				return this;
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
