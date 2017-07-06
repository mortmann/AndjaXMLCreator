package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false)
public class Road extends Structure {

	public Road(){
		tileWidth = 1;
		tileHeight = 1;
		BuildTyp = BuildTypes.Path;
		myBuildingTyp = BuildingTyp.Pathfinding;
		canBeUpgraded = true;
	}
	@Override
	public int GetID() {
		return ID;
	}
}
