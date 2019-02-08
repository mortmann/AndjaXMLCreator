package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI.Language;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="GameEvent")
public class GameEvent implements Comparable<GameEvent>, Tabable {
	
	public enum Target {
	    World, Player, AllUnit, Ship, LandUnit, Island, City, AllStructure, RoadStructure, NeedStructure, MilitaryStructure, HomeStructure,
	    ServiceStructure, GrowableStructure, OutputStructure, MarketStructure, WarehouseStructure, MineStructure,
	    FarmStructure, ProductionStructure
	}
	
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int ID =-1;	
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Description;

	@Element public float probability = 10;
	@Element public float minDuration = 50;
	@Element public float maxDuration = 100;
	@Element public float minRange = 50;
	@Element public float maxRange = 100;
	@FieldInfo(required=false,type=Effect[].class) @ElementArray(entry="Effect",required=false) public int[] effects;
	
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Language.English.toString());
	}

	@Override
	public int GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		for(int i = 0; i<effects.length;i++ ) {
			if(t.GetID() == effects[i])
				return this;
		}
		return null;
	}

	@Override
	public int compareTo(GameEvent ge) {
		return Integer.compare(ID, ge.ID);
	}

}
