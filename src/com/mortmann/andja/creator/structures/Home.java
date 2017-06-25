package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

public class Home extends Structure {
	@Element public int maxLivingSpaces;
	@Element public float increaseSpeed;
	@Element public float decreaseSpeed;
	
	public Home(){
		tileWidth = 2;
		tileHeight = 2;
		BuildTyp = BuildTypes.Drag;
		myBuildingTyp =	BuildingTyp.Blocking;
		buildingRange = 0;
		hasHitbox = true;
		canTakeDamage = true;
		maintenancecost = 0;
	}
}
