package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false)
public class NeedsBuilding extends Structure {

	
	public NeedsBuilding(){
		BuildTyp = BuildTypes.Single;
		myBuildingTyp =	BuildingTyp.Blocking;
	}
	@Override
	public int GetID() {
		return ID;
	}
}
