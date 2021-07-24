package com.mortmann.andja.creator.other;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.GameEvent.Target;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="Effect")
public class Effect implements Tabable, Comparable<Tabable> {
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID = "";	

	
	public enum EffectTypes { Integer, Float, Special }
	public enum EffectModifier { Additive, Multiplicative, Update, Special }
	public enum EffectUpdateChanges { None, Health }
	public enum EffectClassification { Negativ, Neutral, Positiv }
	
	@FieldInfo(order=0,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Name;
	@FieldInfo(subType=String.class,required=true,longtext=true)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> Description;

	@Element(required=false) public boolean unique;
	@FieldInfo(order = 0, required = true, RequiresEffectable = true, compareType=Tabable.class) 
	@Element(required=false) public String nameOfVariable; // what does it change
	@FieldInfo(order = 1, required = true, Minimum = -100000000, Maximum = 100000000) @Element(required=false)public float change; // how it changes the Variable?
	@ElementList(required=false,entry="Target")@FieldInfo(required=true,subType=Target.class)public ArrayList<Target> targets; // what it can target
	@Element(required=false) @FieldInfo(order = 3) public EffectTypes addType;
	@Element(required=false) @FieldInfo(order = 1) public EffectModifier modifierType;
	@Element(required=false) @FieldInfo(order = 2) public EffectUpdateChanges updateChange;
	@Element(required=false) @FieldInfo(order = 0) public EffectClassification classification;

	@Element(required=false) public String uiSpriteName;
	@Element(required=false) public String onMapSpriteName;

	@Element(required=false) public boolean canSpread;
	@Element(required=false) public float spreadProbability;
	@Element(required=false) public int spreadTileRange = 1;

	
	@Override
	public String GetName() {
		return Name == null? "Effect" : Name.get(Settings.CurrentLanguage.toString());
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
	public int compareTo(Tabable other) {
		return GetID().compareToIgnoreCase(other.GetID());
	}
	@Override
	public String toString() {
		return GetName();
	}

	@Override
	public void UpdateDependables(Tabable t, String ID) {
		
	}

	@Override
	public String GetButtonColor() {
		switch(classification) {
		case Negativ:
			return "#E12B38";
		case Neutral:
			return null;
		case Positiv:
			return "#3EB650";
		default:
			return null;
		}
	}
}
