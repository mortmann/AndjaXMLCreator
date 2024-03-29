package com.mortmann.andja.creator.other;

import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.structures.Growable;
import com.mortmann.andja.creator.structures.Home;
import com.mortmann.andja.creator.structures.Market;
import com.mortmann.andja.creator.structures.MilitaryStructure;
import com.mortmann.andja.creator.structures.Mine;
import com.mortmann.andja.creator.structures.NeedStructure;
import com.mortmann.andja.creator.structures.OutputStructure;
import com.mortmann.andja.creator.structures.Production;
import com.mortmann.andja.creator.structures.Road;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.structures.Warehouse;
import com.mortmann.andja.creator.unitthings.Ship;
import com.mortmann.andja.creator.unitthings.Unit;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
@Root(strict=false,name="GameEvent")
public class GameEvent implements Comparable<Tabable>, Tabable {
    public enum EventType { Weather, City, Structure, Quest, Disaster, Other }
	public enum ShadowType { CompleteClear, Clear, Few, Medium, High, VeryHigh, Full }
	public enum Speed { Slow, Medium, Fast, LudicrousSpeed, MopsGeschwindigkeit /*wir haben einen marderschaden*/ }

	public enum Target {
	    World, Player, AllUnit, Ship, LandUnit, Island, City, 
	    AllStructure, DamageableStructure, BurnableStructure,
	    RoadStructure, NeedStructure, MilitaryStructure, HomeStructure,
	    ServiceStructure, GrowableStructure, OutputStructure, MarketStructure, 
	    WarehouseStructure, MineStructure, FarmStructure, ProductionStructure
	}
	@SuppressWarnings("rawtypes")
	@FieldInfo(ignore = true) public static final ObservableList<Class> targetClasses = FXCollections.observableArrayList(
			Structure.class, Growable.class, Home.class, Market.class, MilitaryStructure.class, Mine.class,NeedStructure.class,
			OutputStructure.class,Production.class,Road.class,Warehouse.class,
			Unit.class, Ship.class
	);
	@FieldInfo(ignore = true) public static final ObservableList<Target> specialTargetRangeClasses = FXCollections.observableArrayList(
			Target.AllStructure, Target.AllUnit
	);
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;	
	@Element(required = false)
	@FieldInfo(order=0,required=true,id=true)
	public EventType type;	

	
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(key = "lang",attribute=true) 
	public HashMap<String,String> Description;
	@FieldInfo(required=false, mainType=Target.class, subType=String.class)
	@ElementMap(required=false, key = "target",entry="range",attribute=true) 
	public HashMap<Target, String[]> specialRange;

	@Element @FieldInfo(order = 0) public float probability = 10;
	@Element @FieldInfo(order = 1) public float minDuration = 50;
	@Element @FieldInfo(order = 2) public float maxDuration = 100;
	@Element @FieldInfo(order = 3) public float minRange = 50;
	@Element @FieldInfo(order = 4) public float maxRange = 100;
	
	@FieldInfo(required=false,compareType=Effect[].class) 
	@ElementArray(entry="Effect",required=false) 
	public String[] effects;

	@Element(required = false) public ShadowType cloudCoverage;
	@Element(required = false) public Speed cloudSpeed;
	@Element(required = false) public Speed oceanSpeed;

	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return ID + " : " + getClass().getSimpleName();
		}
		return Name.get(Settings.CurrentLanguage.toString());
	}

	@Override
	public String GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(effects != null) {
			for(int i = 0; i<effects.length;i++ ) {
				if(t.GetID().equals(effects[i]))
					return this;
			}
		}
		return null;
	}

	@Override
	public int compareTo(Tabable ge) {
		return ID.compareTo(ge.GetID());
	}
	@Override
	public String toString() {
		return GetName();
	}

	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(effects != null) {
			for(int i = 0; i<effects.length;i++ ) {
				if(ID.equals(effects[i]))
					effects[i] = t.GetID();
			}
		}
		
	}

	@Override
	public String GetButtonColor() {
		return null;
	}
}
