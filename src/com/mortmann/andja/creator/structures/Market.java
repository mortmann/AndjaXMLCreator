package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

public class Market extends OutputStructure {
	@Element public float takeOverStartGoal = 100;
	
	public Market(){
		hasHitbox = true;
		tileWidth = 4;
		tileHeight = 4;
		BuildTyp = BuildTypes.Single;
		myBuildingTyp = BuildingTyp.Blocking;
		buildingRange = 18;
		canTakeDamage = true;
	}
}
