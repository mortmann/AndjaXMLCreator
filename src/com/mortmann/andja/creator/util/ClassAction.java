package com.mortmann.andja.creator.util;

import com.mortmann.andja.creator.GUI.Language;

@SuppressWarnings("rawtypes")
public class ClassAction {
	public enum ClassType { 
		Structure("Structure"), Unit("Unit Stuff"), Others("Other Stuff"), 
		Event("Event Stuff"), Localization("Localizations"), GameSettings("GameSettings");
		String Title;
		@Override
		public String toString() {
			return Title;
		}
		ClassType(String string) {
			Title = string;
		}
	}
	public ClassType type;
	public String Name;
	public Class Class;
	public Language language; //TODO: think of a more generic version
	public ClassAction(ClassType type, String name, Class Class) {
		super();
		this.type = type;
		Name = name;
		this.Class = Class;
	}
	public ClassAction(ClassType type, String name, Language l) {
		super();
		this.type = type;
		Name = name;
		language = l;
	}
	
}
