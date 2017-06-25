package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

public class Farm extends OutputStructure {
	@Element public Growable growable;
	public Farm(){
		myBuildingTyp = BuildingTyp.Blocking;
		BuildTyp = BuildTypes.Single;
		hasHitbox = true;
		this.canTakeDamage = true;
	}
}
