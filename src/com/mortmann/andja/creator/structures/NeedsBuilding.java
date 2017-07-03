package com.mortmann.andja.creator.structures;

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
