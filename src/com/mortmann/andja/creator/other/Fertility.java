package com.mortmann.andja.creator.other;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.util.Tabable;

@Root
public class Fertility implements Tabable {
	public enum Climate {Cold,Middle,Warm};
	@Attribute
	public int ID;
	public String name;
	public Climate[] climates;

}
