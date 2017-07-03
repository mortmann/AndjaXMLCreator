package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.Convert;

import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.convertes.ItemTypeConverter;


@Root(name="Item",strict=false)
public class ItemXML extends Item implements Tabable {

//	@Element public String EN_Name;
//	@Element public String DE_Name;

	@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;
	@Element(required=false)@Convert(ItemTypeConverter.class) public ItemType Type;
	@Element public int Decays;
	
	@Override
	public String toString() {
		return ID +":"+ Name.get("English");
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
}
