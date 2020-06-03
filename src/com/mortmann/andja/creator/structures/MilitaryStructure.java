package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.unitthings.*;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="militarystructure")
public class MilitaryStructure extends Structure{
	public MilitaryStructure() {
		
	}
	@FieldInfo(required=true,compareType=Unit[].class) @ElementArray(entry="Unit",required=false) public String[] canBeBuildUnits;

	@FieldInfo(required = true, IsEffectable=true)@Element(required=false) public float buildTimeModifier;
	@FieldInfo(required = true, IsEffectable=true)@Element(required=false) public int buildQueueLength = 1;
	@FieldInfo(required = false, IsEffectable=true)@Element(required=false) public DamageType damageType;
	@FieldInfo(required = false, IsEffectable=true)@Element(required=false) public int damage;
	@Override
	public Tabable StructureDependsOnTabable(Tabable t) {
		if(t.getClass().isAssignableFrom(Unit.class)){
			for(int i = 0; i < canBeBuildUnits.length;i++ ) {
				if(t.GetID().equals(canBeBuildUnits[i])){
					return this;
				}
			}
		}
		return null;
	}
    @Override
    public void StructureUpdateDependables(Tabable t, String ID) {
    	if(t.getClass().isAssignableFrom(Unit.class)){
			for(int i = 0; i < canBeBuildUnits.length;i++ ) {
				if(ID.equals(canBeBuildUnits[i])){
					canBeBuildUnits[i] = t.GetID();
				}
			}
		}
	}
	@Override
	public String GetButtonColor() {
		return "#eb4d4b";
	}
	
}
