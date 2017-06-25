package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

public class Mine extends OutputStructure{
	@Element public String myRessource;
	
	public Mine(){
		mustBeBuildOnMountain = true;
		tileWidth = 2;
		tileHeight = 3;
		myBuildingTyp = BuildingTyp.Blocking;
		BuildTyp = BuildTypes.Single;
		hasHitbox = true;
		buildingRange = 0;
	}
	
}
