package com.mortmann.andja.creator.other;

import java.util.ArrayList;

import org.simpleframework.xml.*;
@Root(name="Item")
public class ItemXML extends Item {
	@Element public String name;
	@ElementList public ArrayList<String> languages;
	@Element public ItemType Type;
}
