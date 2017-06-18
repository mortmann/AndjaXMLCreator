package com.mortmann.andja.creator.other;

import org.simpleframework.xml.*;

public class Item {
	public enum ItemType {Build,Intermediate,Luxury}

	@Attribute
	public int ID;
	@Element(required=false)
	public int count;

 }
