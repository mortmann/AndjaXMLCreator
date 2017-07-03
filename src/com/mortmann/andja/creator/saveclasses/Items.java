package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.ItemXML;

@Root
public class Items {
	public Items(Collection<ItemXML> values) {
		this.items = new ArrayList<>(values);
	}
	public Items(){}
	@ElementList(name="Items", inline=true)
	public ArrayList<ItemXML> items;
	
}
