package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.unitthings.*;
import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false,name="militarystructure")
public class MilitaryStructure extends Structure{
	public MilitaryStructure() {
		
	}
	@FieldInfo(required=true,type=Unit[].class) @ElementArray(entry="Unit",required=true) public int[] canBeBuildUnits;
	@FieldInfo(required = true, IsEffectable=true)@Element public float buildTimeModifier;
	
}
