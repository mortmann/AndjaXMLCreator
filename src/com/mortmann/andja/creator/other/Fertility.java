package com.mortmann.andja.creator.other;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(name="Fertility",strict=false)
public class Fertility implements Tabable, Comparable<Tabable> {
	public enum Climate {Cold,Middle,Warm};
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;
	//YOU Cannot create array of generic enum type 
	//so to make my tab more generic changed to arraylist
	//makes no difference for xml anyway
	@FieldInfo(required=true,subType=Climate.class)@ElementList(required=false,entry="Climate")
	public ArrayList<Climate> climates;
	
	@Override
	public int compareTo(Tabable f) {
		return ID.compareTo(f.GetID());
	}
	@Override
	public String toString() {
		return Name.get(Language.English.toString());
	}
	@Override
	public String GetID() {
		return ID;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		
	}
	@Override
	public String GetButtonColor() {
		return null;
	}
}
