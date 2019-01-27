package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="PopulationLevel")
public class PopulationLevel implements Tabable {
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int Level =-1;	
	
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Description;

	//List of Dependend Need Group will be calculated inside of the Prototype Controller
	
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}

	@Override
	public int GetID() {
		return Level;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}

}