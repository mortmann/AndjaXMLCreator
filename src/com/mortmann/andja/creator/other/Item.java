package com.mortmann.andja.creator.other;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.structures.Structure;

public class Item implements Comparable<Item>{
	
	public enum ItemType {Build,Intermediate,Luxury}

	@Attribute
	public int ID;
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
