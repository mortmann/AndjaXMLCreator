package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false,name="needstructure")
public class NeedStructure extends Structure {

	
	public NeedStructure(){
		BuildTyp = BuildTypes.Single;
		myStructureTyp =	StructureTyp.Blocking;
	}
	@Override
	public int GetID() {
		return ID;
	}
}
