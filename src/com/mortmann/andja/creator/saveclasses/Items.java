package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.ItemXML;

@Root
public class Items {
	@ElementList(name="Items", inline=true)
	public ArrayList<ItemXML> items;
	
	public Items(Collection<ItemXML> values) {
		this.items = new ArrayList<>(values);
		Collections.sort(items);
	}
	public Items(){}

	
}
