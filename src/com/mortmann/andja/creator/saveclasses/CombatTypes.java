package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.unitthings.ArmorType;
import com.mortmann.andja.creator.unitthings.DamageType;

import javafx.collections.ObservableMap;

@Root
public class CombatTypes extends BaseSave {
	static String FileName = "combat.xml";

	
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

	@Override
	public String GetSaveFileName() {
		return "combat.xml";
	}

	public static void Load(ObservableMap<String, ArmorType> idToArmorType, ObservableMap<String, DamageType> idToDamageType) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			CombatTypes e = serializer.read(CombatTypes.class, Paths.get(saveFilePath, FileName).toFile());
			if(e.damageTypes!=null)
				for (DamageType u : e.damageTypes) {
					idToDamageType.put(u.GetID(), u);
				}
			if(e.armorTypes!=null)
				for (ArmorType u : e.armorTypes) {
					idToArmorType.put(u.GetID(), u);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
