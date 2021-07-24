package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Road extends Structure {
	@FieldInfo(required = true, IsEffectable=true, Minimum = 0.01f, Maximum = 0.999f) @Element(required = false) public float movementCost = 0.75f;

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
