package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="NeedGroup")
public class NeedGroup implements Tabable {
	
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;

	@Element
	@FieldInfo(order=0,required=true)
	public float importanceLevel;

	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Settings.CurrentLanguage.toString());
	}
	@Override
	public String GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		
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
