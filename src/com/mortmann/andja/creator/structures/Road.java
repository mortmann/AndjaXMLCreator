package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false)
public class Road extends Structure {

	public Road(){
		tileWidth = 1;
		tileHeight = 1;
		buildTyp = BuildTypes.Path;
		myStructureTyp = StructureTyp.Pathfinding;
		canBeUpgraded = true;
	}
	@Override
	public String GetID() {
		return tempID;
	}
}
