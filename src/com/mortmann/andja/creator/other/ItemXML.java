package com.mortmann.andja.creator.other;

import java.util.ArrayList;

import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;


@Root(name="Item",strict=false)
public class ItemXML extends Item {
	@Element public String EN_Name;
	@Element public String DE_Name;

	@ElementList(required=false) public ArrayList<String> languages;
	@Element(required=false)@Convert(EnumConverter.class) public ItemType Type;
	@Element public int Decays;

}
