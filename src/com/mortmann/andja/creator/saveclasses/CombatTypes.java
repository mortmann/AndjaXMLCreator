package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;

import com.mortmann.andja.creator.unitthings.ArmorType;
import com.mortmann.andja.creator.unitthings.DamageType;

public class CombatTypes {
	public CombatTypes(Collection<ArmorType> values, Collection<DamageType> values2) {
		armorTypes = new ArrayList<>(values);
		damageTypes = new ArrayList<>(values2);
	}
	public CombatTypes() {
	}
	@ElementList(name="ArmorTypes", inline=true,required=false)
	public ArrayList<ArmorType> armorTypes;

	@ElementList(name="DamageTypes", inline=true,required=false)
	public ArrayList<DamageType> damageTypes;
}
