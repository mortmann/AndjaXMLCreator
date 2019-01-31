package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;


@Root(name="Item",strict=false)
public class ItemXML extends Item implements Tabable {

//	@Element public String EN_Name;
//	@Element public String DE_Name;

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;
	@Element(required=false) public ItemType type;
	@Element public int decays;
	
	@Override
	public String toString() {
		return ID +":"+ GetName();
	}
	@Override
	public int GetID() {
		return ID;
	}
	public ItemXML(ItemXML i) {
		super(i);
	}
	public ItemXML() {
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
