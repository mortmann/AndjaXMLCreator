package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false)
public class Warehouse extends Market {
	
	public Warehouse(){
		contactRange = 6.3f;
		BuildTyp = BuildTypes.Single;
		hasHitbox = true;
		canTakeDamage = true;
		canBeUpgraded=true;
		structureRange = 18;
	}
	
	
}
