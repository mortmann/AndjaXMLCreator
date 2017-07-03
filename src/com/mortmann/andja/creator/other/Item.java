package com.mortmann.andja.creator.other;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.FieldInfo;

public class Item implements Comparable<Item>{
	@Attribute
	@FieldInfo(order=0,required=true)
	public int ID =-1;	
	
	public enum ItemType {Build,Intermediate,Luxury}

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
	public int compareTo(Item arg0) {
		return Integer.compare(ID, arg0.ID);
	}
	
 }
