package com.mortmann.andja.creator.util;

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
	public String language; //TODO: think of a more generic version
	public ClassAction(ClassType type, String name, Class Class) {
		super();
		this.type = type;
		Name = name;
		this.Class = Class;
	}
	public ClassAction(ClassType type, String name, String l) {
		super();
		this.type = type;
		Name = name;
		language = l;
	}
	
}
