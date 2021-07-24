package com.mortmann.andja.creator.other;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Item implements Comparable<Tabable> {
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;	

	public enum ItemType {Build,Intermediate,Luxury,Military}
	@FieldInfo(ignore = true) public ItemType type = ItemType.Build; //not to save here just for sorting and other in program use
	@FieldInfo(ignore = true)
	@Element(required=false)
	public int count;
	
	public Item(ItemXML i) {
		ID = i.ID;
		type = i.getType();
	}
	public Item() {
	}
	@Override
	public String toString() {
		return GUI.Instance.idToItem.get(ID).toString();
	}
	@Override
	public int compareTo(Tabable i) {
		return ID.compareTo(i.GetID());
	}
	public String GetID() {
		return ID;
	}
	public ItemType getType() {
		return type;
	}
	public String getColor() {
		switch (type) {
		case Build:
			return "#3EB650";
		case Intermediate:
			return "#95afc0";
		case Luxury:
			return "#FCC133";
		case Military:
			return "#E12B38";
		default:
			return null;
		}
	}
	public static String getButtonColor(ItemType type) {
		switch (type) {
		case Build:
			return "#3EB650";
		case Intermediate:
			return "#95afc0";
		case Luxury:
			return "#FCC133";
		case Military:
			return "#E12B38";
		default:
			return null;
		}
	}
 }
