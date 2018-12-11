package com.mortmann.andja.creator.saveclasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.unitthings.*;

@Root(name="units")
public class UnitSave {
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

}
