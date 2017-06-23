package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.Convert;

import com.mortmann.andja.creator.util.Tabable;


@Root(name="Item",strict=false)
public class ItemXML extends Item implements Tabable {
	@Element public String EN_Name;
	@Element public String DE_Name;

	@ElementList(required=false) public HashMap<String,String> languages;
	@Element(required=false)@Convert(EnumConverter.class) public ItemType Type;
	@Element public int Decays;
	
	@Override
	public String toString() {
		return ID +" : "+ EN_Name;
	}
}
