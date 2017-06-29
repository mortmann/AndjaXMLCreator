package com.mortmann.andja.creator.other;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.Convert;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.enumconvertes.ClimateArrayListConverter;

@Root(name="Fertility",strict=false)
public class Fertility implements Tabable,Comparable<Fertility> {
	public enum Climate {Cold,Middle,Warm};
	@Attribute
	public int ID;
	
	@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;
	//YOU Cannot create array of generic enum type 
	//so to make my tab more generic changed to arraylist
	//makes no difference for xml anyway
	@Element(required=false)@Convert(ClimateArrayListConverter.class)public ArrayList<Climate> climates;
	
	@Override
	public int compareTo(Fertility arg0) {
		return Integer.compare(ID, arg0.ID);
	}
	@Override
	public String toString() {
		return Name.get(Language.English.toString());
	}
}
