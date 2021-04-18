package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false,name="needstructure")
public class NeedStructure extends Structure {

	
	public NeedStructure(){
		buildTyp = BuildTypes.Single;
		structureTyp =	StructureTyp.Blocking;
		hasHitbox = true;
	}

	@Override
	public String GetButtonColor() {
		return "#f0932b";
	}
}
