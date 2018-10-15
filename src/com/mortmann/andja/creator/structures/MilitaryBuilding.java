package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.unitthings.*;
import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false,name="militarybuilding")
public class MilitaryBuilding extends Structure{
	public MilitaryBuilding() {
		
	}
	@FieldInfo(required=true,type=Unit[].class) @ElementArray(entry="Unit",required=true) public int[] canBeBuildUnits;
	@Element public float buildTimeModifier;
	
}
