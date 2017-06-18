package com.mortmann.andja.creator.other;

import org.simpleframework.xml.*;

@Root
public class Fertility {
	public enum Climate {Cold,Middle,Warm};
	@Attribute
	public int ID;
	public String name;
	public Climate[] climates;

}
