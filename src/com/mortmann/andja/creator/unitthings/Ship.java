package com.mortmann.andja.creator.unitthings;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Ship extends Unit {
	@FieldInfo(required=true, IsEffectable=true) @Element public int maximumAmountOfCannons = 0;
	@FieldInfo(required=true, IsEffectable=true) @Element public int damagePerCannon = 1;
	@FieldInfo(required=true) @Element public Item cannonType; // WARNING: this has to be filled when maximumAmountOfCannons > 0
	
	@Override
	public Tabable UnitDependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class&&cannonType!=null){
			if(cannonType.GetID().equals(t.GetID())){
				return this;
			}
		}
		return null;
	}
	@Override
	public void UnitUpdateDependables(Tabable t, String ID) {
		if(t.getClass()==ItemXML.class&&cannonType!=null){
			if(cannonType.GetID().equals(ID)){
				cannonType.ID = t.GetID();
			}
		}
	}
}
