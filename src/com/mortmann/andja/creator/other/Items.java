package com.mortmann.andja.creator.other;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Items {
	@ElementList(name="Items", inline=true)
	public ArrayList<ItemXML> items;

}
