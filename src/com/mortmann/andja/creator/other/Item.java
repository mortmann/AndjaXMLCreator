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

	@Element(required=false)
	public int count;
	
	public Item(ItemXML i) {
		ID = i.ID;
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
		return ItemType.Build;
	}
 }
