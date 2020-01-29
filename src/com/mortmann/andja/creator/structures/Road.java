package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Root;

@Root(strict=false)
public class Road extends Structure {

	public Road(){
		tileWidth = 1;
		tileHeight = 1;
		buildTyp = BuildTypes.Path;
		structureTyp = StructureTyp.Pathfinding;
		canBeUpgraded = true;
	}

	@Override
	public String GetButtonColor() {
		return "#95afc0";
	}
}
