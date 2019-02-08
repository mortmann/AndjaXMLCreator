package com.mortmann.andja.creator.other;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.GameEvent.Target;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="Effect")
public class Effect implements Tabable, Comparable<Effect> {
	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public int ID =-1;	
	
	public enum EffectTypes { Integer, Float, Special }
	public enum EffectModifier { Additive, Multiplicative, Special }

	@FieldInfo(order = 0, required = true, RequiresEffectable = true) @Element(required=false) public String nameOfVariable; // what does it change
	@FieldInfo(order = 1, required = true) @Element(required=false)public float change; // how it changes the Variable?
	@Element(required=false) public Target[] targets; // what it can target
	@Element(required=false) public EffectTypes addType;
	@Element(required=false) public EffectModifier modifierType;
	@Element(required=false) public boolean unique;
	
	@Override
	public String GetName() {
		return targets + " => " + nameOfVariable + " -> " + change;
	}

	@Override
	public int GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}

	@Override
	public int compareTo(Effect other) {
		return Integer.compare(GetID(), other.GetID());
	}

}
