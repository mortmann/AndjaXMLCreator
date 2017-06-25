package com.mortmann.andja.creator.structures;

public class Warehouse extends Market {
	
	public Warehouse(){
		contactRange = 6.3f;
		mustBeBuildOnShore = true;
		BuildTyp = BuildTypes.Single;
		showExtraUI = true;
		hasHitbox = true;
		canTakeDamage = true;
		buildingRange = 18;
	}
	
	
}
