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
public class DamageType implements Tabable {
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;	

	@FieldInfo(order=0,required=true,subType=String.class) @ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;
	@FieldInfo(order=0,required=true,subType=ArmorType.class) 
	@ElementMap(key = "ArmorTyp",attribute=true,required=false) 
	public HashMap<String, Float> damageMultiplier;
	
	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;
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
		if(damageMultiplier!=null && t.getClass() == ArmorType.class && damageMultiplier.containsKey(t.GetID()))
			return this;
		return null;
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
		if(damageMultiplier!=null && t.getClass() == ArmorType.class && damageMultiplier.containsKey(t.GetID()))
			damageMultiplier.put(t.GetID(), damageMultiplier.remove(ID));
	}
}
