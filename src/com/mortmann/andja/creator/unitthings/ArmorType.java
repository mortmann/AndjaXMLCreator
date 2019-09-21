package com.mortmann.andja.creator.unitthings;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;
@Root(strict=false)
public class ArmorType implements Tabable {
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;	

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false)
	public HashMap<String,String> Name;
//	@FieldInfo(order = 0,subType=DamageType.class) 
//	@ElementMap(key = "DamageType",attribute=true,required=false)
//	public HashMap<Integer,Float> damageMultiplier;
	
	@FieldInfo(order=0,required=true) @Element(required=false)
	public String spriteBaseName;
	
	@Override
	public String GetID() {
		return ID;
	}
	@Override
	public String toString() {
		return Name.get(Language.English.toString());
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}
}
