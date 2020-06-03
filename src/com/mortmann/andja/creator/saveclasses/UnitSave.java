package com.mortmann.andja.creator.saveclasses;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.unitthings.*;

import javafx.collections.ObservableMap;

@Root(name="units")
public class UnitSave extends BaseSave {
	static String FileName = "units.xml";

	public UnitSave(){}
	public UnitSave(Collection<Unit> values) {
		units = new ArrayList<>();
		ships = new ArrayList<>();
		for(Unit u : values){
			if(u instanceof Ship){
				ships.add((Ship) u);
			} 
			else if( u instanceof Unit) {
				units.add(u);
			}
			
		}
	}

	@ElementList(name="units", inline=true)
	public ArrayList<Unit> units;
	@ElementList(name="ships", inline=true)
	public ArrayList<Ship> ships;
	@SuppressWarnings("unchecked")

	public ArrayList<Unit> getAllUnits() {
		ArrayList<Unit> units = new ArrayList<>();
		for (Field f : this.getClass().getFields()) {
			if(Modifier.isStatic(f.getModifiers()))
				continue;
			try {
				units.addAll((Collection<? extends Unit>) f.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return units;
	}
	public static void Load(ObservableMap<String, Unit> idToUnit) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			UnitSave e = serializer.read(UnitSave.class, Paths.get(saveFilePath, FileName).toFile());
			for (Unit u : e.getAllUnits()) {
				idToUnit.put(u.GetID(), u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public String GetSaveFileName() {
		return FileName;
	}
}
